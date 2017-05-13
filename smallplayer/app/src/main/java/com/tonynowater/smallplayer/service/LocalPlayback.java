package com.tonynowater.smallplayer.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by tonyliao on 2017/5/12.
 */
public class LocalPlayback implements Playback
        , AudioManager.OnAudioFocusChangeListener
        , MediaPlayer.OnCompletionListener
        , MediaPlayer.OnErrorListener
        , MediaPlayer.OnPreparedListener
        , MediaPlayer.OnSeekCompleteListener
        , MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = LocalPlayback.class.getSimpleName();
    private Context mContext;
    private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private int mState;
    private int mCurrentPosition;
    private MediaPlayer mMediaPlayer;
    private MusicProvider mMusicProvider;

    public LocalPlayback(Context mContext, MusicProvider mMusicProvider) {
        this.mContext = mContext;
        this.mMusicProvider = mMusicProvider;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mWifiLock = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange: " + focusChange);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "onBufferingUpdate: " + percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion:");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: " + what);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared:");
        mState = PlaybackState.STATE_PLAYING;
        mMediaPlayer.start();

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete:");
    }

    @Override
    public void setState(int state) {
        Log.d(TAG, "setState:" + state);
    }

    @Override
    public int getState() {
        Log.d(TAG, "getState:" + mState);
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getCurrentStreamPosition() {
        return 0;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {

    }

    @Override
    public void updateLastKnownStreamPosition() {

    }

    @Override
    public void play(MediaMetadata mediaMetadata) {
        Log.d(TAG, "play:" + mediaMetadata.getString(MusicProvider.CUSTOM_METADATA_TRACK_SOURCE));
        createMediaPlayerIfNeeded();
        String source = mediaMetadata.getString(MusicProvider.CUSTOM_METADATA_TRACK_SOURCE);
        try {
            mState = PlaybackState.STATE_BUFFERING;
            mMediaPlayer.setDataSource(source);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }

    @Override
    public void pause() {
        if (mState == PlaybackState.STATE_PLAYING) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
        }

        mState = PlaybackState.STATE_PAUSED;
    }


    @Override
    public void stop(boolean notifyListeners) {
        Log.d(TAG, "stop:" + notifyListeners);
        mState = PlaybackState.STATE_STOPPED;
        releaseResource();
    }

    private void releaseResource() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void seekTo(int position) {

    }

    @Override
    public void setCurrentMediaId(String mediaId) {

    }

    @Override
    public String getCurrentMediaId() {
        return null;
    }

    @Override
    public void setCallback(Callback callback) {

    }
}
