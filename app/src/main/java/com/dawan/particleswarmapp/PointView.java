package com.dawan.particleswarmapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.Random;

public class PointView extends View
{
    public static final int bestSize = 9, ordinarySize = 4;

    int id;
    float _x, _y;                       // DELETE !!!!!

    public PointView(Context context, int id) {
        super(context);
        this.id = id;

        Random rnd = new Random(id);
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        setBackgroundColor(color);
    }

    public void setNewLayout(int x, int y) {
        setX(x);
        setY(y);
        _x = getX();
        _y = getY();
    }

    public void doBest() {
        resizeView(bestSize, bestSize);
    }

    public void undoBest() {
        resizeView(ordinarySize, ordinarySize);
    }

    private void resizeView(int newWidth, int newHeight) {
        try {
            Constructor<? extends ViewGroup.LayoutParams> ctor = getLayoutParams().getClass().getDeclaredConstructor(int.class, int.class);
            setLayoutParams(ctor.newInstance(
                    newWidth*(int)getResources().getDisplayMetrics().density,
                    newHeight*(int)getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
