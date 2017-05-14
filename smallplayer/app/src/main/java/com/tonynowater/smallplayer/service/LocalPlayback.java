package com.tonynowater.smallplayer.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
    private int mSongTrackPosition;
    private MediaPlayer mMediaPlayer;
    private MusicProvider mMusicProvider;
    private PlaybackCallback mPlaybackCallback;

    public LocalPlayback(Context mContext, MusicProvider mMusicProvider, PlaybackCallback mPlaybackCallback) {
        this.mContext = mContext;
        this.mMusicProvider = mMusicProvider;
        this.mPlaybackCallback = mPlaybackCallback;
        mSongTrackPosition = 0;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mWifiLock = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
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
        mSongTrackPosition++;
        if (mSongTrackPosition >= mMusicProvider.getPlayListSize()) {
            mSongTrackPosition = 0;
        }
        play();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: " + what);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared:");
        mState = PlaybackStateCompat.STATE_PLAYING;
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
        return mState;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer == null ? false : mMediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentStreamPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : mCurrentPosition;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {

    }

    @Override
    public void updateLastKnownStreamPosition() {

    }

    @Override
    public void play() {
        Log.d(TAG, "play size : " + mMusicProvider.getPlayListSize());
        Log.d(TAG, "play position : " + mSongTrackPosition);
        createMediaPlayerIfNeeded();
        MediaMetadataCompat mediaMetadataCompat = mMusicProvider.getPlayList(mSongTrackPosition);
        if (mediaMetadataCompat == null) {
            Log.d(TAG, "no list to play:");
            stop(true);
            return;
        }

        Log.d(TAG, "play: " + mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));

        String source = mediaMetadataCompat.getString(MusicProvider.CUSTOM_METADATA_TRACK_SOURCE);
        try {
            mPlaybackCallback.onPlaybackStateChanged(mediaMetadataCompat);
            mMediaPlayer.setDataSource(source);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "play: " + e.toString() );
        }
    }

    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            Log.d(TAG, "createMediaPlayerIfNeeded: initial");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
        } else {
            Log.d(TAG, "createMediaPlayerIfNeeded: reset");
            mMediaPlayer.reset();
        }
    }

    @Override
    public void pause() {
        if (mState == PlaybackStateCompat.STATE_PLAYING) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
        }

        mState = PlaybackStateCompat.STATE_PAUSED;
        mPlaybackCallback.onPlaybackStateChanged(null);
    }


    @Override
    public void stop(boolean notifyListeners) {
        Log.d(TAG, "stop:" + notifyListeners);
        mState = PlaybackStateCompat.STATE_STOPPED;
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
