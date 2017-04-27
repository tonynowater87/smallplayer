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

import java.util.List;

/**
 * Created by tonyliao on 2017/4/27.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListAdapterViewHolder> {

    List<Song> mSongList;
    public SongListAdapter(Context context) {
        mSongList = MediaUtils.getAudioList(context);
    }

    @Override
    public SongListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongListAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_songlistadapter_listitem,null));
    }

    @Override
    public void onBindViewHolder(SongListAdapterViewHolder holder, int position) {
        holder.getBinding().tvSonglistadapter.setText(mSongList.get(position).getmDisplayName());
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public static class SongListAdapterViewHolder extends RecyclerView.ViewHolder {

        private LayoutSonglistadapterListitemBinding mBinding;

        public SongListAdapterViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public LayoutSonglistadapterListitemBinding getBinding() {
            return mBinding;
        }
    }
}
