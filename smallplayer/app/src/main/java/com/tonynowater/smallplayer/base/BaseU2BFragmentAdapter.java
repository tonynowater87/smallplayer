package com.tonynowater.smallplayer.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony10532 on 2017/5/20.
 */
public class BaseU2BFragmentAdapter<K, T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter <T>{

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

    public void setDataSource (List<K> dataSource) {
        mDataList = new ArrayList<>(dataSource);
    }
}
