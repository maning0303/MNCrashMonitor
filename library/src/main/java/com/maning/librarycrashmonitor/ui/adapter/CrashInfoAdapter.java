package com.maning.librarycrashmonitor.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maning.librarycrashmonitor.R;
import com.maning.librarycrashmonitor.listener.OnItemClickListener;

import java.io.File;
import java.util.List;

/**
 * Created by maning on 2017/4/20.
 */

public class CrashInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<File> fileList;

    private OnItemClickListener mOnItemClickLitener;

    public CrashInfoAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void updateDatas(List<File> fileList){
        this.fileList = fileList;
        notifyDataSetChanged();;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = layoutInflater.inflate(R.layout.item_crash, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            File file = fileList.get(position);
            myViewHolder.textView.setText(file.getName());
            myViewHolder.textView_path.setText(file.getAbsolutePath());


            if (mOnItemClickLitener != null) {
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickLitener.onItemClick(view, position);
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView textView_path;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            textView_path = (TextView) itemView.findViewById(R.id.textView_path);

        }
    }

}
