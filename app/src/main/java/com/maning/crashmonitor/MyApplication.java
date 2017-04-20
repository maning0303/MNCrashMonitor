package com.maning.crashmonitor;

import android.app.Application;

import com.maning.librarycrashmonitor.main.MCrashMonitor;

/**
 * Created by maning on 2017/4/20.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        initCrashMonitor();

    }

    private void initCrashMonitor() {
        MCrashMonitor.init(this, true);
    }
}
