package com.maning.librarycrashmonitor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * @author : maning
 * @desc :  获取所有Activity
 */
public class MActivityListUtil {

    /**
     * 返回AndroidManifest.xml中注册的所有Activity的class
     *
     * @param context     环境
     * @param packageName 包名
     * @param excludeList 排除class列表
     * @return
     */
    public final static List<Class> getActivitiesClass(Context context, String packageName, List<Class> excludeList) {

        List<Class> returnClassList = new ArrayList<Class>();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities != null) {
                for (ActivityInfo ai : packageInfo.activities) {
                    Class c;
                    try {
                        c = Class.forName(ai.name);
                        if (Activity.class.isAssignableFrom(c)) {
                            returnClassList.add(c);
                        }
                    } catch (ClassNotFoundException e) {
                    }
                }
                if (excludeList != null) {
                    returnClassList.removeAll(excludeList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnClassList;
    }


}
