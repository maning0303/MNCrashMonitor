package com.maning.librarycrashmonitor.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public void showProgressLoading() {
        showProgressLoading("加载中...");
    }

    public void showProgressLoading(String msg) {
        LinearLayout progress_view = findViewById(R.id.progress_view);
        TextView tv_progressbar_msg = findViewById(R.id.tv_progressbar_msg);
        if (progress_view != null) {
            progress_view.setVisibility(View.VISIBLE);
            tv_progressbar_msg.setText(msg);
            progress_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //空
                }
            });
        }
    }

    public void dismissProgressLoading() {
        LinearLayout progress_view = findViewById(R.id.progress_view);
        TextView tv_progressbar_msg = findViewById(R.id.tv_progressbar_msg);
        if (progress_view != null) {
            progress_view.setVisibility(View.GONE);
            tv_progressbar_msg.setText("加载中...");
        }
    }

}
