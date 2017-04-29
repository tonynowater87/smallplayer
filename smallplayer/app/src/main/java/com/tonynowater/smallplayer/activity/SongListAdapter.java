package com.tonynowater.smallplayer.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.MediaUtils;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

/**
 * Created by tonyliao on 2017/4/27.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListAdapterViewHolder> {

    private List<Song> mSongList;
    private OnClickSomething<Song> mOnClickSongListener;

    public SongListAdapter(Context context, OnClickSomething mOnClickSongListener) {
        mSongList = MediaUtils.getAudioList(context);
        this.mOnClickSongListener = mOnClickSongListener;
    }

    @Override
    public SongListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongListAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_songlistadapter_listitem,null), mOnClickSongListener, mSongList);
    }

    @Override
    public void onBindViewHolder(SongListAdapterViewHolder holder, int position) {
        holder.getBinding().tvSonglistadapter.setText(mSongList.get(position).getmDisplayName());
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public static class SongListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private LayoutSonglistadapterListitemBinding mBinding;
        private OnClickSomething mOnClickSongListener;
        private List<Song> mSongList;

        public SongListAdapterViewHolder(View itemView, OnClickSomething mOnClickSongListener, List<Song> mSongList) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mOnClickSongListener = mOnClickSongListener;
            this.mSongList = mSongList;
            mBinding.tvSonglistadapter.setOnClickListener(this);
        }

        public LayoutSonglistadapterListitemBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            mOnClickSongListener.onClick(mSongList.get(getAdapterPosition()));
        }
    }
}
