package com.maning.librarycrashmonitor.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.utils.MStatusBarUtils;

/**
 * @author maning
 */
public class CrashBaseActivity extends AppCompatActivity {

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //设置状态栏
        MStatusBarUtils.setColor(this, getResources().getColor(R.color.crash_tool_bar_color), 50);
    }

    public void initToolBar(Toolbar toolbar, String title, int leftIcon) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(leftIcon);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

}
