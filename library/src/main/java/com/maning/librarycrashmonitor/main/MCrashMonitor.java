package com.maning.librarycrashmonitor.main;

import android.content.Context;
import android.content.Intent;

import com.maning.librarycrashmonitor.crash.CrashHandler;
import com.maning.librarycrashmonitor.ui.activity.CrashListActivity;
import com.maning.librarycrashmonitor.ui.activity.CrashShowActivity;

/**
 * Created by maning on 2017/4/20.
 * 主类
 */

public class MCrashMonitor {

    public static void init(Context context, boolean isDebug) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context, isDebug);
    }

    public static void startCrashListPage(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), CrashListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }


    public static void startCrashShowPage(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), CrashShowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

}
