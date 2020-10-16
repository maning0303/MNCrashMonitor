package com.maning.librarycrashmonitor.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.listener.MOnItemClickListener;
import com.maning.librarycrashmonitor.ui.adapter.CrashInfoAdapter;
import com.maning.librarycrashmonitor.utils.MFileUtils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 崩溃列表的展示页面
 */
public class CrashListActivity extends CrashBaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<File> fileList;

    private Handler handler = new Handler();
    private CrashInfoAdapter crashInfoAdapter;
    private ProgressDialog progressDialog;
    private TextView btn_delete;
    private LinearLayout btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mncrash_list);
        try {
            initViews();

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                    initCrashFileList();
                }
            });
        } catch (Exception e) {

        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //设置刷新球颜色
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, Color.YELLOW, Color.RED, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

        btn_back = (LinearLayout) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_delete = (TextView) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);

    }


    private void initCrashFileList() {
        //获取日志
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCrashList();
            }
        }).start();

    }

    private void getCrashList() {
        //重新获取
        File fileCrash = new File(MFileUtils.getCrashLogPath(context));
        fileList = MFileUtils.getFileList(fileCrash);

        //排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file01, File file02) {
                try {
                    //根据修改时间排序
                    long lastModified01 = file01.lastModified();
                    long lastModified02 = file02.lastModified();
                    if (lastModified01 > lastModified02) {
                        return -1;
                    } else {
                        return 1;
                    }
                } catch (Exception e) {
                    return 1;
                }
            }
        });

        //通知页面刷新
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.hide();
                }
                initAdapter();
            }
        });
    }


    private void initAdapter() {
        if (crashInfoAdapter == null) {
            crashInfoAdapter = new CrashInfoAdapter(context, fileList);
            recyclerView.setAdapter(crashInfoAdapter);
            crashInfoAdapter.setOnItemClickLitener(new MOnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(context, CrashDetailsActivity.class);
                    File file = fileList.get(position);
                    intent.putExtra(CrashDetailsActivity.IntentKey_FilePath, file.getAbsolutePath());
                    startActivity(intent);
                }

                @Override
                public void onLongClick(View view, final int position) {
                    //弹出Dialog是否删除当前
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("是否删除当前日志?");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressDialog = ProgressDialog.show(CrashListActivity.this, "提示", "正在删除...");
                            progressDialog.show();
                            //删除单个
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    File file = fileList.get(position);
                                    MFileUtils.deleteFile(file.getPath());
                                    //重新获取
                                    getCrashList();
                                }
                            }).start();
                        }
                    });
                    builder.show();
                }
            });
        } else {
            crashInfoAdapter.updateDatas(fileList);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        initCrashFileList();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_delete) {
            //弹出Dialog是否删除全部
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否删除全部日志?");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    progressDialog = ProgressDialog.show(CrashListActivity.this, "提示", "正在删除...");
                    progressDialog.show();

                    //删除全部
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File fileCrash = new File(MFileUtils.getCrashLogPath(context));
                            MFileUtils.deleteAllFiles(fileCrash);

                            //重新获取
                            getCrashList();
                        }
                    }).start();
                }
            });
            builder.show();
        } else if (i == R.id.btn_back) {
            finish();
        }
    }
}
