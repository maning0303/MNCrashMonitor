package com.maning.librarycrashmonitor.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.utils.StatusBarUtil;

public class CrashBaseActivity extends AppCompatActivity {

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_base);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.crash_tool_bar_color), 50);
        context = this;
    }

    public void initToolBar(Toolbar toolbar, String title, int leftIcon) {
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(leftIcon);
        setSupportActionBar(toolbar);
    }

    public void initToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

}
