package com.maning.librarycrashmonitor.utils;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.text.Spannable;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : maning
 * @desc :  富文本相关
 */
public class MSpannableUtils {

    /**
     * 添加富文本
     *
     * @param spannable
     * @param matchContent    需要匹配的文本
     * @param foregroundColor 改变颜色
     * @param textSize        文字大小
     */
    public static Spannable addNewSpanable(Context context, Spannable spannable, String allContent, String matchContent, @ColorInt int foregroundColor, int textSize) {
        Pattern pattern = Pattern.compile(Pattern.quote(matchContent));
        Matcher matcher = pattern.matcher(allContent);
        while (matcher.find()) {
            int start = matcher.start();
            if (start >= 0) {
                int end = start + matchContent.length();
                if (textSize > 0) {
                    spannable.setSpan(new AbsoluteSizeSpan(MSizeUtils.sp2px(context, textSize)), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                spannable.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return spannable;
    }

}
