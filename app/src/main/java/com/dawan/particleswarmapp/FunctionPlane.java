package com.dawan.particleswarmapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class FunctionPlane extends ConstraintLayout {
    List<PointView> pointViews = new ArrayList<>();
    View axeX, axeY;
    int pointsNum;
    int bestPointViewId = -1;
    double maxX = 5.0;
    double minX = -5.0;
    double maxY = 5.0;
    double minY = -5.0;
    double kX, muY, kY, muX;               // Scaling factor and bias
    double w, h;
    int marginX;
    int marginY;
    public FunctionPlane(Context context) {
        super(context);
    }
    public FunctionPlane(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public FunctionPlane(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void findAxes() {
        axeX = findViewById(R.id.axeX);
        axeY = findViewById(R.id.axeY);
    }

    public void preparePoints(int pointsNum) {
        this.pointsNum = pointsNum;

        if (pointViews != null) {
            int dn = pointsNum - pointViews.size();
            if (dn > 0) {
                for (int i = 0; i < dn; i++) {
                    PointView pv = new PointView(getContext(),  pointViews.size() + i);
                    pointViews.add(pv);
                    addView(pv, 8, 8);
                }
            }
            if (dn < 0) {
                for (int i = pointViews.size()-1; i >= pointsNum ; i--) {
                    removeView(pointViews.get(i));
                    pointViews.remove(i);
                }
            }
        }
    }

    public void updatePoint(int id, double x, double y) {
        if (valid(id)) {
            pointViews.get(id).setNewLayout(scaledX(x) ,scaledY(y));
        }
    }

    public void updateBestPoint(int id) {       // Не имеет смысла, т.к показкает точку давшую глобальные значения
        if (id != bestPointViewId && valid(id)) {
            if (bestPointViewId != -1)
                pointViews.get(bestPointViewId).undoBest();
            bestPointViewId = id;
            pointViews.get(id).doBest();
        }
    }

    public void setActualMinimum(double actualX, double actualY) {
        if (axeX.getLayoutParams() instanceof ViewGroup.MarginLayoutParams
                && axeY.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams pX = (ViewGroup.MarginLayoutParams) axeX.getLayoutParams();
            ViewGroup.MarginLayoutParams pY = (ViewGroup.MarginLayoutParams) axeY.getLayoutParams();
            pX.setMargins(0, scaledY(actualY), 0, 0);
            pY.setMargins(scaledX(actualX), 0, 0, 0);
            axeX.requestLayout();
            axeY.requestLayout();
        }
    }

    public void setCustomAxes(double maxX, double minX, double maxY,double minY) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
    }


    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public int scaledX(double x) {
        return (int) (x*kX + muX);
    }
    public int scaledY(double y) {
        return (int) (y*kY + muY);
    }

    public boolean valid(int id) {
        return (id >=0) && (id < pointViews.size());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        updateProportions();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void updateProportions() {
        double dx = maxX - minX;
        double dy = maxY - minY;

        this.kX = w / dx;
        this.muX = -(minX * w) / dx;

        this.kY = -(h / dy);
        this.muY = (minY / dy + 1) * h;

        if (axeX != null && axeY != null) {
            setActualMinimum(U.actualX, U.actualY);
        }
    }
}
