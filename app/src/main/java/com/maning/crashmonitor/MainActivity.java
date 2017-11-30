package com.maning.crashmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maning.librarycrashmonitor.MCrashMonitor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void btn01(View view) {
        //手动造成一个Crash
        throw new NullPointerException("自定义异常抛出");
    }

    public void btn02(View view) {
        MCrashMonitor.startCrashListPage(this);
    }


    public void btn03(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    public void btn04(View view) {
        String extraInfo = "用户手机号码：16666666666" +
                "\n用户网络环境：wifi";
        MCrashMonitor.setCrashLogExtraInfo(extraInfo);
    }

}
