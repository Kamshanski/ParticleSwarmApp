package com.dawan.particleswarmapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.Random;

/**
 * Square block represents a point of algorithm swamp
 */
public class PointView extends View
{
    int id;

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
    }

    // maybe in future implement emphasizing with size of point
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
