package com.maning.librarycrashmonitor.crash;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.listener.CrashCallBack;
import com.maning.librarycrashmonitor.main.MCrashMonitor;
import com.maning.librarycrashmonitor.ui.activity.CrashListActivity;
import com.maning.librarycrashmonitor.utils.MAppUtils;
import com.maning.librarycrashmonitor.utils.MFileUtils;
import com.maning.librarycrashmonitor.utils.MNotifyUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class CrashHandler implements UncaughtExceptionHandler {
    //上下文
    private Context mContext;

    //日志Tag
    private static final String TAG = "CrashMonitor";
    //时间转换
    private static final SimpleDateFormat yyyy_MM_dd_hh_mm_ss_DataFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    //log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".txt";
    //log文件的前缀名
    private static final String FILE_NAME_PREFIX = "CrashLog_";
    //实例对象
    private static final CrashHandler sInstance = new CrashHandler();
    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private static UncaughtExceptionHandler mDefaultCrashHandler;
    //app版本信息
    private static String versionName;
    private static String versionCode;
    private static String crashTime;
    private static String crashHead;
    //是否处于Debug状态
    private boolean isDebug = false;
    //回调
    private CrashCallBack crashCallBack;

    //构造方法私有，防止外部构造多个实例，即采用单例模式
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    //这里主要完成初始化工作
    public void init(Context context) {
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    public void init(Context context, boolean isDebug) {
        init(context, isDebug, null);
    }

    public void init(Context context, CrashCallBack crashCallBack) {
        init(context, false, crashCallBack);
    }

    public void init(Context context, boolean isDebug, CrashCallBack crashCallBack) {
        init(context);
        this.isDebug = isDebug;
        this.crashCallBack = crashCallBack;
    }


    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //初始化相关信息
        initCrashHead();

        //导出异常信息到文件
        dumpExceptionToFile(ex);

        //Debug相关处理
        debugHandler(ex);

        //延时1秒杀死进程
        SystemClock.sleep(1000);

        //这里可以弹出自己自定义的程序崩溃页面：然后自己干掉自己；
        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }

    private void dumpExceptionToFile(Throwable ex) {

        File file = null;
        PrintWriter pw = null;
        try {
            //Log保存路径
            // SDCard/Android/data/<application package>/cache
            // data/data/<application package>/cache
            File dir = new File(MFileUtils.getCrashLogPath(mContext));
            if (!dir.exists()) {
                boolean ok = dir.mkdirs();
                if (!ok) {
                    return;
                }
            }
            //Log文件的名字
            String fileName = FILE_NAME_PREFIX + "V" + versionName + "_" + crashTime + FILE_NAME_SUFFIX;
            file = new File(dir, fileName);
            if (!file.exists()) {
                boolean ok = file.createNewFile();
                if (!ok) {
                    return;
                }
            }
            //开始写日志
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出发生异常的时间
            pw.println(crashHead);
            //导出异常的调用栈信息
            ex.printStackTrace(pw);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
        } catch (Exception e) {
            Log.e(TAG, "保存日志失败：：" + e.toString());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        //回调处理
        if (crashCallBack != null && file != null) {
            crashCallBack.onCrash(file);
        }
    }


    private void initCrashHead() {
        //崩溃时间
        crashTime = yyyy_MM_dd_hh_mm_ss_DataFormat.format(new Date(System.currentTimeMillis()));
        //版本信息
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = String.valueOf(pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        crashHead =
                        "\nCrash Time           : " + crashTime +// 时间
                        "\nDevice Manufacturer  : " + Build.MANUFACTURER +// 设备厂商
                        "\nDevice CPU ABI       : " + Build.CPU_ABI +// CPU
                        "\nDevice Model         : " + Build.MODEL +// 设备型号
                        "\nAndroid OS Version   : " + Build.VERSION.RELEASE +// 系统版本
                        "\nAndroid SDK          : " + Build.VERSION.SDK_INT +// SDK版本
                        "\nApp VersionName      : " + versionName +
                        "\nApp VersionCode      : " + versionCode +
                        "\n\n";
        Log.i(TAG, crashHead);
    }


    private void debugHandler(Throwable ex) {
        if (!isDebug) {
            return;
        }
        //发送通知
        notifyLog(Log.getStackTraceString(ex));
        //开启日志详情页面
        MCrashMonitor.startCrashShowPage(mContext);
    }


    private void notifyLog(String content) {
        //设置想要展示的数据内容
        Intent intent = new Intent(mContext, CrashListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int smallIcon = R.drawable.crash_ic_show_error;
        String ticker = "Crash通知";
        String title = "Crash通知:" + crashTime;
        //实例化工具类，并且调用接口
        MNotifyUtil notify2 = new MNotifyUtil(mContext, 1);
        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, content, true, true, false);
    }

}
