package com.maning.librarycrashmonitor.utils;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maning on 2017/4/20.
 * <p>
 * 保存日志的工具类
 */

public class MFileUtils {

    private static final String TAG = "CrashMonitor";

    /**
     * SDCard/Android/data/<application package>/cache
     */
    public static String getCrashLogPath(Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        String path = externalCacheDir.getAbsolutePath() + File.separator + "crashLogs";
        return path;
    }


    public static List<File> getFileList(File file) {
        List<File> mFileList = new ArrayList<>();
        File[] fileArray = file.listFiles();
        for (File f : fileArray) {
            if (f.isFile()) {
                mFileList.add(f);
            }
        }
        return mFileList;
    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

}
