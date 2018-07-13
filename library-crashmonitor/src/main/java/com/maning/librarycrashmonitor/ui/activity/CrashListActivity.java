package com.maning.librarycrashmonitor.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.listener.MOnItemClickListener;
import com.maning.librarycrashmonitor.ui.adapter.CrashInfoAdapter;
import com.maning.librarycrashmonitor.utils.MFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 崩溃列表的展示页面
 */
public class CrashListActivity extends CrashBaseActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView recycleView_search;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<File> fileList;
    private List<File> fileListSearch = new ArrayList<>();

    private Handler handler = new Handler();
    private CrashInfoAdapter crashInfoAdapter;
    private CrashInfoAdapter crashInfoAdapter_search;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_list);

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recycleView_search = (RecyclerView) findViewById(R.id.recycleView_search);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        initToolBar(toolbar, "崩溃日志列表");

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycleView_search.setItemAnimator(new DefaultItemAnimator());
        recycleView_search.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //设置刷新球颜色
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK, Color.YELLOW, Color.RED, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crash_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //刷新界面
        fileListSearch.clear();

        if (TextUtils.isEmpty(newText)) {
            recycleView_search.setVisibility(View.GONE);
        } else {
            recycleView_search.setVisibility(View.VISIBLE);
            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                String name = file.getName();
                if (name.contains(newText)) {
                    fileListSearch.add(file);
                }
            }
        }
        initSearchAdapter();
        return true;
    }

    private void initSearchAdapter() {
        if (crashInfoAdapter_search == null) {
            crashInfoAdapter_search = new CrashInfoAdapter(context, fileListSearch);
            recycleView_search.setAdapter(crashInfoAdapter_search);
            crashInfoAdapter_search.setOnItemClickLitener(new MOnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(context, CrashDetailsActivity.class);
                    File file = fileList.get(position);
                    intent.putExtra(CrashDetailsActivity.IntentKey_FilePath, file.getAbsolutePath());
                    startActivity(intent);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            });
        } else {
            crashInfoAdapter_search.updateDatas(fileListSearch);
        }
    }

}
