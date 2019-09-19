package com.maning.librarycrashmonitor.ui.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.maning.librarycrashmonitor.utils.MSizeUtils;
import com.maning.librarycrashmonitor.utils.MSpannableUtils;

import java.io.File;
import java.util.List;

/***
 * 崩溃详情页面展示
 */
public class CrashDetailsActivity extends CrashBaseActivity implements View.OnClickListener {

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
    private ScrollView scrollView;

    private Handler handler = new Handler();
    private LinearLayout mBtnBack;
    /**
     * 分享
     */
    private TextView mBtnShare;
    /**
     * 复制
     */
    private TextView mBtnCopy;
    /**
     * 截图
     */
    private TextView mBtnScreenshot;
    private ImageView iv_screen_shot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mncrash_details);
        initView();

        try {
            initIntent();

            initView();

            initDatas();
        } catch (Exception e) {
        }

    }

    private void initDatas() {
        showProgressLoading("加载中...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                dismissProgressLoading();
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

    private void initView() {
        textView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scrollViewCrashDetails);


        mBtnBack = (LinearLayout) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mBtnShare = (TextView) findViewById(R.id.btn_share);
        mBtnShare.setOnClickListener(this);
        mBtnCopy = (TextView) findViewById(R.id.btn_copy);
        mBtnCopy.setOnClickListener(this);
        mBtnScreenshot = (TextView) findViewById(R.id.btn_screenshot);
        mBtnScreenshot.setOnClickListener(this);
        iv_screen_shot = (ImageView) findViewById(R.id.iv_screen_shot);
    }

    private void initIntent() {
        filePath = getIntent().getStringExtra(IntentKey_FilePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10086) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareLogs();
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
        showProgressLoading("正在保存截图...");
        //生成截图
        final Bitmap bitmap = MScreenShotUtil.getBitmapByView(scrollView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    String crashPicPath = MFileUtils.getCrashPicPath(context) + "/crash_pic_" + System.currentTimeMillis() + ".jpg";
                    boolean saveBitmap = MBitmapUtil.saveBitmap(context, bitmap, crashPicPath);
                    if (saveBitmap) {
                        showToast("保存截图成功，请到相册查看\n路径：" + crashPicPath);
                        final Bitmap bitmapCompress = MBitmapUtil.getBitmap(new File(crashPicPath), 200, 200);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgressLoading();
                                //设置图片
                                iv_screen_shot.setImageBitmap(bitmapCompress);
                                //显示
                                iv_screen_shot.setVisibility(View.VISIBLE);
                                //设置宽高
                                ViewGroup.LayoutParams layoutParams = iv_screen_shot.getLayoutParams();
                                layoutParams.width = MSizeUtils.getScreenWidth(context);
                                layoutParams.height = bitmapCompress.getHeight() * layoutParams.width / bitmapCompress.getWidth();
                                iv_screen_shot.setLayoutParams(layoutParams);
                                //设置显示动画
                                iv_screen_shot.setPivotX(0);
                                iv_screen_shot.setPivotY(0);
                                AnimatorSet animatorSetScale = new AnimatorSet();
                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv_screen_shot, "scaleX", 1, 0.2f);
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv_screen_shot, "scaleY", 1, 0.2f);
                                animatorSetScale.setDuration(1000);
                                animatorSetScale.setInterpolator(new DecelerateInterpolator());
                                animatorSetScale.play(scaleX).with(scaleY);
                                animatorSetScale.start();

                                //三秒后消失
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_screen_shot.setVisibility(View.GONE);
                                    }
                                }, 3000);
                            }
                        });
                    } else {
                        showToast("保存截图失败");
                        dismissProgressLoading();
                    }
                } else {
                    showToast("保存截图失败");
                    dismissProgressLoading();
                }
            }
        }).start();
    }

    private void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
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
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            finish();
        } else if (i == R.id.btn_share) {
            //先把文件转移到外部存储文件
            //请求权限
            //检查版本是否大于M
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10086);
                } else {
                    shareLogs();
                }
            } else {
                //6.0之下判断有没有权限
                if (MPermission5Utils.hasWritePermission()) {
                    shareLogs();
                } else {
                    Toast.makeText(context, "缺少存储权限", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (i == R.id.btn_copy) {
            //复制
            putTextIntoClip();
            Toast.makeText(context, "复制内容成功", Toast.LENGTH_SHORT).show();
        } else if (i == R.id.btn_screenshot) {
            //直接保存
            saveScreenShot();
        }
    }

    private void shareLogs() {
        //先把文件转移到外部存储文件
        File srcFile = new File(filePath);
        String destFilePath = MFileUtils.getCrashSharePath() + "/CrashShare.txt";
        File destFile = new File(destFilePath);
        boolean copy = MFileUtils.copyFile(srcFile, destFile);
        if (copy) {
            //分享
            MShareUtil.shareFile(context, destFile);
        } else {
            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
        }
    }
}
