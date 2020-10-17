package com.dawan.particleswarmapp;

import android.os.Message;
import android.util.Log;

import java.util.GregorianCalendar;

public class U {
    public static final String formula = "(x-2)^2+(y-2)^2";
    public static final double actualX = 0, actualY = 0;
    public static final int maxIter = 70;
    public static final int[] pointsGrid = {5, 4};
    public static final double maxX = 5.0;
    public static final double minX = -5.0;
    public static final double maxY = 5.0;
    public static final double minY = -5.0;
    public static final double[][] constraints = {{U.minX, U.maxX},{U.minY, U.maxY}};
    public static final long sleepTimeMs = 250L;

    public static final Object o = new Object();


    public static long getTimeMs() {
        return new GregorianCalendar().getTimeInMillis();
    }

    public static boolean has(int a, int b) {
        return (a & b) > 0;
    }

    public static Message messageWith(int what) {
        return messageWith(what, o);
    }
    public static Message messageWith(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }
    public static Message messageWith(int what, Integer integer) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = integer;
        return msg;
    }
    public static void d(String s) {
        Log.d("MyApp", s);
    }

    public static int toIntSafely(String s, int errorCase) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            U.d("Wrong String at Number Parsing");
            return errorCase;
        }
    }

    public static double toDoubleSafely(String s, double errorCase) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            U.d("Wrong String at Number Parsing");
            return errorCase;
        }
    }
}
