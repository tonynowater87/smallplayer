package com.tonynowater.smallplayer.fragment.songlist;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.SongPlayManager;

import java.io.File;

/**
 * 本地音樂Adapter
 * Created by tonyliao on 2017/4/27.
 */
public class SongListAdapter extends BasePlayableFragmentAdapter<Song, LayoutSonglistadapterListitemBinding> {

    public SongListAdapter(Context context, OnClickSomething mOnClickSongListener) {
        super(mOnClickSongListener);
        mDataList = SongPlayManager.getInstance(context).getSongList();
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_songlistadapter_listitem;
    }

    @Override
    protected boolean isFootviewVisible() {
        return false;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().tvSongArtistSonglistadapter.setText(mDataList.get(position).getmArtist());
        holder.getBinding().tvSongTitleSonglistadapter.setText(mDataList.get(position).getmTitle());
        holder.getBinding().tvDurationSonglistadapter.setText(mDataList.get(position).getFormatDuration());
        if (!TextUtils.isEmpty(mDataList.get(position).getmAlbumObj().getmAlbumArt())) {
            Glide.with(mContext).load(Uri.fromFile(new File(mDataList.get(position).getmAlbumObj().getmAlbumArt()))).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }
    }
}
