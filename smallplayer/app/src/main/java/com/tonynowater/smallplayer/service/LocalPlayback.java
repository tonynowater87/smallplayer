package com.tonynowater.smallplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.u2b.U2BApiDefine;
import com.tonynowater.smallplayer.util.Logger;
import com.tonynowater.smallplayer.util.YoutubeExtractorUtil;
import com.tonynowater.smallplayer.util.kt.SNetworkInfo;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;
// TODO: 2017/9/2 連結藍芽耳機播放音樂時，縮小App將手機螢幕關閉，關閉藍芽耳機，會出現音樂繼續播放的問題
// TODO: 2017/8/22 音頻柱狀圖 http://blog.csdn.net/topgun_chenlingyun/article/details/7663849
// TODO: 2017/8/20 找到音樂播放的聲音大小不一致的問題解法 https://stackoverflow.com/questions/37046343/dsp-digital-sound-processing-with-android-media-player
// TODO: 2017/8/12 移動進度條時會有一直切換到下一首歌的問題
// TODO: 2017/5/29 綁定Wifi待實做
/**
 *
 * Created by tonyliao on 2017/5/12.
 */
public class LocalPlayback implements Playback {

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
    private AudioManager mAudioManager;
    private int mAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    private int mState = PlaybackStateCompat.STATE_NONE;
    private long mCurrentSongStreamPosition;
    private int mSongDuration = 0;
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

    //exo
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer mExoPlayer;
    private MyExoPlayerEventLogger eventListener = new MyExoPlayerEventLogger();
    //exo

    LocalPlayback(PlayMusicService mPlayMusicService, MusicProvider mMusicProvider, Playback.Callback mPlaybackCallback) {
        this.mPlayMusicService = mPlayMusicService;
        this.mMusicProvider = mMusicProvider;
        this.mPlaybackCallback = mPlaybackCallback;
        mAudioManager = (AudioManager) mPlayMusicService.getSystemService(Context.AUDIO_SERVICE);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("smallplayer")).
                createMediaSource(uri, null, null);
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
                if (mExoPlayer != null) {
                    mExoPlayer.setPlayWhenReady(true);
                }
                mPlayOnFocusGain = false;
            }
        }
    }

    private void setMediaPlayerVolume(float volume) {
        if (mExoPlayer != null) {
            mExoPlayer.setVolume(volume);
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
                    Logger.getInstance().e(TAG, "onAudioFocusChange: Ignoring unsupported focusChange: " + focusChange);
            }

            if (mExoPlayer != null) {
                configureMediaPlayerByAudioFocus();
            }
        }
    };

    @Override
    public void seekTo(int position) {
        if (mExoPlayer != null) {
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public int getState() {
        Logger.getInstance().d(TAG, "getState:" + mState);
        return mState;
    }

    @Override
    public boolean isPlaying() {
        return mExoPlayer != null && mExoPlayer.getPlayWhenReady();
    }

    @Override
    public long getCurrentStreamPosition() {
        return mExoPlayer != null ? mExoPlayer.getCurrentPosition() : 0;
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
            Logger.getInstance().e(TAG, "play: null");
            return;
        }

        //沒有網路
        if (!MetaDataCustomKeyDefine.isLocal(mediaMetadataCompat)
                && !SNetworkInfo.INSTANCE.isNetworkAvailable()) {
            stop(true);
            mPlaybackCallback.onError(MyApplication.getMyString(R.string.no_network_msg));
            return;
        }

//        if (mCurrentSongStreamPosition != 0 && TextUtils.equals(mCurrentPlayId, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
//            //暫停時並切換歌單後，若是同一首歌曲的Id才做暫停=>播放的動作
//            Logger.getInstance().d(TAG, "pause and play position : " + trackPosition);
//            Logger.getInstance().d(TAG, "resume:" + mCurrentSongStreamPosition);
//            mExoPlayer.seekTo(mCurrentSongStreamPosition);
//            return;
//        }

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

            mCurrentSongStreamPosition = 0;
            mPlaybackCallback.onPlaybackStateChanged();
            youtubeExtractorAsyncTask.extract(String.format(U2BApiDefine.U2B_EXTRACT_VIDEO_URL, mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE)), false, false);
        }
    }

    private void play(int trackPosition, String source, MediaMetadataCompat mediaMetadataCompat) {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();

        if (mExoPlayer == null || !TextUtils.equals(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), mCurrentPlayId)) {
            releaseResource();
            mCurrentPlayId = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            mSongDuration = (int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(MyApplication.getContext(), new DefaultTrackSelector(), new DefaultLoadControl());
            mExoPlayer.addListener(eventListener);
            mExoPlayer.addAudioDebugListener(new AudioRendererEventListener() {
                @Override
                public void onAudioEnabled(DecoderCounters counters) {

                }

                @Override
                public void onAudioSessionId(int audioSessionId) {
                    if (mEqualizer == null) {
                        mEqualizer = new Equalizer(0, mExoPlayer.getAudioSessionId());
                        setEqualizerBandLevel();
                        mEqualizer.setEnabled(true);
                    }
                }

                @Override
                public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

                }

                @Override
                public void onAudioInputFormatChanged(Format format) {

                }

                @Override
                public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

                }

                @Override
                public void onAudioDisabled(DecoderCounters counters) {

                }
            });
            // Android "O" makes much greater use of AudioAttributes, especially
            // with regards to AudioFocus. All of UAMP's tracks are music, but
            // if your content includes spoken word such as audiobooks or podcasts
            // then the content type should be set to CONTENT_TYPE_SPEECH for those
            // tracks.
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build();
            mExoPlayer.setAudioAttributes(audioAttributes);
            mExoPlayer.prepare(buildMediaSource(Uri.parse(source)));
        }

        configureMediaPlayerByAudioFocus();
    }

    @Override
    public void pause() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
            mCurrentSongStreamPosition = mExoPlayer.getCurrentPosition();
            Logger.getInstance().d(TAG, "pause:" + mCurrentSongStreamPosition);
        }

        giveUpAudioFocus();
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
        if (mExoPlayer != null) {
            mCurrentSongStreamPosition = 0;
            mExoPlayer.release();
            mExoPlayer.removeListener(eventListener);
            mExoPlayer = null;

            if (mEqualizer != null) {
                mEqualizer.release();
                mEqualizer = null;
            }
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

    private class MyExoPlayerEventLogger implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            Logger.getInstance().d(TAG, "onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Logger.getInstance().d(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Logger.getInstance().d(TAG, "onLoadingChanged:" + isLoading);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Logger.getInstance().d(TAG, "onPlayerStateChanged, playWhenReady: " + playWhenReady + ", playbackState:" + playbackState);
            switch (playbackState) {
                case Player.STATE_IDLE:
                    mState = PlaybackStateCompat.STATE_NONE;
                    mPlaybackCallback.onPlaybackStateChanged();
                    break;
                case Player.STATE_BUFFERING:
                    mState = PlaybackStateCompat.STATE_BUFFERING;
                    mPlaybackCallback.onPlaybackStateChanged();
                    break;
                case Player.STATE_READY:
                    Logger.getInstance().d(TAG, "mExoPlayer.getPlayWhenReady():" + mExoPlayer.getPlayWhenReady());
                    if (playWhenReady) {
                        mState = PlaybackStateCompat.STATE_PLAYING;
                    } else {
                        mState = PlaybackStateCompat.STATE_PAUSED;
                    }
                    mPlaybackCallback.onPlaybackStateChanged();
                    break;
                case Player.STATE_ENDED:
                    mCurrentSongStreamPosition = 0;
                    mPlaybackCallback.onCompletion();
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            Logger.getInstance().d(TAG, "onRepeatModeChanged:" + repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            Logger.getInstance().d(TAG, "onShuffleModeEnabledChanged:" + shuffleModeEnabled);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            final String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = error.getUnexpectedException().getMessage();
                    break;
                default:
                    what = "Unknown: " + error;
            }
            Logger.getInstance().d(TAG, "onPlayerError: what = " + what);
            mPlaybackCallback.onError(what);
            stop(true);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Logger.getInstance().d(TAG, "onPositionDiscontinuity:" + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Logger.getInstance().d(TAG, "onPlaybackParametersChanged:" + playbackParameters);
        }

        @Override
        public void onSeekProcessed() {
            Logger.getInstance().d(TAG, "onSeekProcessed:");
            mExoPlayer.setPlayWhenReady(true);
        }
    }
}
