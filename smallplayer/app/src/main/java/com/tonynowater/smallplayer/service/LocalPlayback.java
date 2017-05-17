package com.tonynowater.smallplayer.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
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
    private PlayMusicService mPlayMusicService;
    private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private int mState;
    private int mCurrentPosition;
    private int mCurrentTrackPosition;
    private MediaPlayer mMediaPlayer;
    private MusicProvider mMusicProvider;
    private Playback.Callback mPlaybackCallback;

    public LocalPlayback(PlayMusicService mPlayMusicService, MusicProvider mMusicProvider, Playback.Callback mPlaybackCallback) {
        this.mPlayMusicService = mPlayMusicService;
        this.mMusicProvider = mMusicProvider;
        this.mPlaybackCallback = mPlaybackCallback;
        mAudioManager = (AudioManager) mPlayMusicService.getSystemService(Context.AUDIO_SERVICE);
        mWifiLock = ((WifiManager) mPlayMusicService.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
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
        mPlaybackCallback.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: " + what);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = PlaybackStateCompat.STATE_PLAYING;
        Log.d(TAG, "onPrepared:" + mState);
        mMediaPlayer.start();
        mPlaybackCallback.onPlaybackStateChanged();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "onSeekComplete:");
        mState = PlaybackState.STATE_PLAYING;
        mMediaPlayer.start();
        mPlaybackCallback.onPlaybackStateChanged();
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
    public void play(int trackPosition) {

        if (mCurrentTrackPosition != trackPosition) {
            Log.d(TAG, String.format("before track : %s \t coming track : %s",mCurrentTrackPosition,trackPosition));
            mCurrentPosition = 0;
        }

        if (mCurrentPosition != 0) {
            Log.d(TAG, "pause and play position : " + trackPosition);
            mMediaPlayer.seekTo(mCurrentPosition);
            return;
        }

        createMediaPlayerIfNeeded();
        MediaMetadataCompat mediaMetadataCompat = mMusicProvider.getPlayList(trackPosition);
        String source = mediaMetadataCompat.getString(MusicProvider.CUSTOM_METADATA_TRACK_SOURCE);
        try {
            Log.d(TAG, String.format("PlaySize:%d\tPlayPosition:%d\tPlaySong:%s",mMusicProvider.getPlayListSize(),trackPosition,mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE)));
            mCurrentTrackPosition = trackPosition;
            mCurrentPosition = 0;
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(source);
            mMediaPlayer.prepareAsync();
            mPlaybackCallback.onPlaybackStateChanged();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "play error: " + e.toString() );
        }
    }

    private void createMediaPlayerIfNeeded() {
        Log.d(TAG, "createMediaPlayerIfNeeded. needed? " + (mMediaPlayer==null));
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnErrorListener(this);
        } else {
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
        mPlaybackCallback.onPlaybackStateChanged();
    }


    @Override
    public void stop(boolean notifyListeners) {
        Log.d(TAG, "stop:" + notifyListeners);
        mState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners) {
            mPlaybackCallback.onPlaybackStateChanged();
        }

        mCurrentPosition = 0;
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
}
