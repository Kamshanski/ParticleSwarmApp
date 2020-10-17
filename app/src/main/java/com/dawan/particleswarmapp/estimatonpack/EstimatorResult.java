package com.dawan.particleswarmapp.estimatonpack;

public class EstimatorResult {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int FV = 2;
    private double[][] fullResult;
    public final int stepsNum, pointNum;
    private int timeIndex;              // Index of computation time of step-th step
    private int bestPointIndex;         // Index of index of point w/ best FV on step-th step

    public EstimatorResult(int stepsNum, int pointNum) {
        this.stepsNum = stepsNum;
        this.pointNum = pointNum;
        fullResult = new double[stepsNum+1][(pointNum * 3) + 2];
        timeIndex = (pointNum * 3);
        bestPointIndex = (pointNum * 3) + 1;
    }

    public double[] getPointCoordinatesAtStep(int step, int point) {   // step={1,...,N}, point={0,...,N-1}, step[0]=initial
        if (valid(step, point)) {
            double[] pc = new double[3];                        // point coordinates {X, Y, FV}
            int pi = point * 3;                                 // point kinda index
            pc[X]  = fullResult[step][pi + X];
            pc[Y]  = fullResult[step][pi + Y];
            pc[FV] = fullResult[step][pi + FV];
            return pc;
        }
        return null;
    }

    public Estimator.Answer getBestResult(int step) {
        if (valid(step)) {
            int bpid = (int) fullResult[step][bestPointIndex];
            int bpid3 = (int) fullResult[step][bestPointIndex] * 3;    // best point ID
            double x = fullResult[step][bpid3 + X];
            double y = fullResult[step][bpid3 + Y];
            double fv = fullResult[step][bpid3 + FV];
            Estimator.Answer ans = new Estimator.Answer(x, y, fv, bpid);
            ans.time = (long) fullResult[step][timeIndex];
            return ans;
        }
        return null;
    }


    public void putPointCoordinatesAtStep(int step, int point, double x, double y, double fv) {
        if (valid(step, point)) {
            fullResult[step][point*3 + X] = x;
            fullResult[step][point*3 + Y] = y;
            fullResult[step][point*3 + FV] = fv;
        }
    }

    public void putAnalysis(int step, int bestPointId, long time) {   // Save best point ID and computation time
        if (valid(step)) {
            fullResult[step][timeIndex] = time;
            fullResult[step][bestPointIndex] = bestPointId;
        }
    }

    public boolean valid(int step) {
        return (0 <= step) && (step <= stepsNum);
    }

    public boolean valid(int step, int point) {
        return valid(step) && (0 <= point && point < pointNum);
    }


//    private Estimator.Answer accessResultSynchronically(boolean doGet, int step, Estimator.Answer singleResult) {
//        synchronized (this) {
//            if (0 <= step && step < result.length) {
//                if (doGet) {
//                    return result[step];
//                } else {
//                    if (singleResult.step == step) {
//                        result[step] = singleResult;
//                    } else {
//                        U.d("ERROR. Can't set singleResult to result 'cause length of arrays aren't the same");
//                    }
//                }
//            }
//            return null;
//        }
//    }
}
