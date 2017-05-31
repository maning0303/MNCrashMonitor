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
        /**
         * 初始化日志系统
         * context :    上下文
         * isDebug :    是不是Debug模式,true:崩溃后显示自定义崩溃页面 ;false:关闭应用,不跳转奔溃页面(默认)
         */
        MCrashMonitor.init(this, true);
    }
}
