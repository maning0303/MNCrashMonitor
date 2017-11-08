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

    private static final String TAG = "CrashMonitor";

    //log文件的后缀名
    private static final String FILE_NAME_SUFFIX = ".txt";

    private static CrashHandler sInstance = new CrashHandler();

    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

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
        init(context);
        this.isDebug = isDebug;
    }

    public void init(Context context, boolean isDebug, CrashCallBack crashCallBack) {
        init(context);
        this.isDebug = isDebug;
        this.crashCallBack = crashCallBack;
    }

    public void init(Context context, CrashCallBack crashCallBack) {
        init(context);
        this.crashCallBack = crashCallBack;
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        //导出异常信息到SD卡中
        File file = dumpExceptionToSDCard(ex);

        //回调处理
        if (crashCallBack != null) {
            crashCallBack.onCrash(file);
        }

        //打印出当前调用栈信息
        ex.printStackTrace();

        //延时1秒杀死进程
        SystemClock.sleep(1000);

        //Debug相关处理
        debugHandler(ex);

        //这里可以弹出自己自定义的程序崩溃页面：然后自己干掉自己；
        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }


    private File dumpExceptionToSDCard(Throwable ex) {
        File file = null;
        PrintWriter pw = null;
        try {
            //Log保存路径
            // SDCard/Android/data/<application package>/cache
            // data/data/<application package>/cache
            File dir = new File(MFileUtils.getCrashLogPath(mContext));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //版本号
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            String version = "V" + pi.versionName + "_";
            //时间
            String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(System.currentTimeMillis()));
            time = "T" + time;

            //以当前时间创建log文件
            file = new File(dir, "CrashLog_" + version + time.trim() + FILE_NAME_SUFFIX);
            if (!file.exists()) {
                file.createNewFile();
            }

            //开始写日志
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出发生异常的时间
            pw.println(time);
            //导出手机信息
            dumpPhoneInfo(pw);
            //换行
            pw.println();
            //导出异常的调用栈信息
            ex.printStackTrace(pw);

        } catch (Exception e) {
            Log.e(TAG, "保存日志失败：：" + e.toString());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return file;
    }

    private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        //应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }


    private void debugHandler(Throwable ex) {
        if (!isDebug) {
            return;
        }
        //发送通知
        notify_log(Log.getStackTraceString(ex));
        //开启日志详情页面
        MCrashMonitor.startCrashShowPage(mContext);
    }


    private void notify_log(String content) {
        //设置想要展示的数据内容
        Intent intent = new Intent(mContext, CrashListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int smallIcon = R.drawable.crash_ic_show_error;
        String ticker = "Crash通知";
        String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(System.currentTimeMillis()));
        String title = "Crash通知:" + time;
        //实例化工具类，并且调用接口
        MNotifyUtil notify2 = new MNotifyUtil(mContext, 1);
        notify2.notify_normail_moreline(pIntent, smallIcon, ticker, title, content, true, true, false);
    }

}
