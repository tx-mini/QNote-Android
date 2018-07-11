package com.ace.qnote.util;

import android.content.Context;

/**
 * Created by ice on 2018/7/10.
 */

public class CommonUtils {
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isEmpty(String s){
        return s == null || s.equals("");
    }
}
