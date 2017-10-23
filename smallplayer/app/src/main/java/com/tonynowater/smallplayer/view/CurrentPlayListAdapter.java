package com.tonynowater.smallplayer.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutCurrentPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.TimeUtil;

/**
 * 目前播放清單List Adapter
 * Created by tonynowater on 2017/6/8.
 */
public class CurrentPlayListAdapter extends BasePlayableFragmentAdapter<MediaBrowserCompat.MediaItem, LayoutCurrentPlayListAdapterBinding> {

    private ColorStateList sColorStatePlaying;
    private int mPlayPosition = 0;
    private boolean mIsPlaying = false;
    private AnimationDrawable animation;

    public CurrentPlayListAdapter(OnClickSomething mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_current_play_list_adapter;
    }

    @Override
    protected void onBindItem(LayoutCurrentPlayListAdapterBinding binding, MediaBrowserCompat.MediaItem item, int position) {
        Bundle extras = item.getDescription().getExtras();
        binding.tvSongTitleSonglistadapter.setText(extras.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        binding.tvSongArtistSonglistadapter.setText(extras.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        binding.tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration((int) extras.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        if (!TextUtils.isEmpty(extras.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))) {
            Glide.with(mContext).load(extras.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }

        if (MetaDataCustomKeyDefine.isLocal(extras.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL))) {
            binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.local_music_icon));
        } else {
            binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        }

        if (mPlayPosition == position) {//目前播放的位置
            binding.ivAnimation.setVisibility(View.VISIBLE);
            binding.ivAnimation.setImageDrawable(getDrawableByState(mContext, position));
            binding.tvSongArtistSonglistadapter.setTextColor(ContextCompat.getColor(mContext, R.color.icon_equalizer_playing));
            binding.tvSongTitleSonglistadapter.setTextColor(ContextCompat.getColor(mContext, R.color.icon_equalizer_playing));
            binding.tvDurationSonglistadapter.setTextColor(ContextCompat.getColor(mContext, R.color.icon_equalizer_playing));
        } else {
            binding.ivAnimation.setVisibility(View.GONE);
            binding.tvSongArtistSonglistadapter.setTextColor(ContextCompat.getColor(mContext, R.color.default_textview_color));
            binding.tvSongTitleSonglistadapter.setTextColor(ContextCompat.getColor(mContext, R.color.default_textview_color));
            binding.tvDurationSonglistadapter.setTextColor(ContextCompat.getColor(mContext, R.color.default_textview_color));
        }
    }

    @Override
    protected boolean supportFooter() {
        return false;
    }

    /** 更新目前歌曲的播放狀態 */
    public void onPlaybackStateChanged(PlaybackStateCompat mLastPlaybackState) {
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mIsPlaying = true;
        } else {
            mIsPlaying = false;
        }

        notifyItemChanged(mPlayPosition);
    }

    /** 更新目前播放歌曲的位置 */
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        notifyItemChanged(mPlayPosition);
        int size = mDataList.size();
        MediaBrowserCompat.MediaItem mediaItem;
        String source1, source2;
        for (int i = 0; i < size; i++) {
            mediaItem = mDataList.get(i);
            source1 = mediaItem.getDescription().getExtras().getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE);
            source2 = metadata.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE);
            if (TextUtils.equals(source1, source2)) {
                notifyItemChanged(mPlayPosition = i);
                break;
            }
        }
    }

    private void initializeColorStateLists(Context ctx) {
        sColorStatePlaying = ColorStateList.valueOf(ctx.getResources().getColor(R.color.icon_equalizer_playing));
    }

    /** 依目前播放狀態回傳Drawable */
    public Drawable getDrawableByState(Context context, int position) {
        if (sColorStatePlaying == null) {
            initializeColorStateLists(context);
        }

        if (position == mPlayPosition) {

            if (mIsPlaying) {
                if (animation == null) {
                    animation = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.ic_equalizer_white_36dp);
                    DrawableCompat.setTintList(animation, sColorStatePlaying);
                    animation.start();
                }
                return animation;
            } else {
                animation = null;
                Drawable playDrawable = ContextCompat.getDrawable(context, R.drawable.ic_equalizer1_white_36dp);
                DrawableCompat.setTintList(playDrawable, sColorStatePlaying);
                return playDrawable;
            }
        }

        return null;
    }
}
