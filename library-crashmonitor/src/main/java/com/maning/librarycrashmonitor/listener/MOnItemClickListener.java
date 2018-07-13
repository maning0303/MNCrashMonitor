package com.maning.librarycrashmonitor.listener;

import android.view.View;

/**
 * Created by maning on 2017/4/20.
 */

public interface MOnItemClickListener {
    void onItemClick(View view, int position);

    void onLongClick(View view, int position);
}
