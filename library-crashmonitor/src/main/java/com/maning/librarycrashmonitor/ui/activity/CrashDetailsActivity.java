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
import com.maning.librarycrashmonitor.utils.MScreenShotUtil;
import com.maning.librarycrashmonitor.utils.MShareUtil;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 崩溃详情页面展示
 */
public class CrashDetailsActivity extends CrashBaseActivity {

    public static final String IntentKey_FilePath = "IntentKey_FilePath";
    private String filePath;
    private String crashContent;
    private String matchErrorInfo;
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
                if (splitNames.length >= 3) {
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (textView != null) {
                            try {
                                //富文本显示
                                Spannable spannable = Spannable.Factory.getInstance().newSpannable(crashContent);

                                //匹配错误信息
                                if (!TextUtils.isEmpty(matchErrorInfo)) {
                                    addNewSpanable(spannable, matchErrorInfo, Color.parseColor("#FF0006"), 18);
                                }

                                //匹配包名
                                String packageName = getPackageName();
                                addNewSpanable(spannable, packageName, Color.parseColor("#0070BB"), 0);

                                //匹配Activity
                                if (activitiesClass != null && activitiesClass.size() > 0) {
                                    for (int i = 0; i < activitiesClass.size(); i++) {
                                        addNewSpanable(spannable, activitiesClass.get(i).getSimpleName(), Color.parseColor("#55BB63"), 16);
                                    }
                                }

                                textView.setText(spannable);
                            } catch (Exception e) {
                                textView.setText(crashContent);
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void addNewSpanable(Spannable spannable, String matchContent, @ColorInt int foregroundColor, int textSize) {
        Pattern pattern = Pattern.compile(Pattern.quote(matchContent));
        Matcher matcher = pattern.matcher(crashContent);
        while (matcher.find()) {
            int start = matcher.start();
            if (start >= 0) {
                int end = start + matchContent.length();
                if (textSize > 0) {
                    spannable.setSpan(new AbsoluteSizeSpan(sp2px(textSize)), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
                spannable.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    /**
     * sp 转 px
     *
     * @param spValue sp 值
     * @return px 值
     */
    public int sp2px(final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

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
                saveScreenShot();
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
