package com.maning.librarycrashmonitor.crash;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.listener.MCrashCallBack;
import com.maning.librarycrashmonitor.MCrashMonitor;
import com.maning.librarycrashmonitor.ui.activity.CrashListActivity;
import com.maning.librarycrashmonitor.utils.MFileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author maning
 */
public class MCrashHandler implements UncaughtExceptionHandler {
    /**
     * 日志Tag
     */
    private static final String TAG = "CrashMonitor";
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 时间转换
     */
    private static final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.CHINA);
    /**
     * log文件的后缀名
     */
    private static final String FILE_NAME_SUFFIX = ".txt";
    /**
     * 实例对象
     */
    private static final MCrashHandler sInstance = new MCrashHandler();
    /**
     * 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
     */
    private static UncaughtExceptionHandler mDefaultCrashHandler;
    /**
     * app版本信息
     */
    private String versionName;
    private String versionCode;
    private String crashTime;
    private String crashHead;
    /**
     * 是否处于Debug状态
     */
    private boolean isDebug = false;
    /**
     * 回调
     */
    private MCrashCallBack crashCallBack;
    /**
     * 额外信息写入
     */
    private String extraContent;

    /**
     * 构造方法私有，防止外部构造多个实例，即采用单例模式
     */
    private MCrashHandler() {
    }

    public static MCrashHandler getInstance() {
        return sInstance;
    }

    /**
     * 这里主要完成初始化工作
     *
     * @param context
     */
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

    public void init(Context context, MCrashCallBack crashCallBack) {
        init(context, false, crashCallBack);
    }

    public void init(Context context, boolean isDebug, MCrashCallBack crashCallBack) {
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

        //延时杀死进程
        SystemClock.sleep(500);

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

    /**
     * 崩溃信息写入文件
     *
     * @param ex
     */
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
            String fileName = "V" + versionName + "_" + crashTime + FILE_NAME_SUFFIX;
            file = new File(dir, fileName);
            if (!file.exists()) {
                boolean createNewFileOk = file.createNewFile();
                if (!createNewFileOk) {
                    return;
                }
            }
            //开始写日志
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //判断有没有额外信息需要写入
            if (!TextUtils.isEmpty(extraContent)) {
                pw.println(extraContent);
            }
            //写入设备信息
            pw.println(crashHead);
            //导出异常的调用栈信息
            ex.printStackTrace(pw);
            //异常信息
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
            //重新命名文件
            String newName = "V" + versionName + "_" + crashTime + "_" + ex.toString() + FILE_NAME_SUFFIX;
            File newFile = new File(dir, newName);
            MFileUtils.renameFile(file.getPath(), newFile.getPath());
        } catch (Exception e) {
            Log.e(TAG, "保存日志失败：" + e.toString());
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
        crashTime = dataFormat.format(new Date(System.currentTimeMillis()));
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
        //组合Android相关信息
        crashHead =
                "\n崩溃的时间\b\b\b:\b\b" + crashTime +
                        "\n系统硬件商\b\b\b:\b\b" + Build.MANUFACTURER +
                        "\n设备的品牌\b\b\b:\b\b" + Build.BRAND +
                        "\n手机的型号\b\b\b:\b\b" + Build.MODEL +
                        "\n设备版本号\b\b\b:\b\b" + Build.ID +
                        "\nCPU的类型\b\b\b:\b\b" + Build.CPU_ABI +
                        "\n系统的版本\b\b\b:\b\b" + Build.VERSION.RELEASE +
                        "\n系统版本值\b\b\b:\b\b" + Build.VERSION.SDK_INT +
                        "\n当前的版本\b\b\b:\b\b" + versionName + "—" + versionCode +
                        "\n\n";
    }


    private void debugHandler(Throwable ex) {
        if (!isDebug) {
            return;
        }
        //发送通知
        notifyLog(Log.getStackTraceString(ex));
        //开启日志崩溃页面
        MCrashMonitor.startCrashShowPage(mContext);
    }

    /**
     * 显示通知
     *
     * @param content
     */
    private void notifyLog(String content) {
        try {
            //点击跳转的页面
            Intent intent = new Intent(mContext, CrashListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(mContext,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap crash_ic_notify =  BitmapFactory.decodeResource(mContext.getResources(), R.drawable.crash_ic_notify);
            //通知
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(mContext)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentTitle("Crash通知:" + crashTime)
                    .setSmallIcon(R.drawable.crash_ic_notify)
                    .setLargeIcon(crash_ic_notify)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pIntent)
                    .build();
            if (manager != null) {
                manager.notify(10010, notification);
            }
        } catch (Exception e) {

        }
    }

    //----------------------华丽分割线----------------------------//

    /**
     * 设置额外的内容
     *
     * @param extraContent
     */
    public void setExtraContent(String extraContent) {
        this.extraContent = extraContent;
    }


}
