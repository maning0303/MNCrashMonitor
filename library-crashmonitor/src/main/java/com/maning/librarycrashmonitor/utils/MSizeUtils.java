package com.maning.librarycrashmonitor.utils;

import android.content.Context;

/**
 * @author : maning
 * @desc :  转换工具
 */
public class MSizeUtils {

    /**
     * sp 转 px
     *
     * @param spValue sp 值
     * @return px 值
     */
    public static int sp2px(Context context, final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
