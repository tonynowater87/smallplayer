package com.tonynowater.smallplayer.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/20.
 */
public class BaseU2BFragmentAdapter<K, T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter <T> {

    protected static final int NORMAL_VIEWTYPE = 1;
    protected static final int FOOTER_VIEWTYPE = 2;

    protected List<K> mDataList;
    protected OnClickSomething<Song> mOnClickSongListener;

    protected BaseU2BFragmentAdapter(OnClickSomething<Song> mOnClickSongListener) {
        this.mDataList = new ArrayList<>();
        this.mOnClickSongListener = mOnClickSongListener;
    }

    protected BaseU2BFragmentAdapter(List<K> mDataList, OnClickSomething<Song> mOnClickSongListener) {

        this.mDataList = mDataList;
        this.mOnClickSongListener = mOnClickSongListener;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

//    @Override
//    public int getItemCount() {
//        return mDataList.size() + 1;
//    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position + 1 == getItemCount()) {
//            return FOOTER_VIEWTYPE;
//        }
//
//        return NORMAL_VIEWTYPE;
//    }

    public void setDataSource (List<K> dataSource) {
        mDataList = new ArrayList<>(dataSource);
    }

    protected FooterViewHolder getFooterViewHolder(Context context) {
        return new FooterViewHolder(LayoutInflater.from(context).inflate(R.layout.list_footer, null));
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
