package com.maning.librarycrashmonitor.ui.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.utils.MActivityListUtil;
import com.maning.librarycrashmonitor.utils.MBitmapUtil;
import com.maning.librarycrashmonitor.utils.MFileUtils;
import com.maning.librarycrashmonitor.utils.MPermission5Utils;
import com.maning.librarycrashmonitor.utils.MScreenShotUtil;
import com.maning.librarycrashmonitor.utils.MShareUtil;
import com.maning.librarycrashmonitor.utils.MSpannableUtils;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 崩溃详情页面展示
 */
public class CrashDetailsActivity extends CrashBaseActivity {

    /**
     * Intent 传递的文件路径
     */
    public static final String IntentKey_FilePath = "IntentKey_FilePath";
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 崩溃日志的内容
     */
    private String crashContent;
    /**
     * 具体的异常类型
     */
    private String matchErrorInfo;
    /**
     * 所有Activity的集合
     */
    private List<Class> activitiesClass;


    private TextView textView;
    private Toolbar toolbar;
    private ScrollView scrollView;

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_details);

        try {
            initIntent();

            initViews();

            initDatas();
        } catch (Exception e) {
        }

    }

    private void initDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取文件夹名字匹配异常信息高亮显示
                File file = new File(filePath);
                String[] splitNames = file.getName().replace(".txt", "").split("_");
                if (splitNames.length == 3) {
                    String errorMsg = splitNames[2];
                    if (!TextUtils.isEmpty(errorMsg)) {
                        matchErrorInfo = errorMsg;
                    }
                }
                //获取内容
                crashContent = MFileUtils.readFile2String(filePath);
                if (handler == null) {
                    return;
                }
                //获取所有Activity
                activitiesClass = MActivityListUtil.getActivitiesClass(context, getPackageName(), null);

                //富文本显示
                Spannable spannable = Spannable.Factory.getInstance().newSpannable(crashContent);

                //匹配错误信息
                if (!TextUtils.isEmpty(matchErrorInfo)) {
                    spannable = MSpannableUtils.addNewSpanable(context, spannable, crashContent, matchErrorInfo, Color.parseColor("#FF0006"), 18);
                }

                //匹配包名
                String packageName = getPackageName();
                spannable = MSpannableUtils.addNewSpanable(context, spannable, crashContent, packageName, Color.parseColor("#0070BB"), 0);

                //匹配Activity
                if (activitiesClass != null && activitiesClass.size() > 0) {
                    for (int i = 0; i < activitiesClass.size(); i++) {
                        spannable = MSpannableUtils.addNewSpanable(context, spannable, crashContent, activitiesClass.get(i).getSimpleName(), Color.parseColor("#55BB63"), 16);
                    }
                }

                //主线程处理
                final Spannable finalSpannable = spannable;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (textView != null) {
                            try {
                                textView.setText(finalSpannable);
                            } catch (Exception e) {
                                textView.setText(crashContent);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        textView = (TextView) findViewById(R.id.textView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        scrollView = (ScrollView) findViewById(R.id.scrollViewCrashDetails);

        initToolBar(toolbar, "崩溃详情", R.drawable.crash_ic_back_arrow_white_24dp);
    }

    private void initIntent() {
        filePath = getIntent().getStringExtra(IntentKey_FilePath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crash_menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.share) {
            //分享
            MShareUtil.shareFile(context, new File(filePath));
            return true;
        } else if (item.getItemId() == R.id.copy) {
            //复制
            putTextIntoClip();
            Toast.makeText(context, "复制内容成功", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.shot) {
            //请求权限
            //检查版本是否大于M
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10086);
                } else {
                    saveScreenShot();
                }
            } else {
                //6.0之下判断有没有权限
                if(MPermission5Utils.hasWritePermission()){
                    saveScreenShot();
                }else{
                    Toast.makeText(context, "缺少存储权限", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10086) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveScreenShot();
            } else {
                Toast.makeText(context, "权限已拒绝", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 保存截图
     */
    private void saveScreenShot() {
        //生成截图
        Bitmap bitmap = MScreenShotUtil.getBitmapByView(scrollView);
        if (bitmap != null) {
            String crashPicPath = MFileUtils.getCrashPicPath() + "/crash_pic_" + System.currentTimeMillis() + ".jpg";
            boolean saveBitmap = MBitmapUtil.saveBitmap(context, bitmap, crashPicPath);
            if (saveBitmap) {
                Toast.makeText(context, "保存截图成功，请到相册查看\n路径：" + crashPicPath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "保存截图失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "保存截图失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加到剪切板
     */
    public void putTextIntoClip() {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("CrashLog", crashContent);
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
