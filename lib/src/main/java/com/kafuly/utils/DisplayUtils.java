package com.kafuly.utils;

import android.content.Context;


public class DisplayUtils {
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static float mScreenDensity;


    public static int getScreenHeight(Context context) {
        if (mScreenHeight <= 0) {
            mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        }
        return mScreenHeight;
    }


    public static int getScreenWidth(Context context) {
        if (mScreenWidth <= 0) {
            mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }
        return mScreenWidth;
    }


    public static float getDensity(Context context) {
        if (mScreenDensity <= 0) {
            mScreenDensity = context.getResources().getDisplayMetrics().density;
        }
        return mScreenDensity;
    }


    public static int dp2px(Context context, float dp) {
        return (int) (dp * getDensity(context) + 0.5f);
    }

}