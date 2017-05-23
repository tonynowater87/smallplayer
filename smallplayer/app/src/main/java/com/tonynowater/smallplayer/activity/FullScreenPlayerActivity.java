package com.tonynowater.smallplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityFullScreenPlayerBinding;
import com.tonynowater.smallplayer.u2b.U2BApiUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FullScreenPlayerActivity extends BaseActivity<ActivityFullScreenPlayerBinding> implements View.OnClickListener{
    private static final String TAG = FullScreenPlayerActivity.class.getSimpleName();
    private static final long INITIAL_DELAY = 100;
    private static final long UPDATE_PERIOD = 1000;
    private Handler mHandler = new Handler();
    private PlaybackStateCompat mLastPlaybackState;

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: ");

        updatePlaybackState(state);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                scheduledSeekUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                stopSeekbarUpdate();
                break;
            default:
                Log.d(TAG, "updatePlaybackState: unhandle state " + state.getState());
        }
    }

    @Override
    protected void onSessionDestroyed() {
        Log.d(TAG, "onSessionDestroyed: ");
    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        Log.d(TAG, "onMetadataChanged: ");
        updateUI(metadata);
    }

    private void updateUI(MediaMetadataCompat metadata) {
        mBinding.tvEndTextActivityFullScreenPlayer.setText(U2BApiUtil.formateU2BDurationToString(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        mBinding.seekbarActivityFullScreenPlayer.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        mBinding.tvTitleActivityFullScreenPlayer.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mBinding.tvArtistActivityFullScreenPlayer.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        mBinding.tvAlbumDescriptionActivityFullScreenPlayer.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION));
        Glide.with(getApplicationContext()).load(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)).into((ImageView) mBinding.rlRootActivityFullScreenPlayer.findViewById(R.id.imageview_background_activity_full_screen_player));
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mBinding.tvStartTextActivityFullScreenPlayer.setText(DateUtils.formatElapsedTime(progress/1000));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            stopSeekbarUpdate();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTransportControls.seekTo(seekBar.getProgress());
            scheduledSeekUpdate();
        }
    };

    private ScheduledFuture<?> mScheduledFuture;
    private ScheduledExecutorService mSceduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_full_screen_player;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        mBinding.seekbarActivityFullScreenPlayer.setOnSeekBarChangeListener(mOnSeekChangedListener);
        mBinding.ivPreviousActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivPlayPauseActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivNextActivityFullScreenPlayer.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        stopSeekbarUpdate();
        mSceduledExecutorService.shutdown();
        super.onDestroy();
    }

    private void scheduledSeekUpdate() {
        stopSeekbarUpdate();
        if (!mSceduledExecutorService.isShutdown()) {
            mScheduledFuture = mSceduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(mRunnable);
                }
            }, INITIAL_DELAY, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduledFuture != null) {
            mScheduledFuture.cancel(false);// TODO: 2017/5/22
        }
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            long timeDelta = SystemClock.elapsedRealtime() - mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        else {
            Log.d(TAG, "updateProgress: 不更新SeekBar的狀態 " + mLastPlaybackState.getState());
        }

        mBinding.seekbarActivityFullScreenPlayer.setProgress((int) currentPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_previous_activity_full_screen_player:
                mTransportControls.skipToPrevious();
                break;
            case R.id.iv_play_pause__activity_full_screen_player:
                onPressPlayButton();
                break;
            case R.id.iv_next_activity_full_screen_player:
                mTransportControls.skipToNext();
                break;
        }
    }

    private void onPressPlayButton() {
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
            mTransportControls.pause();
        } else {
            mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
            mTransportControls.play();
        }
    }
}
