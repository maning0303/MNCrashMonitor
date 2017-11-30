package com.maning.librarycrashmonitor.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.utils.MFileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

        initToolBar(toolbar, "崩溃详情", R.drawable.crash_ic_arrow_black_24dp);
    }

    private void initIntent() {
        filePath = getIntent().getStringExtra(IntentKey_FilePath);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
