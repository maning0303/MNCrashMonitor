package com.maning.librarycrashmonitor.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

/**
 * Created by maning on 2017/11/30.
 */

public class MShareUtil {

    // 調用系統方法分享文件
    public static void shareFile(Context context, File file) {
        try {
            if (null != file && file.exists()) {
                Intent share = new Intent(Intent.ACTION_SEND);

                Uri uri;
                //判断7.0以上
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".crashFileProvider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                share.putExtra(Intent.EXTRA_STREAM, uri);
                //此处可发送多种文件
                share.setType(getMimeType(file.getAbsolutePath()));
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(share, "分享文件"));
            } else {
                Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "分享失败：" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

}
