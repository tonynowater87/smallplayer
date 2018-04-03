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

import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.u2b.U2BApiDefine;
import com.tonynowater.smallplayer.util.Logger;
import com.tonynowater.smallplayer.util.YoutubeExtractorUtil;
import com.tonynowater.smallplayer.util.kt.SNetworkInfo;

import java.io.IOException;
import java.util.Locale;
// TODO: 2018/3/13 暫停取到的時間不正確，需更換ExoPlayer解決
// TODO: 2017/9/2 連結藍芽耳機播放音樂時，縮小App將手機螢幕關閉，關閉藍芽耳機，會出現音樂繼續播放的問題
// TODO: 2017/8/22 音頻柱狀圖 http://blog.csdn.net/topgun_chenlingyun/article/details/7663849
// TODO: 2017/8/20 找到音樂播放的聲音大小不一致的問題解法 https://stackoverflow.com/questions/37046343/dsp-digital-sound-processing-with-android-media-player
// TODO: 2017/8/12 移動進度條時會有一直切換到下一首歌的問題
// TODO: 2017/5/29 綁定Wifi待實做
/**
 *
 * Created by tonyliao on 2017/5/12.
 */
public class LocalPlayback implements Playback
        , MediaPlayer.OnCompletionListener
        , MediaPlayer.OnErrorListener
        , MediaPlayer.OnPreparedListener
        , MediaPlayer.OnSeekCompleteListener
        , MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = LocalPlayback.class.getSimpleName();
    private static final float VOLUME_DOCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private PlayMusicService mPlayMusicService;
    //private WifiManager.WifiLock mWifiLock;
    private AudioManager mAudioManager;
    private int mAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    private int mState = PlaybackStateCompat.STATE_NONE;
    private int mCurrentSongStreamPosition;
    private int mCurrentTrackPosition;
    private int mSongDuration = 0;
    private MediaPlayer mMediaPlayer;
    private MusicProvider mMusicProvider;
    private Playback.Callback mPlaybackCallback;
    private Equalizer mEqualizer;
    private boolean mAudioNoisyReceiverRegistered = false;
    private final IntentFilter mAudioNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                //耳機拔掉的事件
                Logger.getInstance().d(TAG, "onReceive: " + isPlaying());
                if (isPlaying()) {
                    pause();
                }
            }
        }
    };
    private boolean mPlayOnFocusGain;//獲取AudioFocus後是否繼續播放
    private String mCurrentPlayId;//目前正在播放歌曲的Id
    private EqualizerType mEqualizerType = EqualizerType.STANDARD;

    LocalPlayback(PlayMusicService mPlayMusicService, MusicProvider mMusicProvider, Playback.Callback mPlaybackCallback) {
        this.mPlayMusicService = mPlayMusicService;
        this.mMusicProvider = mMusicProvider;
        this.mPlaybackCallback = mPlaybackCallback;
        mAudioManager = (AudioManager) mPlayMusicService.getSystemService(Context.AUDIO_SERVICE);
        //mWifiLock = ((WifiManager) mPlayMusicService.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
    }

    /**
     * 試著獲取AudioFocus
     */
    private void tryToGetAudioFocus() {
        Logger.getInstance().d(TAG, "tryToGetAudioFocus: " + mAudioNoisyReceiverRegistered);
        registerAudioNoisyReceiver();
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AUDIO_FOCUSED;
        } else {
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    /**
     * 放棄AudioFocus
     */
    private void giveUpAudioFocus() {
        Logger.getInstance().d(TAG, "giveUpAudioFocus: " + mAudioNoisyReceiverRegistered);
        unregisterAudioNoisyReceiver();
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
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
        Logger.getInstance().d(TAG, "configureMediaPlayerByAudioFocus: " + mAudioFocus);

        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            pause();
        } else {
            if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                setMediaPlayerVolume(VOLUME_DOCK);
            } else {
                setMediaPlayerVolume(VOLUME_NORMAL);
            }

            if (mPlayOnFocusGain) {
                play(mCurrentTrackPosition);
                mPlayOnFocusGain = false;
            }
        }
    }

    private void setMediaPlayerVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logger.getInstance().d(TAG, "onAudioFocusChange: " + focusChange);

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mAudioFocus = AUDIO_FOCUSED;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //電話播打進來時會觸發的焦點狀態
                    mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
                    mPlayOnFocusGain = isPlaying();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mAudioFocus = focusChange;
                    break;
                default:
                    Log.e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
            }

            if (mMediaPlayer != null) {
                configureMediaPlayerByAudioFocus();
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logger.getInstance().d(TAG, "onCompletion:");
        mCurrentSongStreamPosition = 0;
        mPlaybackCallback.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // FIXME: 2017/6/14 目前當歌曲在緩衝時切換播放位置會onError -38 onCompletion
        Logger.getInstance().d(TAG, "onError: " + what + " " + extra);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = PlaybackStateCompat.STATE_PLAYING;
        Logger.getInstance().d(TAG, "onPrepared:" + mState);
        mp.start();
        mPlaybackCallback.onPlaybackStateChanged();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Logger.getInstance().d(TAG, "onBufferingUpdate: " + percent);
//        if (percent == 100) {
//            mMediaPlayer.start();
//            mPlaybackCallback.onPlaybackStateChanged();
//        }
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mState = PlaybackStateCompat.STATE_BUFFERING;
            mMediaPlayer.seekTo(position);
            mCurrentSongStreamPosition = position;
            mPlaybackCallback.onPlaybackStateChanged();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Logger.getInstance().d(TAG, "onSeekComplete:");
        mState = PlaybackState.STATE_PLAYING;
        mMediaPlayer.start();
        mPlaybackCallback.onPlaybackStateChanged();
    }

    @Override
    public void setState(int state) {
        Logger.getInstance().d(TAG, "setState:" + state);
    }

    @Override
    public int getState() {
        Logger.getInstance().d(TAG, "getState:" + mState);
        return mState;
    }

    @Override
    public boolean isPlaying() {
        return mState == PlaybackStateCompat.STATE_PLAYING || mState == PlaybackStateCompat.STATE_BUFFERING;
    }

    @Override
    public int getCurrentStreamPosition() {
        return mState == PlaybackStateCompat.STATE_PLAYING ? mMediaPlayer.getCurrentPosition() : mCurrentSongStreamPosition;
    }

    @Override
    public int getCurrentDuration() {
        return mSongDuration;
    }

    @Override
    public void setCurrentStreamPosition(int pos) {}

    @Override
    public void updateLastKnownStreamPosition() {}

    @Override
    public void play(final int trackPosition) {

        final MediaMetadataCompat mediaMetadataCompat = mMusicProvider.getCurrentPlayingMediaMetadata();

        if (mediaMetadataCompat == null) {
            stop(true);
            Log.e(TAG, "play: null");
            return;
        }

        //沒有網路
        if (!MetaDataCustomKeyDefine.isLocal(mediaMetadataCompat)
                && !SNetworkInfo.INSTANCE.isNetworkAvailable()) {
            stop(true);
            mPlaybackCallback.onError(MyApplication.getMyString(R.string.no_network_msg));
            return;
        }

        tryToGetAudioFocus();

        //沒有取得播音樂焦點
        if (mAudioFocus == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            Logger.getInstance().d(TAG, "play: AudioManager.AUDIOFOCUS_REQUEST_FAILED");
            return;
        }

        if (mCurrentSongStreamPosition != 0 && TextUtils.equals(mCurrentPlayId, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
            //暫停時並切換歌單後，若是同一首歌曲的Id才做暫停=>播放的動作
            Logger.getInstance().d(TAG, "pause and play position : " + trackPosition);
            Logger.getInstance().d(TAG, "resume:" + mCurrentSongStreamPosition);
            mState = PlaybackStateCompat.STATE_BUFFERING;
            mMediaPlayer.seekTo(mCurrentSongStreamPosition);
            return;
        }

        mCurrentPlayId = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

        if (MetaDataCustomKeyDefine.isLocal(mediaMetadataCompat)) {
            //播放本地音樂
            String source = mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE);
            play(trackPosition, source ,mediaMetadataCompat);
        } else {
            //播放Youtube音樂
            YoutubeExtractorUtil youtubeExtractorAsyncTask = new YoutubeExtractorUtil(mPlayMusicService.getApplicationContext(), new YoutubeExtractorUtil.CallBack() {
                @Override
                public void onSuccess(String url) {
                    play(trackPosition, url, mediaMetadataCompat);
                }

                @Override
                public void onFailed() {
                    //歌曲有問題就跳下一首
                    mPlaybackCallback.onCompletion();
                }
            });
            //設定狀態為BUFFERING通知畫面
            mState = PlaybackStateCompat.STATE_BUFFERING;
            mCurrentSongStreamPosition = 0;
            mPlaybackCallback.onPlaybackStateChanged();
            youtubeExtractorAsyncTask.extract(String.format(U2BApiDefine.U2B_EXTRACT_VIDEO_URL, mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE)), false, false);
        }
    }

    private void play(int trackPosition, String source, MediaMetadataCompat mediaMetadataCompat) {
        try {
            createMediaPlayerIfNeeded();
            Logger.getInstance().d(TAG, String.format(Locale.TAIWAN, "PlaySize:%d\tPlayPosition:%d\tPlaySong:%s",mMusicProvider.getPlayListSize(),trackPosition,mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE)));
            mCurrentTrackPosition = trackPosition;
            mSongDuration = (int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(source);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "play error: " + e.toString() );
            //歌曲有問題就跳下一首
            mPlaybackCallback.onCompletion();
        }
    }

    private void createMediaPlayerIfNeeded() {
        Logger.getInstance().d(TAG, "createMediaPlayerIfNeeded");
        releaseResource();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        setEqualizerBandLevel();
        mEqualizer.setEnabled(true);
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentSongStreamPosition = mMediaPlayer.getCurrentPosition();
                Logger.getInstance().d(TAG, "pause:" + mCurrentSongStreamPosition);
            }
        }

        giveUpAudioFocus();
        mState = PlaybackStateCompat.STATE_PAUSED;
        mPlaybackCallback.onPlaybackStateChanged();
    }

    @Override
    public void stop(boolean notifyListeners) {
        Logger.getInstance().d(TAG, "stop:" + notifyListeners);
        mState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners) {
            mPlaybackCallback.onPlaybackStateChanged();
        }

        mCurrentSongStreamPosition = 0;
        giveUpAudioFocus();
        releaseResource();
    }

    @Override
    public void releaseResource() {
        if (mMediaPlayer != null) {
            mCurrentSongStreamPosition = 0;
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mEqualizer.release();
            mEqualizer = null;
        }
    }

    @Override
    public void setEqualizer(EqualizerType equalizerType) {
        mEqualizerType = equalizerType;
        setEqualizerBandLevel();
    }

    @Override
    public EqualizerType getEqualizerType() {
        return mEqualizerType;
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

    private void setEqualizerBandLevel() {
        if (mEqualizer == null) {
            Logger.getInstance().d(TAG, "setEqualizer: you can't setEqualizer when mEqualizer is null");
            return;
        }
        switch (mEqualizerType) {
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
}
