package com.dawan.particleswarmapp.estimatonpack;

import java.util.Random;

/**
 * Class that implements Particle Swamp algorithm
 */
public class Estimator
{
    public static final int OX = 1;
    public static final int OY = 2;

    double globalBestX, globalBestY, globalBestFunctionValue;
    double xBestTemp, yBestTemp, fBestTemp;
    int globalBestId, idBestTemp;
    Point[] points;
    int xN, yN;
    Function f;
    double xMax, xMin, yMax, yMin;
    int maxIterations;
    int step = 0;
    Random uniformDistribution;

    public Estimator(int[] pointsGrid, int maxIterations, String function, double[][] constraints) {
        this.maxIterations = maxIterations;
        this.f = Function.getInstance(function);
        xMin = constraints[0][0];                       // init constraints
        xMax = constraints[0][1];                       //
        yMin = constraints[1][0];                       //
        yMax = constraints[1][1];                       //

        uniformDistribution = new Random();             // set only ONE uniformly distributed random digits source for estimator

        xN = pointsGrid[0];                             // Points on Ox axe
        yN = pointsGrid[1];                             // --------- Oy ---
        points = new Point[xN*yN];                      //

        createShuffledPoints();                         //

        xBestTemp = globalBestX;                        // Best point staring values
        yBestTemp = globalBestY;                        //
        fBestTemp = globalBestFunctionValue;            //
        idBestTemp = globalBestId;
    }

    /**
     * Compute a step
     * @return best step result
     */
    public Answer next() {
        if (step <= maxIterations) {
            if (fBestTemp < globalBestFunctionValue) {
                globalBestFunctionValue = fBestTemp;
                globalBestX = xBestTemp;
                globalBestY = yBestTemp;
                globalBestId = idBestTemp;
            }

            for (int i = 0; i < points.length; ++i) {
                Point p = points[i];
                updatePositionSafely(p);
                double funcVal = f.of(p.x, p.y);
                p.setFunctionValue(funcVal);
                if (funcVal < fBestTemp) {
                    fBestTemp = funcVal;
                    xBestTemp = p.x;
                    yBestTemp = p.y;
                    idBestTemp = i;
                }
            }

            ++step;

            return new Answer(globalBestX, globalBestY, globalBestFunctionValue, globalBestId);
        }
        return null;
    }

    /**
     * Put points in a grid inside constraints
     */
    public void createShuffledPoints() {
        double dx = (xMax - xMin) / (xN + 1);
        double dy = (yMax - yMin) / (yN + 1);

        for (int i = 0; i < xN; ++i) {
            double x = xMin + dx*(double)(i+1);
            int xi = i*yN;
            for (int j = 0; j < yN; ++j) {
                double y = yMin + dy*(double)(j+1);
                Point p = new Point(x, y, uniformDistribution);
                double J = f.of(p.x, p.y);
                p.fBestLocal = J;
                if (J < globalBestFunctionValue || xi+j == 0) {
                    globalBestFunctionValue = J;
                    globalBestX = x;
                    globalBestY = y;
                    globalBestId = xi+j;
                }
                points[xi+j] = p;
            }
        }
    }

    /**
     * Keeps points inside constraints. (Not working now)
     */
    private void updatePositionSafely(Point p) {
        double xOld = p.x;
        double yOld = p.y;
        double[] coordinates = p.updatePosition(globalBestX, globalBestY);
//        double xUpd = coordinates[0];
//        double yUpd = coordinates[1];
//
//        boolean lessThanXBound = xUpd < xMin;
//        boolean biggerThanXBound = xUpd > xMin;
//        boolean lessThanYBound = yUpd < yMin;
//        boolean biggerThanYBound = yUpd > yMin;
//
//        boolean outOfXBounds = lessThanXBound || biggerThanXBound;
//        boolean outOfYBounds = lessThanYBound || biggerThanYBound;
//
//        double q;
//        double[] intersection;
//        if (!(outOfXBounds || outOfYBounds)) {
//            return;
//        } else if (outOfXBounds && outOfYBounds) {
//            q = lessThanXBound ? xMin : xMax;
//            double[] interOx = getIntersectionWith(xOld, yOld, xUpd, yUpd, OX, q);
//            q = lessThanYBound ? yMin : yMax;
//            double[] interOy = getIntersectionWith(xOld, yOld, xUpd, yUpd, OY, q);
//            intersection = (interOx[0] < xMax || interOx[1] > xMin) ?
//                    interOx :
//                    interOy;
//        } else if (outOfXBounds) {
//            q = lessThanXBound ? xMin : xMax;
//            intersection = getIntersectionWith(xOld, yOld, xUpd, yUpd, OX, q);
//        } else { // if (outOfYBounds)
//            q = lessThanYBound ? yMin : yMax;
//            intersection = getIntersectionWith(xOld, yOld, xUpd, yUpd, OY, q);
//        }
//
//        // Reupdate state w/ respect to found intersections
//        p.xVel = outOfXBounds ? -p.xVel :p.xVel;
//        p.yVel = outOfYBounds ? -p.yVel :p.yVel;
//        p.x = intersection[0];
//        p.y = intersection[1];
    }

    /**
     * Finds intersection between point trajectory and constraints lines. (Not working now)
     */
    private double[] getIntersectionWith(double x1, double y1, double x2, double y2, int axe, double k) {
        double[] intersection;
        double u = 0.0d;
        if (axe == OX) {
            u = (k-y1)*(x2-x1)/(y2-y1);
            intersection = new double[] {k, u};
        } else { // if (axe == OY)
            u = (k-x1)*(y2-y1)/(x2-x1);
            intersection = new double[] {u, k};
        }
        return intersection;
    }

    public boolean isFinished() {
        return step >= maxIterations;
    }

    public int getStep() {
        return step;
    }

    public int getPointsNumber() {
        return points.length;
    }

    public Point[] getPoints() {    // Only for reading!!!
        return points;
    }

    public int getGlobalBestId() {
        return globalBestId;
    }

    /**
     * Just a structure to hold best answer, id and time of cycle
     */
    public static class Answer {
        double x, y, z;
        public long time;
        public int step, id;

        public Answer(double x, double y, double z, int id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.id = id;
        }

        public long getTime() {
            return time;
        }
        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }
        public double[] getCoordinates() {
            return new double[] {x, y};
        }
        public double getFunctionValue() {
            return z;
        }
    }
}
