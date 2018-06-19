package com.maning.librarycrashmonitor.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.utils.MFileUtils;
import com.maning.librarycrashmonitor.utils.MShareUtil;

import java.io.File;

/***
 * 崩溃详情页面展示
 */
public class CrashDetailsActivity extends CrashBaseActivity {

    public static final String IntentKey_FilePath = "IntentKey_FilePath";
    private String filePath;
    private String content;

    private TextView textView;
    private Toolbar toolbar;

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_details);

        initIntent();

        initViews();

        initDatas();

    }

    private void initDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                content = MFileUtils.readFile2String(filePath);
                if (handler == null) {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(content);
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        textView = (TextView) findViewById(R.id.textView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        initToolBar(toolbar, "崩溃详情", R.drawable.crash_ic_back_arrow_white_24dp);
    }

    private void initIntent() {
        filePath = getIntent().getStringExtra(IntentKey_FilePath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crash_menu2, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void putTextIntoClip() {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("CrashLog", content);
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
