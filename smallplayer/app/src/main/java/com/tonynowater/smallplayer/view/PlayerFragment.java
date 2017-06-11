package com.tonynowater.smallplayer.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.activity.FullScreenPlayerActivity;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.FragmentPlayerBinding;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.DialogUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * 畫面底部的播放Fragment
 * Created by tonynowater on 2017/5/20.
 */
public class PlayerFragment extends BaseFragment<FragmentPlayerBinding> {
    private static final String TAG = PlayerFragment.class.getSimpleName();

    private static final long INITIAL_DELAY = 100;
    private static final long UPDATE_PERIOD = 1000;
    private static final int DEFAULT_PROGRESS = 0;
    private PlaybackStateCompat mPlaybackStateCompat;
    private Handler mHandler = new Handler();
    private ScheduledExecutorService mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduledFuture;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            long state = mPlaybackStateCompat != null ? mPlaybackStateCompat.getState() : PlaybackStateCompat.STATE_NONE;

            switch (v.getId()) {
                case R.id.buttonPlay:
                    if (state == PlaybackStateCompat.STATE_PAUSED
                            || state == PlaybackStateCompat.STATE_STOPPED
                            || state == PlaybackStateCompat.STATE_NONE) {
                        play();
                    } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                        pause();
                    }
                    break;
                case R.id.buttonNext:
                    skipToNext();
                    break;
                case R.id.buttonPrevious:
                    skipToPrevious();
                    break;
                case R.id.buttonAction:
                    DialogUtil.showChangePlayListDialog((BaseActivity) getActivity());
                    break;
                case R.id.ll_song_info_fragment_player:
                    startActivity(new Intent(getActivity(), FullScreenPlayerActivity.class));
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mBinding.buttonPlay.setOnClickListener(mOnClickListener);
        mBinding.buttonNext.setOnClickListener(mOnClickListener);
        mBinding.buttonPrevious.setOnClickListener(mOnClickListener);
        mBinding.buttonAction.setOnClickListener(mOnClickListener);
        mBinding.llSongInfoFragmentPlayer.setOnClickListener(mOnClickListener);
    }

    /**
     * 設定預設的歌曲文字圖片顯示
     */
    private void initialUI() {
        RealmUtils realmUtils = new RealmUtils();
        List<PlayListSongEntity> playListSongEntities = realmUtils.queryPlayListSongByListIdSortByPosition(realmUtils.queryCurrentPlayListID());
        if (playListSongEntities.size() > 0) {
            PlayListSongEntity playListSongEntity = playListSongEntities.get(0);
            setUIByPlayListSongEntity(playListSongEntity);
        }
        realmUtils.close();
    }

    private void setUIByPlayListSongEntity(PlayListSongEntity playListSongEntity) {
        mBinding.textViewSongNameValue.setText(playListSongEntity.getTitle());
        mBinding.textViewSongArtistValue.setText(playListSongEntity.getArtist());
        if (playListSongEntity.getAlbumArtUri() == null) {
            Glide.with(MyApplication.getContext()).load(R.mipmap.ic_launcher).into(mBinding.imageviewThumb);
        } else {
            Glide.with(MyApplication.getContext()).load(playListSongEntity.getAlbumArtUri()).into(mBinding.imageviewThumb);
        }
    }

    private void setUIWhenPlayListSongEntityNull() {
        mBinding.textViewSongNameValue.setText(getString(R.string.dash));
        mBinding.textViewSongArtistValue.setText(getString(R.string.dash));
        mBinding.progressBar.setProgress(DEFAULT_PROGRESS);
        Glide.with(MyApplication.getContext()).load(R.mipmap.ic_launcher).into(mBinding.imageviewThumb);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_player;
    }

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: " + state);
        if (state == null) {
            return;
        }
        mPlaybackStateCompat = state;
        PlayerFragment.this.updateState(state);
    }

    /**
     * 更新播放的狀態
     *
     * @param state
     */
    private void updateState(PlaybackStateCompat state) {
        Log.d(TAG, "updateState : " + mPlaybackStateCompat);

        if (mPlaybackStateCompat == null) {
            return;
        }

        mBinding.buttonPlay.setVisibility(View.VISIBLE);
        mBinding.progressBar.setVisibility(View.GONE);
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_NONE:
                mBinding.buttonPlay.setImageDrawable(MyApplication.getContext().getDrawable(android.R.drawable.ic_media_play));
                startScheduledUpdateProgress();
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                mBinding.buttonPlay.setImageDrawable(MyApplication.getContext().getDrawable(android.R.drawable.ic_media_pause));
                startScheduledUpdateProgress();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mBinding.buttonPlay.setImageDrawable(MyApplication.getContext().getDrawable(android.R.drawable.ic_media_play));
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                mBinding.buttonPlay.setImageDrawable(MyApplication.getContext().getDrawable(android.R.drawable.ic_media_play));
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                mBinding.buttonPlay.setVisibility(View.GONE);
                mBinding.progressBar.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        Log.d(TAG, "updateState: " + state.getState());

        if (mPlaybackStateCompat.getExtras() != null && mPlaybackStateCompat.getExtras().getBoolean(PlayMusicService.BUNDLE_KEY_CHANGE_NO_SONG_PLAYLIST)) {
            //切換到沒歌曲的播放清單將UI文字圖片設回預設值
            Glide.with(MyApplication.getContext()).load(R.mipmap.ic_launcher).into(mBinding.imageviewThumb);
            mBinding.textViewSongNameValue.setText(getString(R.string.dash));
            mBinding.textViewSongArtistValue.setText(getString(R.string.dash));
        }
    }

    private void startScheduledUpdateProgress() {
        stopScheduledUpdateProgress();
        if (!mScheduledExecutorService.isShutdown()) {
            mScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(mRunnable);
                }
            }, INITIAL_DELAY, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
        }
    }

    private void stopScheduledUpdateProgress() {
        if (mScheduledFuture != null) {
            mScheduledFuture.cancel(false);
        }
    }

    @Override
    public void onDestroy() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onSessionDestroyed() {

    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataChanged: " + (metadata == null));
        if (metadata == null) {
            setUIWhenPlayListSongEntityNull();
            return;
        }

        mBinding.progressView.setmMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        mBinding.textViewSongNameValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mBinding.textViewSongArtistValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        if (metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) == null) {
            Glide.with(MyApplication.getContext()).load(R.mipmap.ic_launcher).into(mBinding.imageviewThumb);
        } else {
            Glide.with(MyApplication.getContext()).load(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).into(mBinding.imageviewThumb);
        }
    }

    @Override
    protected void onFragmentMediaConnected() {
        initialUI();
    }

    @Override
    protected void pause() {
        super.pause();
        mBinding.buttonPlay.setImageDrawable(getContext().getDrawable(android.R.drawable.ic_media_play));
    }

    @Override
    protected void play() {
        super.play();
        mBinding.buttonPlay.setImageDrawable(getContext().getDrawable(android.R.drawable.ic_media_pause));
    }

    private void updateProgress() {

        if (mPlaybackStateCompat == null) {
            return;
        }

        long currentTime = mPlaybackStateCompat.getPosition();
        if (mPlaybackStateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
            long deltaTime = SystemClock.elapsedRealtime() - mPlaybackStateCompat.getLastPositionUpdateTime();
            currentTime += (int) deltaTime * mPlaybackStateCompat.getPlaybackSpeed();
        }

        mBinding.progressView.setmProgress((int) currentTime);
    }
}
