package com.dawan.particleswarmapp.estimatonpack;

import java.util.Random;

// Добавить макс и мин значения для коэффициентов

public class Point {
    double x, y;
    double xVel = 0.0, yVel = 0.0;
    double xBestLocal =.0, yBestLocal =.0, fBestLocal = .0;
    double functionValue=.0;
    double Cin;
    double Ccog;
    double Csoc;

    public Point() {
    }

    public Point(double x, double y, Random U) {
        this.x = x;
        this.y = y;
        xBestLocal = x;
        yBestLocal = y;
        xVel = U.nextDouble();
        yVel = U.nextDouble();
        Cin = U.nextDouble();
        Ccog = U.nextDouble();
        Csoc = U.nextDouble();
    }

    public double[] updatePosition(double xGlobalBest, double yGlobalBest) {
        xVel = Cin*xVel + Ccog*(xBestLocal - x) + Csoc*(xGlobalBest - x);
        yVel = Cin*yVel + Ccog*(yBestLocal - y) + Csoc*(yGlobalBest - y);

        x = x + xVel;
        y = y + yVel;

        return new double[] {x, y};
    }

    public void setFunctionValue(double fv) {
        this.functionValue = fv;
        if (fv < fBestLocal) {
            fBestLocal = fv;
            xBestLocal = x;
            yBestLocal = y;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getFv() {
        return functionValue;
    }
}
