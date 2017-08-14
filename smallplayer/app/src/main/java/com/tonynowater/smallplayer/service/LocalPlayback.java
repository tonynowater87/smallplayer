package com.tonynowater.smallplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.session.PlaybackState;
import android.net.wifi.WifiManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.u2b.U2BApiDefine;
import com.tonynowater.smallplayer.util.YoutubeExtratorUtil;

import java.io.IOException;

// TODO: 2017/8/12 移動進度條時會有一直切換到下一首歌的問題
// TODO: 2017/5/29 綁定Wifi待實做
/**
 *
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
    private static final float VOLUME_DOCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    private PlayMusicService mPlayMusicService;
    private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private int mAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    private int mState = PlaybackStateCompat.STATE_NONE;
    private int mCurrentPosition;
    private int mCurrentTrackPosition;
    private int mSongDuration = 0;
    private MediaPlayer mMediaPlayer;
    private MusicProvider mMusicProvider;
    private Playback.Callback mPlaybackCallback;
    private Equalizer mEqualizer;
    private YoutubeExtratorUtil mYoutubeExtratorAsyncTask;
    private boolean mAudioNoisyReceiverRegistered = false;
    private final IntentFilter mAudioNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.getAction()) {
                //耳機拔掉的事件
                Log.d(TAG, "onReceive: " + isPlaying());
                if (isPlaying()) {
                    pause();
                }
            }
        }
    };
    private boolean mPlayOnFocusGain;//獲取AudioFocus後是否繼續播放
    private String mCurrentPlayId;//目前正在播放歌曲的Id
    private EqualizerType mEqualizerType = EqualizerType.STANDARD;
    private EnumPlayMode mEnumPlayMode;

    public LocalPlayback(PlayMusicService mPlayMusicService, MusicProvider mMusicProvider, Playback.Callback mPlaybackCallback, EnumPlayMode mEnumPlayMode) {
        this.mPlayMusicService = mPlayMusicService;
        this.mMusicProvider = mMusicProvider;
        this.mPlaybackCallback = mPlaybackCallback;
        this.mEnumPlayMode = mEnumPlayMode;
        mAudioManager = (AudioManager) mPlayMusicService.getSystemService(Context.AUDIO_SERVICE);
        mWifiLock = ((WifiManager) mPlayMusicService.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
    }

    /**
     * 試著獲取AudioFocus
     */
    private void tryToGetAudioFocus() {
        Log.d(TAG, "tryToGetAudioFocus: ");
        registerAudioNoisyReceiver();
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            mAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
    }

    /**
     * 放棄AudioFocus
     */
    private void giveUpAudioFocus() {
        Log.d(TAG, "giveUpAudioFocus: ");
        unregisterAudioNoisyReceiver();
        if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        }
    }

    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mPlayMusicService.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mPlayMusicService.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    private void configureMediaPlayerByAudioFocus() {
        Log.d(TAG, "configureMediaPlayerByAudioFocus: " + mAudioFocus);
        if (mAudioFocus == AudioManager.AUDIOFOCUS_REQUEST_FAILED
         || mAudioFocus == AudioManager.AUDIOFOCUS_LOSS
         || mAudioFocus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            if (isPlaying()) {
                pause();
            }
        } else {
            if (mAudioFocus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                setMediaPlayerVolume(VOLUME_DOCK);
            } else {
                setMediaPlayerVolume(VOLUME_NORMAL);
            }
        }
    }

    private void setMediaPlayerVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange: " + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            mAudioFocus = AudioManager.AUDIOFOCUS_GAIN;
            if (mPlayOnFocusGain) {
                play(mCurrentTrackPosition);
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
            mAudioFocus = focusChange;

            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                mPlayOnFocusGain = true;
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }

        } else {
            Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
        }

        configureMediaPlayerByAudioFocus();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion:");
        mPlaybackCallback.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // FIXME: 2017/6/14 目前當歌曲在緩衝時切換播放位置會onError -38 onCompletion
        Log.d(TAG, "onError: " + what + " " + extra);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = PlaybackStateCompat.STATE_PLAYING;
        Log.d(TAG, "onPrepared:" + mState);
        mp.start();
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
        return mState == PlaybackStateCompat.STATE_PLAYING || mState == PlaybackStateCompat.STATE_BUFFERING || (mMediaPlayer != null && mMediaPlayer.isPlaying()) ? true : false;
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

    // TODO: 2017/6/3 從Youtube音樂切回播本地音樂，會有不是播放本地音樂的問題
    @Override
    public void play(final int trackPosition) {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();

        final MediaMetadataCompat mediaMetadataCompat = mMusicProvider.getPlayItemByIndex(trackPosition, mEnumPlayMode);

        if (mediaMetadataCompat == null) {
            stop(true);
            Log.e(TAG, "play: null");
            return;
        }

        //沒有取得播音樂焦點
        if (mAudioFocus == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            Log.d(TAG, "play: AudioManager.AUDIOFOCUS_REQUEST_FAILED");
            return;
        }

        //取消前一首取Youtube的AsyncTask
        if (mYoutubeExtratorAsyncTask != null) {
            if (!mYoutubeExtratorAsyncTask.isCancelled()) {
                Log.d(TAG, "play: mYoutubeExtratorAsyncTask.cancel(true)");
                mYoutubeExtratorAsyncTask.cancel(true);
            }
        }

        if (mCurrentTrackPosition != trackPosition) {
            Log.d(TAG, String.format("before track : %s \t coming track : %s",mCurrentTrackPosition,trackPosition));
            mCurrentPosition = 0;
        }

        if (mCurrentPosition != 0 && TextUtils.equals(mCurrentPlayId, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
            //暫停時並切換歌單後，若是同一首歌曲的Id才做暫停=>播放的動作
            Log.d(TAG, "pause and play position : " + trackPosition);
            mState = PlaybackStateCompat.STATE_BUFFERING;
            mMediaPlayer.seekTo(mCurrentPosition);
            return;
        }

        mCurrentPlayId = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

        //設定狀態為BUFFERING通知畫面
        mState = PlaybackStateCompat.STATE_BUFFERING;
        mPlaybackCallback.onPlaybackStateChanged();

        if (MetaDataCustomKeyDefine.isLocal(mediaMetadataCompat)) {
            //播放本地音樂
            String source = mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE);
            play(trackPosition, source ,mediaMetadataCompat);
        } else {
            //播放Youtube音樂
            mYoutubeExtratorAsyncTask = new YoutubeExtratorUtil(mPlayMusicService.getApplicationContext(), new YoutubeExtratorUtil.CallBack() {
                @Override
                public void getU2BUrl(String url) {
                    if (TextUtils.isEmpty(url)) {
                        mPlaybackCallback.onError(mPlayMusicService.getString(R.string.extract_youtube_error));
                        return;
                    }
                    play(trackPosition, url, mediaMetadataCompat);
                }
            });

            mYoutubeExtratorAsyncTask.extract(String.format(U2BApiDefine.U2B_EXTRACT_VIDEO_URL, mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE)), false, false);
        }
    }

    private void play(int trackPosition, String source, MediaMetadataCompat mediaMetadataCompat) {
        try {
            createMediaPlayerIfNeeded();
            Log.d(TAG, String.format("PlaySize:%d\tPlayPosition:%d\tPlaySong:%s",mMusicProvider.getPlayListSize(),trackPosition,mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE)));
            mCurrentTrackPosition = trackPosition;
            mCurrentPosition = 0;
            mSongDuration = (int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(source);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "play error: " + e.toString() );// TODO: 2017/5/24 錯誤事件需要接到畫面處理
        }
    }

    private void createMediaPlayerIfNeeded() {
        Log.d(TAG, "createMediaPlayerIfNeeded");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mEqualizer != null) {
            mEqualizer.release();
            mEqualizer = null;
        }
        
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);
    }

    @Override
    public void pause() {
        if (mState == PlaybackStateCompat.STATE_PLAYING) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mPlayOnFocusGain = false;
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
        giveUpAudioFocus();
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
    public void setEqualizer(EqualizerType equalizerType) {
        if (mEqualizer == null) {
            Log.d(TAG, "setEqualizer: you can't setEqualizer when mEqualizer is null");
            return;
        }

        mEqualizerType = equalizerType;
        switch (equalizerType) {
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

    @Override
    public EqualizerType getEqualizerType() {
        return mEqualizer == null ? null : mEqualizerType;
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

    public void setEnumPlayMode(EnumPlayMode mEnumPlayMode) {
        this.mEnumPlayMode = mEnumPlayMode;
    }
}
