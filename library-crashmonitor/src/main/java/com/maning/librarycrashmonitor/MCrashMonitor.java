package com.maning.librarycrashmonitor;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.maning.librarycrashmonitor.crash.MCrashHandler;
import com.maning.librarycrashmonitor.listener.MCrashCallBack;
import com.maning.librarycrashmonitor.ui.activity.CrashListActivity;
import com.maning.librarycrashmonitor.ui.activity.CrashShowActivity;
import com.maning.librarycrashmonitor.utils.MFileUtils;

/**
 * Created by maning on 2017/4/20.
 * 主类
 */

public class MCrashMonitor {

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        MCrashHandler crashHandler = MCrashHandler.getInstance();
        crashHandler.init(context, false, null);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param isDebug 是否处于debug状态
     */
    public static void init(Context context, boolean isDebug) {
        MCrashHandler crashHandler = MCrashHandler.getInstance();
        crashHandler.init(context, isDebug, null);
    }

    /**
     * 初始化
     *
     * @param context        上下文
     * @param crashCallBacks 回调
     */
    public static void init(Context context, MCrashCallBack crashCallBacks) {
        MCrashHandler crashHandler = MCrashHandler.getInstance();
        crashHandler.init(context, false, crashCallBacks);
    }

    /**
     * 初始化
     *
     * @param context        上下文
     * @param isDebug        是否处于debug状态
     * @param crashCallBacks 回调
     */
    public static void init(Context context, boolean isDebug, MCrashCallBack crashCallBacks) {
        MCrashHandler crashHandler = MCrashHandler.getInstance();
        crashHandler.init(context, isDebug, crashCallBacks);
    }

    /**
     * 日志列表页面
     *
     * @param context
     */
    public static void startCrashListPage(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), CrashListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    /**
     * 打开奔溃展示页面
     *
     * @param context
     */
    public static void startCrashShowPage(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), CrashShowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    /**
     * 获取日志的路径
     *
     * @param context
     * @return
     */
    public static String getCrashLogFilesPath(Context context) {
        return MFileUtils.getCrashLogPath(context);
    }

    /**
     * 设置额外的日志内容，当发生崩溃的时候会写入当前内容到文件开头
     * 例如L用户手机号码，Token , 网络环境等定制化东西
     *
     * @param content 内容
     */
    public static void setCrashLogExtraInfo(String content) {
        if (!TextUtils.isEmpty(content)) {
            MCrashHandler crashHandler = MCrashHandler.getInstance();
            crashHandler.setExtraContent(content);
        }
    }

}
