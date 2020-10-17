package com.dawan.particleswarmapp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.dawan.particleswarmapp.estimatonpack.Estimator;
import com.dawan.particleswarmapp.estimatonpack.EstimatorResult;
import com.dawan.particleswarmapp.estimatonpack.Point;

import static com.dawan.particleswarmapp.MainActivity.CALC_AT_ONCE;
import static com.dawan.particleswarmapp.MainActivity.CALC_AUTO;
import static com.dawan.particleswarmapp.MainActivity.CALC_CLEAR;
import static com.dawan.particleswarmapp.MainActivity.CALC_FINISHED;
import static com.dawan.particleswarmapp.MainActivity.CALC_STEP;

public class SeparateThread extends HandlerThread
{
    private Handler calcHandler;
    Estimator estimator;
    EstimatorResult estimationResult;
    long fullTime = 0L;

    String formula = "(x-2)*(x-2)+(y-3)*(y-3)";
    int maxIter = 70;
    int[] pointsGrid = {5, 4};
    public double[][] constraints = {{U.minX, U.maxX},{U.minY, U.maxY}};

    public SeparateThread(String name) {
        super(name);
        init();
    }

    void init() {
        estimator = new Estimator(pointsGrid, maxIter, formula, constraints);
        estimationResult = new EstimatorResult(maxIter, estimator.getPointsNumber());
        saveStepResults(0,
                estimator.getPoints(),
                estimator.getGlobalBestId(),
                0);
    }

    public void postTask(int codice, Handler extHandler) {
        if (codice == CALC_CLEAR) {
            calcHandler.post(() -> {
                init();
                fullTime = 0L;
                extHandler.sendMessage(U.messageWith(CALC_CLEAR));
            });
        }
        else if (codice == CALC_STEP) {
            calcHandler.post(() -> {
                int response = CALC_STEP;                           // At once init response to set in Message
                response |= calculateStep(true);
                extHandler.sendMessage(U.messageWith(response, estimator.getStep()));
            });
        }
        else if (codice == CALC_AUTO) {
            calcHandler.postDelayed(() -> {
                int response = CALC_STEP | CALC_AUTO;                           // At once init response to set in Message
                response |= calculateStep(true);
                extHandler.sendMessage(U.messageWith(response, estimator.getStep()));
            }, U.sleepTimeMs);
        }
        else if (codice == CALC_AT_ONCE) {
            calcHandler.post(() -> {
                int response = CALC_STEP;
                long time1 = U.getTimeMs();
                while (!U.has(calculateStep(false), CALC_FINISHED)) { }              // Calculate until the finish
                long time2 = U.getTimeMs();
                long time = time2 - time1;

                Message msg = U.messageWith(response | CALC_FINISHED | CALC_AT_ONCE, estimator.getStep());
                msg.arg2 = (int)time;
                extHandler.sendMessage(msg);
            });
        }
    }

    public int calculateStep(boolean measureTime) {
        Estimator.Answer ans;                               //
        long time1, time2;

        time1 = measureTime ? U.getTimeMs() : 0L;  //
        ans = estimator.next();                             // Do a new step and measure the computation time
        time2 = measureTime ? U.getTimeMs() : 0L;  //

        if (ans == null) {
            return CALC_FINISHED;
        } else {
            int step = estimator.getStep();
            long time = time2 - time1;

            // Record data
            saveStepResults(step,
                    estimator.getPoints(),
                    ans.id,
                    time);

            // Compile the feedback to the main thread
            ans.time = time;                                        // Count step time
            fullTime += ans.time;                                   // ----- full ----
            ans.step = step;                                        // Record step

            return  estimator.isFinished() ? CALC_FINISHED : 0;
        }
    }

    public void prepareHandler(){
        calcHandler = new Handler(getLooper());
    }

    public EstimatorResult getEstimationResult() {
        return estimationResult;
    }

    public void saveStepResults(int step, Point[] points, int id, long time) {
        estimationResult.putAnalysis(step, id, time);      // Save best point ID and computation time of step-th step

        for (int i = 0; i < points.length; i++) {           // Save states of all point at step-th step
            Point p = points[i];
            estimationResult.putPointCoordinatesAtStep(step, i,
                    p.getX(),
                    p.getY(),
                    p.getFv());
        }
    }

    public void setNewFormula(String formula) {
        this.formula = formula;
    }

    public void setMaxIterations(int maxIter) {
        this.maxIter = maxIter;
    }

    public void setMaxX(double maxX) {
        constraints[0][1] = maxX;
    }

    public void setMinX(double minX) {
        constraints[0][0] = minX;
    }

    public void setMaxY(double maxY) {
        constraints[1][1] = maxY;
    }

    public void setMinY(double minY) {
        constraints[1][0] = minY;
    }

    public void setPointsNumX(int pointsNumX) {
        pointsGrid[0] = pointsNumX;
    }

    public void setPointsNumY(int pointsNumY) {
        pointsGrid[1] = pointsNumY;
    }
}
