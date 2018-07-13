package com.maning.crashmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maning.librarycrashmonitor.MCrashMonitor;
import com.maning.librarycrashmonitor.utils.MActivityListUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    /**
     * 抛出自定义异常
     */
    private Button mBtnThrowException;
    /**
     * 日志列表页面
     */
    private Button mBtnLogList;
    /**
     * 下一页
     */
    private Button mBtnNextPage;
    /**
     * 添加额外的Log信息
     */
    private Button mBtnAddExtraInfo;
    /**
     * 获取日志保存路径
     */
    private Button mBtnGetLogPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnThrowException = (Button) findViewById(R.id.btn_throw_exception);
        mBtnThrowException.setOnClickListener(this);
        mBtnLogList = (Button) findViewById(R.id.btn_log_list);
        mBtnLogList.setOnClickListener(this);
        mBtnNextPage = (Button) findViewById(R.id.btn_next_page);
        mBtnNextPage.setOnClickListener(this);
        mBtnAddExtraInfo = (Button) findViewById(R.id.btn_add_extra_info);
        mBtnAddExtraInfo.setOnClickListener(this);
        mBtnGetLogPath = (Button) findViewById(R.id.btn_get_log_path);
        mBtnGetLogPath.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_throw_exception:
                throwException();
                break;
            case R.id.btn_log_list:
                //打开日志列表
                MCrashMonitor.startCrashListPage(this);
                break;
            case R.id.btn_next_page:
                startActivity(new Intent(this, SecondActivity.class));
                break;
            case R.id.btn_add_extra_info:
                String extraInfo = "用户手机号码：16666666666" +
                        "\n用户网络环境：xxx";
                MCrashMonitor.setCrashLogExtraInfo(extraInfo);
                break;
            case R.id.btn_get_log_path:
                String crashLogFilesPath = MCrashMonitor.getCrashLogFilesPath(this);
                Toast.makeText(this, "崩溃日志文件夹的路径：" + crashLogFilesPath, Toast.LENGTH_SHORT).show();
                textView.setText("崩溃日志文件夹的路径：\n" + crashLogFilesPath);
                break;
        }
    }

    private void throwException() {
        //手动造成一个Crash
        throw new NullPointerException("自定义异常抛出");
    }
}
