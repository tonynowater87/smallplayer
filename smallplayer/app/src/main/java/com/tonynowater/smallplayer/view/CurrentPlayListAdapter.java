package com.tonynowater.smallplayer.view;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutCurrentPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowtaer87.myutil.TimeUtil;

/**
 * 目前播放清單List Adapter
 * Created by tonynowater on 2017/6/8.
 */
public class CurrentPlayListAdapter extends BasePlayableFragmentAdapter<MediaBrowserCompat.MediaItem, LayoutCurrentPlayListAdapterBinding> {

    public CurrentPlayListAdapter(OnClickSomething mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected boolean isFootViewVisible() {
        return false;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_current_play_list_adapter;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        Bundle extras = mDataList.get(position).getDescription().getExtras();
        holder.getBinding().tvSongTitleSonglistadapter.setText(extras.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        holder.getBinding().tvSongArtistSonglistadapter.setText(extras.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        holder.getBinding().tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration((int) extras.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        if (!TextUtils.isEmpty(extras.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))) {
            Glide.with(mContext).load(extras.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }

        if (MetaDataCustomKeyDefine.isLocal(extras.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL))) {
            holder.getBinding().ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.local_music_icon));
        } else {
            holder.getBinding().ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        }
    }
}
