package com.tonynowater.smallplayer.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutCurrentPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * 目前播放清單List Adapter
 * Created by tonynowater on 2017/6/8.
 */
public class CurrentPlayListAdapter extends BasePlayableFragmentAdapter<CurrentPlayEntity, LayoutCurrentPlayListAdapterBinding> {

    private ColorStateList sColorStatePlaying;
    private int mPlayPosition = 0;
    private boolean mIsPlaying = false;
    private AnimationDrawable animation;

    public CurrentPlayListAdapter(OnClickSomething mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.song;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_current_play_list_adapter;
    }

    @Override
    protected void onBindItem(LayoutCurrentPlayListAdapterBinding binding, CurrentPlayEntity item, int position) {
        String uri = item.getArtUri();
        if (!TextUtils.isEmpty(uri)) {
            Glide.with(mContext).load(uri).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }

        if (MetaDataCustomKeyDefine.isLocal(item.getLocal())) {
            binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.local_music_icon));
        } else {
            binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        }

        if (mPlayPosition == position) {//目前播放的位置
            binding.ivAnimation.setVisibility(View.VISIBLE);
            binding.ivAnimation.setImageDrawable(getDrawableByState(mContext, position));
            if (binding.ivAnimation.getDrawable() instanceof AnimationDrawable) {
                ((AnimationDrawable) binding.ivAnimation.getDrawable()).start();
            }
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

    /**
     * 更新目前歌曲的播放狀態
     */
    public void onPlaybackStateChanged(PlaybackStateCompat mLastPlaybackState) {
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mIsPlaying = true;
        } else {
            mIsPlaying = false;
        }

        notifyItemChanged(mPlayPosition);
    }

    /**
     * 更新目前播放歌曲的位置
     */
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        notifyItemChanged(mPlayPosition);
        int size = mDataList.size();
        CurrentPlayEntity playEntity;
        String source1, source2;
        for (int i = 0; i < size; i++) {
            playEntity = mDataList.get(i);
            source1 = playEntity.getSource();
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

    /**
     * 依目前播放狀態回傳Drawable
     */
    public Drawable getDrawableByState(Context context, int position) {
        if (sColorStatePlaying == null) {
            initializeColorStateLists(context);
        }

        if (position == mPlayPosition) {
            if (mIsPlaying) {
                animation = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.ic_equalizer_white_36dp);
                DrawableCompat.setTintList(animation, sColorStatePlaying);
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
