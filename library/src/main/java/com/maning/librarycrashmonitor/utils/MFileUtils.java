package com.maning.librarycrashmonitor.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maning on 2017/4/20.
 * <p>
 * 保存日志的工具类
 */

public class MFileUtils {

    /**
     * SDCard/Android/data/<application package>/cache
     * data/data/<application package>/cache
     */
    public static String getCrashLogPath(Context context) {
        String path = getCachePath(context) + File.separator + "crashLogs";
        return path;
    }

    /**
     * 获取app缓存路径
     * SDCard/Android/data/<application package>/cache
     * data/data/<application package>/cache
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getAbsolutePath();
        }
        return cachePath;
    }


    public static List<File> getFileList(File file) {
        List<File> mFileList = new ArrayList<>();
        File[] fileArray = file.listFiles();
        if (fileArray == null || fileArray.length <= 0) {
            return mFileList;
        }
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

    public static String readFile2String(String fileName) {
        String res = "";
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder("");
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            res = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
