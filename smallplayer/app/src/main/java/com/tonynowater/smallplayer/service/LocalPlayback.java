package com.tonynowater.smallplayer.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
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
    private int mState = PlaybackStateCompat.STATE_NONE;
    private int mCurrentPosition;
    private int mCurrentTrackPosition;
    private int mSongDuration = 0;
    private MediaPlayer mMediaPlayer;
    private MusicProvider mMusicProvider;
    private Playback.Callback mPlaybackCallback;
    private Equalizer mEqualizer;

    public LocalPlayback(PlayMusicService mPlayMusicService, MusicProvider mMusicProvider, Playback.Callback mPlaybackCallback) {
        this.mPlayMusicService = mPlayMusicService;
        this.mMusicProvider = mMusicProvider;
        this.mPlaybackCallback = mPlaybackCallback;
        mAudioManager = (AudioManager) mPlayMusicService.getSystemService(Context.AUDIO_SERVICE);
        mWifiLock = ((WifiManager) mPlayMusicService.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange: " + focusChange);
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
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "onBufferingUpdate: " + percent);
//        if (percent == 100) {
//            mMediaPlayer.start();
//            mPlaybackCallback.onPlaybackStateChanged();
//        }
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
    public int getCurrentDuration() {
        return mSongDuration;
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
            mState = PlaybackStateCompat.STATE_BUFFERING;
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
            mSongDuration = (int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            mState = PlaybackStateCompat.STATE_BUFFERING;
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
            mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            mEqualizer.setEnabled(true);

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
            mEqualizer.release();
        }
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mState = PlaybackStateCompat.STATE_BUFFERING;
            mMediaPlayer.seekTo(position);
            mPlaybackCallback.onPlaybackStateChanged();
        }
    }

    @Override
    public void setEqualizer(EqualizerType preset) {
        switch (preset) {
            case STANDARD:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(0));
                break;
            case CLASSICAL:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(-7.2));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(-9.6));
                break;
            case DANCE:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(7.2));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(-5.6));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(-7.2));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(0));
                break;
            case POP:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(4.8));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(8.0));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(-2.4));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(-1.6));
                break;
            case ROCK:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(4.8));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(-8.0));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(4.0));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(11.2));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(11.2));
                break;
            case OPERA:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(-9.6));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(-4.0));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(11.2));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(13.0));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(13.8));
                break;
            case JAZZ:
                mEqualizer.setBandLevel((short) 0, provideBandLevel(0));
                mEqualizer.setBandLevel((short) 1, provideBandLevel(5.6));
                mEqualizer.setBandLevel((short) 2, provideBandLevel(5.6));
                mEqualizer.setBandLevel((short) 3, provideBandLevel(2.4));
                mEqualizer.setBandLevel((short) 4, provideBandLevel(2.4));
                break;
        }
    }

    private short provideBandLevel(double dB) {
        final short minLevel = mEqualizer.getBandLevelRange()[0];
        final short maxLevel = mEqualizer.getBandLevelRange()[1];
        dB *= 100;
        if (dB > maxLevel) {
            return maxLevel;
        }

        if (dB < minLevel) {
            return minLevel;
        }
        return (short) dB;
    }
}
