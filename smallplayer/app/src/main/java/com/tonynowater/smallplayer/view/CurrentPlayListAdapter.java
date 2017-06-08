package com.tonynowater.smallplayer.view;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutCurrentPlayListAdapterBinding;
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
    protected int getItemResourceId() {
        return R.layout.layout_current_play_list_adapter;
    }

    @Override
    protected boolean isFootviewVisible() {
        return false;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().tvSongTitleSonglistadapter.setText(mDataList.get(position).getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        holder.getBinding().tvSongArtistSonglistadapter.setText(mDataList.get(position).getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        holder.getBinding().tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration((int) mDataList.get(position).getDescription().getExtras().getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        if (!TextUtils.isEmpty(mDataList.get(position).getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))) {
            Glide.with(mContext).load(mDataList.get(position).getDescription().getExtras().getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }
    }
}
