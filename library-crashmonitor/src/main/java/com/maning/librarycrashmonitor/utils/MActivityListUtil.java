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
            //Get all activity classes in the AndroidManifest.xml
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities != null) {
                Log.d(TAG, "Found " + packageInfo.activities.length + " activity in the AndrodiManifest.xml");
                for (ActivityInfo ai : packageInfo.activities) {
                    Class c;
                    try {
                        c = Class.forName(ai.name);
                        // Maybe isAssignableFrom is unnecessary
                        if (Activity.class.isAssignableFrom(c)) {
                            returnClassList.add(c);
                            Log.d(TAG, ai.name + "...OK");
                        }
                    } catch (ClassNotFoundException e) {
                        Log.d(TAG, "Class Not Found:" + ai.name);
                    }
                }
                Log.d(TAG, "Filter out, left " + returnClassList.size() + " activity," + Arrays.toString(returnClassList.toArray()));

                //Exclude some activity classes
                if (excludeList != null) {
                    returnClassList.removeAll(excludeList);
                    Log.d(TAG, "Exclude " + excludeList.size() + " activity," + Arrays.toString(excludeList.toArray()));
                }
                Log.d(TAG, "Return " + returnClassList.size() + " activity," + Arrays.toString(returnClassList.toArray()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnClassList;
    }


}
