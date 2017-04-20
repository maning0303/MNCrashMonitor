package com.maning.librarycrashmonitor.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.maning.librarycrashmonitor.R;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_details);

        initIntent();

        initViews();

        initDatas();

    }

    private void initDatas() {
        try {
            //读取数据
            StringBuffer sb = new StringBuffer();
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            content = sb.toString();
            textView.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initViews() {
        textView = (TextView) findViewById(R.id.textView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        initToolBar(toolbar,"崩溃详情",R.drawable.crash_ic_arrow_black_24dp);
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
}
