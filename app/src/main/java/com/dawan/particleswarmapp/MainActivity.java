package com.dawan.particleswarmapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dawan.particleswarmapp.databinding.ActivityMainBinding;
import com.dawan.particleswarmapp.estimatonpack.Estimator;
import com.dawan.particleswarmapp.estimatonpack.EstimatorResult;

public class MainActivity extends AppCompatActivity {
    public static final int CALC_STEP =     1;
    public static final int CALC_FINISHED = 1 << 1;
    public static final int CALC_CLEAR =    1 << 2;
    public static final int CALC_AUTO =     1 << 3;
    public static final int CALC_MANUAL =   1 << 4;
    public static final int CALC_AT_ONCE =  1 << 5;

    public static final String TEXT_BEST_X = "X: %f";
    public static final String TEXT_BEST_Y = "Y: %f";
    public static final String TEXT_PROGRESS = "Шаг: %d";
    public static final String TEXT_ERROR = "Ошибка: %f%%";
    public static final String TEXT_FULL_TIME = "Полное время: %d ms";
    public static final String TEXT_STEP_TIME = "Время шага: %d ms";
    public static final String TEXT_POINTS_NUMBER = "Количество точек: %d";

    long fullTime = 0L;
    SeparateThread threadCalc;
    EstimatorResult estimatorResult;
    ActivityMainBinding b;
    boolean autoMode = false;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int step = msg.arg1;
            Estimator.Answer ans = null;

            if (U.has(msg.what, CALC_AT_ONCE)) {
                fullTime += msg.arg2;
            }
            if (U.has(msg.what, CALC_STEP)) {
                if (estimatorResult != null) {
                    ans = estimatorResult.getBestResult(step);
                }
                if (ans != null) {
                    showCoordinates(ans.getX(), ans.getY());
                    long stepTime = ans.getTime();
                    fullTime += stepTime;
                    showTime(stepTime, fullTime);
                    showProgress(step);
                    showPoints(step);
                } else {
                    U.d("Answer is not ANSWER!!!1!");
                }
            }
            if (U.has(msg.what, CALC_FINISHED)) {
                if (estimatorResult != null) {
                    ans = estimatorResult.getBestResult(step);
                }
                if (ans != null) {
                    b.txProgress.append(". Конец.");
                    String s = String.format("T: %d\nMinValue: %f\n[X, Y]: [%f, %f]\n",
                            fullTime,
                            ans.getFunctionValue(),
                            ans.getX(),
                            ans.getY());

                    U.d(s);
                } else {
                    U.d("It's already finished. Stop clicking, plz");
                }
                autoMode = false;
            }
            if (U.has(msg.what, CALC_CLEAR)) {
                clear();
            }
            if (U.has(msg.what, CALC_AUTO) && autoMode) {
                threadCalc.postTask(CALC_AUTO, handler);
            }

            try {
                msg.recycle(); //it can work in some situations
            } catch (IllegalStateException e) {
                handler.removeMessages(msg.what); //if recycle doesnt work we do it manually
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        threadCalc = new SeparateThread("threadCalc");
        threadCalc.start();
        threadCalc.prepareHandler();

        setListeners();

        b.functionPlane.setCustomAxes(U.maxX, U.minX, U.maxY, U.minY);
        clear();
    }

    public void clear() {
        estimatorResult = threadCalc.getEstimationResult();

        String formula = b.edtFormula.getText().toString();
        threadCalc.setNewFormula(formula);

        b.functionPlane.preparePoints(estimatorResult.pointNum);
        b.functionPlane.updateProportions();

        showCoordinates(.0, .0);
        fullTime = 0L;
        showProgress(0);
        showTime(0L, 0L);
        showPoints(0);
        showPointsNum(estimatorResult.pointNum);
    }


    @Override
    protected void onResume() {
        super.onResume();

        b.functionPlane.post(() -> {
            showPoints(0);
            b.functionPlane.findAxes();
            b.functionPlane.setActualMinimum(U.actualX, U.actualY);
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    public void taskClear(View v) {
        threadCalc.postTask(CALC_CLEAR, handler);
        View focusView = getCurrentFocus();
        if (focusView != null) {
            focusView.clearFocus();
        }
    }


    public void taskNextStep(View v) {
        threadCalc.postTask(CALC_STEP, handler);
    }

    public void taskAutoOn(View v) {
        autoMode = true;
        threadCalc.postTask(CALC_AUTO, handler);
    }

    public void taskAutoOff(View v) {
        autoMode = false;
    }

    public void taskInstantCalc(View v) {
        threadCalc.postTask(CALC_AT_ONCE, handler);
    }

    public void debug(View v) {
        U.d("do debug");
    }

    public void showCoordinates(double x, double y) {
        b.txX.setText(String.format(TEXT_BEST_X, x));
        b.txY.setText(String.format(TEXT_BEST_Y, y));
    }

    public void showTime(long st, long ft) {
        b.txFullTime.setText(String.format(TEXT_FULL_TIME, ft));
        b.txStepTime.setText(String.format(TEXT_STEP_TIME, st));
    }

    public void showPoints(int step) {
        for (int i = 0; i < estimatorResult.pointNum; i++) {
            double[] coords = estimatorResult.getPointCoordinatesAtStep(step, i);
            b.functionPlane.updatePoint(i, coords[0], coords[1]);
        }
        int bpid = estimatorResult.getBestResult(step).id;
//        functionPlane.updateBestPoint(bpid);
    }

    public void showProgress(int step) {
        b.txProgress.setText(String.format(TEXT_PROGRESS, step));
    }

    public void showPointsNum(int num) {
        b.txPointsNum.setText(String.format(TEXT_POINTS_NUMBER, num));
    }

    public void setListeners() {
        b.edtFormula.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                threadCalc.setNewFormula(s.toString());
            }
        });

        b.edtMaxIter.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                threadCalc.setMaxIterations(U.toIntSafely(s.toString(), U.maxIter));
            }
        });

        b.edtPointsNumX.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                threadCalc.setPointsNumX(U.toIntSafely(s.toString(), U.pointsGrid[0]));
            }
        });

        b.edtPointsNumY.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                threadCalc.setPointsNumY(U.toIntSafely(s.toString(), U.pointsGrid[1]));
            }
        });

        b.edtXMax.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                double num = U.toDoubleSafely(s.toString(), U.maxX);
                threadCalc.setMaxX(num);
                b.functionPlane.setMaxX(num);
            }
        });

        b.edtXMin.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                double num = U.toDoubleSafely(s.toString(), U.minX);
                threadCalc.setMinX(num);
                b.functionPlane.setMinX(num);

            }
        });

        b.edtYMax.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                double num = U.toDoubleSafely(s.toString(), U.maxY);
                threadCalc.setMaxY(num);
                b.functionPlane.setMaxY(num);
            }
        });

        b.edtYMin.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                double num = U.toDoubleSafely(s.toString(), U.minY);
                threadCalc.setMinY(num);
                b.functionPlane.setMinY(num);
            }
        });

    }
}
