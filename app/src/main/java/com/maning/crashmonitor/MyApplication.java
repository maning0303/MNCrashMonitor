package com.maning.crashmonitor;

import android.app.Application;
import android.util.Log;

import com.maning.librarycrashmonitor.main.MCrashMonitor;

/**
 * Created by maning on 2017/4/20.
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");

        initCrashMonitor();

    }

    private void initCrashMonitor() {
        MCrashMonitor.init(this, true);
    }
}
