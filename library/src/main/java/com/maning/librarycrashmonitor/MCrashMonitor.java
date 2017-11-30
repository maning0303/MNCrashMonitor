package com.maning.librarycrashmonitor;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.maning.librarycrashmonitor.crash.CrashHandler;
import com.maning.librarycrashmonitor.listener.CrashCallBack;
import com.maning.librarycrashmonitor.ui.activity.CrashListActivity;
import com.maning.librarycrashmonitor.ui.activity.CrashShowActivity;
import com.maning.librarycrashmonitor.utils.MFileUtils;

/**
 * Created by maning on 2017/4/20.
 * 主类
 */

public class MCrashMonitor {

    public static void setCrashLogExtraInfo(String content) {
        if (!TextUtils.isEmpty(content)) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.setExtraContent(content);
        }
    }

    public static void init(Context context) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
    }

    public static void init(Context context, boolean isDebug) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context, isDebug);
    }

    public static void init(Context context, CrashCallBack crashCallBacks) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context, crashCallBacks);
    }

    public static void init(Context context, boolean isDebug, CrashCallBack crashCallBacks) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context, isDebug, crashCallBacks);
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

    public static String getCrashLogFile(Context context) {
        return MFileUtils.getCrashLogPath(context);
    }
}
