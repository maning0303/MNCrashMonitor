package com.maning.librarycrashmonitor.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.main.MCrashMonitor;
import com.maning.librarycrashmonitor.utils.StatusBarUtil;


/**
 * 展示Crash页面的
 */
public class CrashShowActivity extends CrashBaseActivity {

    private Toolbar toolbar;
    private Button btn_restart_app;
    private Button btn_crash_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_show);

        initViews();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_restart_app = (Button) findViewById(R.id.btn_restart_app);
        btn_crash_list = (Button) findViewById(R.id.btn_crash_list);

        initToolBar(toolbar, "崩溃啦~");

        btn_restart_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重启app代码
                Intent intent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CrashShowActivity.this.finish();
            }
        });

        btn_crash_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MCrashMonitor.startCrashListPage(context);
                CrashShowActivity.this.finish();
            }
        });

    }

}
