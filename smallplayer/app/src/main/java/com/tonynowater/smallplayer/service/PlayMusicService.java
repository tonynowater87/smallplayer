package com.tonynowater.smallplayer.service;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.tonynowater.smallplayer.BuildConfig;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;

import java.lang.ref.WeakReference;
import java.util.List;

// TODO: 2017/7/16 疑似有異常會啟動兩次MediaPlayer播放歌曲的問題
public class PlayMusicService extends MediaBrowserServiceCompat {

    private static final String TAG = PlayMusicService.class.getSimpleName();
    public static final String ACTION_PLAYING_NOW = "ACTION_PLAYING_NOW";
    public static final String ACTION_CHANGE_EQUALIZER_TYPE = "ACTION_CHANGE_EQUALIZER_TYPE";
    public static final String ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST = "ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST";
    public static final String ACTION_ADD_SONG_TO_PLAYLIST = "ACTION_ADD_SONG_TO_PLAYLIST";
    public static final String ACTION_REMOVE_SONG_FROM_PLAYLIST = "ACTION_REMOVE_SONG_FROM_PLAYLIST";
    public static final String ACTION_CHANGE_PLAYLIST = "ACTION_CHANGE_PLAYLIST";
    public static final String ACTION_CHANGE_PLAYMODE = "ACTION_CHANGE_PLAYMODE";
    public static final String ACTION_CHANGE_REPEAT = "ACTION_CHANGE_REPEAT";
    public static final String BUNDLE_KEY_PLAYLIST_ID = "BUNDLE_KEY_PLAYLIST_ID";
    public static final String BUNDLE_KEY_SONG_ID = "BUNDLE_KEY_SONG_ID";
    public static final String BUNDLE_KEY_CURRENT_PLAY_SONG_ID = "BUNDLE_KEY_CURRENT_PLAY_SONG_ID";
    public static final String BUNDLE_KEY_SONG_DURATION = "BUNDLE_KEY_SONG_DURATION";
    public static final String BUNDLE_KEY_EQUALIZER_TYPE = "BUNDLE_KEY_EQUALIZER_TYPE";
    public static final String BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION = "BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION";
    public static final String BUNDLE_KEY_IS_REPEAT = "BUNDLE_KEY_IS_REPEAT";
    public static final String BUNDLE_KEY_PLAYMODE = "BUNDLE_KEY_PLAYMODE";
    public static final String GET_CURRENT_PLAY_LIST_ID = "GET_CURRENT_PLAY_LIST_ID";
    private static final String ROOT_ID_TEST = "ROOT_ID_TEST";
    private static final int STOP_DELAY = 30000;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private MediaSessionCompat mMediaSessionCompat;
    private MusicProvider mMusicProvider;
    private MediaNotificationManager mMediaNotificationManager;
    private LocalPlayback mLocalPlayback;
    private Playback.Callback mPlaybackCallback = new Playback.Callback() {
        @Override
        public void onCompletion() {
            Log.d(TAG, "onCompletion: ");
            if (!mMusicProvider.isPlayListAvailable()) {
                return;
            }

            mMusicProvider.addSongPosition(false);
            if (mLocalPlayback.isPlaying()) {
                handlePlayRequest();
            }
            updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
        }

        @Override
        public void onPlaybackStateChanged() {
            updatePlaybackState(null);
        }

        @Override
        public void onError(String error) {
            updatePlaybackState(error);
        }
    };

    // 表示Service是否已start過了
    private boolean mServiceStarted;
    private int mCurrentPlayListId;

    public PlayMusicService() {
        RealmUtils realmUtils = new RealmUtils();
        mCurrentPlayListId = realmUtils.queryCurrentPlayListID();
        mMusicProvider = new MusicProvider(mCurrentPlayListId);
        realmUtils.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalPlayback = new LocalPlayback(this, mMusicProvider, mPlaybackCallback, mMusicProvider.getmEnumPlayMode());

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), TAG);
        // Enable callbacks from MediaButtons and TransportControls
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSessionCompat.setCallback(mSessionCallBack);
        mMediaSessionCompat.setActive(true);
        setSessionToken(mMediaSessionCompat.getSessionToken());

        mMediaNotificationManager = new MediaNotificationManager(this);
        //修改Service一建立起來就通知畫面
        updatePlaybackState(null);
        updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:");
        mMediaNotificationManager.cancelNotification();
        stopSelf();
        updatePlaybackState(null);
        mServiceStarted = false;
        mMediaSessionCompat.release();
        super.onDestroy();
    }

    /** 更新播放狀態至畫面及Notification */
    private void updatePlaybackState(String sError) {
        Log.d(TAG, "updatePlaybackState: " + mLocalPlayback.getState());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        int state;
        if (mLocalPlayback != null) {
            position = mLocalPlayback.getCurrentStreamPosition();
        }

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(getAvailableAction());
        state = mLocalPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (sError != null) {
            stateBuilder.setErrorMessage(PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR,sError);
        }

        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_SONG_DURATION, mLocalPlayback.getCurrentDuration());
        bundle.putBoolean(BUNDLE_KEY_IS_REPEAT, mMusicProvider.getIsReapeated());
        bundle.putSerializable(BUNDLE_KEY_PLAYMODE, mMusicProvider.getmEnumPlayMode());
        bundle.putSerializable(BUNDLE_KEY_EQUALIZER_TYPE, mLocalPlayback.getEqualizerType());
        stateBuilder.setExtras(bundle);
        mMediaSessionCompat.setPlaybackState(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
            mMediaNotificationManager.startNotification();
        }
    }

    /**
     * @return 設定Notification通知可用的播放動作
     */
    private long getAvailableAction() {
        long actions = PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT;

        if (mLocalPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        }

        return actions;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent);
        return START_STICKY;
    }

    /**
     * 這裡必須回傳BrowserRoot，畫面端才能成功連線Service
     * 每個畫面啟動時都會觸發此事件
     * @param clientPackageName
     * @param clientUid
     * @param rootHints
     * @return
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot: " + clientPackageName);
        // not allowing any arbitrary app to browse your app's contents, you
        // 這裡必須回傳，畫面端才能成功連線Service
        
        if (clientPackageName.equals(BuildConfig.APPLICATION_ID)) {
            Log.d(TAG, "onGetRoot equals my app");
            //這裡放的Bundle可從MediaBrowserCompat.getExtra取得
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_KEY_PLAYMODE, mMusicProvider.getmEnumPlayMode());
            bundle.putSerializable(BUNDLE_KEY_EQUALIZER_TYPE, mLocalPlayback.getEqualizerType());
            return new BrowserRoot(ROOT_ID_TEST, bundle);
        }
        return null;
    }

    /**
     * 透過指定訂閱的ID，回傳不同的項目給畫面端
     * @param parentId {@link BaseMediaControlActivity#getSubscribeID()}
     * @param result
     */
    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren: " + parentId);
        // 在畫面端subcribe後，回傳項目回畫面
        if (TextUtils.equals(parentId, GET_CURRENT_PLAY_LIST_ID)) {
            result.sendResult(mMusicProvider.getMediaItemList());
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result, @NonNull Bundle options) {
        super.onLoadChildren(parentId, result, options);
    }

    private MediaSessionCompat.Callback mSessionCallBack = new MediaSessionCompat.Callback() {
        /**
         * 處理線控按鈕事件
         */
        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            if (TextUtils.equals(Intent.ACTION_MEDIA_BUTTON, mediaButtonEvent.getAction())) {
                KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //只處理按下時的ACTION，因為按一次按鈕會有ACTION_DONW & ACTION_UP兩個ACTION
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            Log.d(TAG, "onMediaButtonEvent: KEYCODE_MEDIA_PLAY_PAUSE");
                            onPlay();
                            break;
                        case KeyEvent.KEYCODE_HEADSETHOOK:
                            Log.d(TAG, "onMediaButtonEvent: KEYCODE_HEADSETHOOK");
                            onPlay();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            Log.d(TAG, "onMediaButtonEvent: KEYCODE_MEDIA_NEXT");
                            onSkipToNext();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            Log.d(TAG, "onMediaButtonEvent: KEYCODE_MEDIA_PREVIOUS");
                            onSkipToPrevious();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            Log.d(TAG, "onMediaButtonEvent: KEYCODE_MEDIA_PLAY");
                            onPlay();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            Log.d(TAG, "onMediaButtonEvent: KEYCODE_MEDIA_PAUSE");
                            onPause();
                            break;
                    }
                }
            }

            return true;
        }

        @Override
        public void onPlay() {
            Log.d(TAG, "onPlay:"+mMusicProvider.isPlayListAvailable());
            if (mMusicProvider.isPlayListAvailable()) {
                if (!mLocalPlayback.isPlaying()) {
                    handlePlayRequest();
                } else {
                    handlePauseRequest();
                }
            } else {
                updatePlaybackState(null);
            }
        }

        @Override
        public void onPause() {
            Log.d(TAG, "onPause:");
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop:");
            handleStopRequest();
        }

        @Override
        public void onSkipToNext() {
            if (!mMusicProvider.isPlayListAvailable()) {
                return;
            }

            mMusicProvider.addSongPosition(true);
            if (mLocalPlayback.isPlaying()) {
                handlePlayRequest();
            }
            updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
        }

        @Override
        public void onSkipToPrevious() {
            if (!mMusicProvider.isPlayListAvailable()) {
                return;
            }

            mMusicProvider.minusSongPosition(true);
            if (mLocalPlayback.isPlaying()) {
                handlePlayRequest();
            }
            updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            Log.d(TAG, "onCustomAction: " + action);
            switch (action) {
                case ACTION_CHANGE_EQUALIZER_TYPE:
                    handleChangeEqualizerType((EqualizerType) extras.getSerializable(BUNDLE_KEY_EQUALIZER_TYPE));
                    break;
                case ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST:
                    handlePlayExplicitSong(extras.getInt(BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION));
                    break;
                case ACTION_PLAYING_NOW:
                    handlePlayingNow(extras.getInt(BUNDLE_KEY_PLAYLIST_ID), extras.getInt(BUNDLE_KEY_CURRENT_PLAY_SONG_ID));
                    break;
                case ACTION_ADD_SONG_TO_PLAYLIST:
                    handleAddSongToPlaylist(extras.getInt(BUNDLE_KEY_PLAYLIST_ID));
                    break;
                case ACTION_REMOVE_SONG_FROM_PLAYLIST:
                    handleRemoveSongFromPlaylist(extras.getInt(BUNDLE_KEY_PLAYLIST_ID), extras.getInt(BUNDLE_KEY_SONG_ID));
                    break;
                case ACTION_CHANGE_PLAYLIST:
                    handleChangePlayList(extras.getInt(BUNDLE_KEY_PLAYLIST_ID));
                    break;
                case ACTION_CHANGE_PLAYMODE:
                    handleChangePlayMode((EnumPlayMode) extras.getSerializable(BUNDLE_KEY_PLAYMODE));
                    break;
                case ACTION_CHANGE_REPEAT:
                    handleChangeRepeat();
                    break;
            }
        }

        private void handleChangeRepeat() {
            mMusicProvider.changeRepeatStatus();
            updatePlaybackState(null);
        }

        /**
         * @param equalizerType 等化器風格Type
         */
        private void handleChangeEqualizerType(EqualizerType equalizerType) {
            mLocalPlayback.setEqualizer(equalizerType);
            updatePlaybackState(null);
        }

        /**
         * 點擊切換播放模式
         * @param enumPlayMode
         */
        private void handleChangePlayMode(EnumPlayMode enumPlayMode) {
            if (enumPlayMode == EnumPlayMode.RANDOM_NO_SAME) {
                mMusicProvider.generateRandomList();
            }
            mMusicProvider.setmEnumPlayMode(enumPlayMode);
            mLocalPlayback.setEnumPlayMode(enumPlayMode);
            updatePlaybackState(null);
        }

        /**
         * 指定位置播放歌曲
         * @param songPosition 要播放的歌曲位置
         */
        private void handlePlayExplicitSong(int songPosition) {
            mMusicProvider.setSongPosition(songPosition);
            if (mLocalPlayback.isPlaying()) {
                //正在播放時換歌曲繼續播放
                handlePlayRequest();
            } else {
                //暫停時換歌曲停止播放
                handleStopRequest();
                //給畫面更新歌曲UI)
                updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
            }
        }

        /**
         * 從歌單刪除歌曲動作處理
         * @param playlistId
         * @param songId
         */
        private void handleRemoveSongFromPlaylist(int playlistId, int songId) {
            // FIXME: 2017/6/11 刪歌目前會出現IndexOutOfException
            if (!mMusicProvider.isPlayListAvailable()) {
                Log.w(TAG, "handleRemoveSongFromPlaylist: 目前沒有歌曲");
                return;
            }

            if (mMusicProvider.getCurrentPlayingMediaMetadata() == null) {
                mMusicProvider.setSongPosition(0);
            }

            String currentSongId = mMusicProvider.getCurrentPlayingMediaMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

            Log.d(TAG, "handleRemoveSongFromPlaylist: song id : " + String.valueOf(songId));
            Log.d(TAG, "handleRemoveSongFromPlaylist: current song id : " + currentSongId);
            Log.d(TAG, "handleRemoveSongFromPlaylist: playlist id : " + String.valueOf(playlistId));
            Log.d(TAG, "handleRemoveSongFromPlaylist: current playlist id : " + mCurrentPlayListId);
            if (TextUtils.equals(String.valueOf(songId), currentSongId)) {
                //刪目前正在播放的歌曲
                mMusicProvider.queryDBPlayList(playlistId);
                if (mLocalPlayback.isPlaying()) {
                    //刪目前正在播放的歌曲, 停止播放
                    handleStopRequest();

                    if (mMusicProvider.getCurrentPlayingMediaMetadata() == null) {
                        mMusicProvider.setSongPosition(0);
                    }
                }
                //給畫面更新歌曲UI
                updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
            } else {

                //刪不是現在播放的歌曲，刷新目前播放清單
                if (mCurrentPlayListId == playlistId) {
                    mMusicProvider.queryDBPlayList(playlistId);
                }
            }

            if (mCurrentPlayListId == playlistId && !mMusicProvider.isPlayListAvailable()) {
                //刪到沒歌時要停止播放
                if (mLocalPlayback.isPlaying()) {
                    Log.w(TAG, "handleRemoveSongFromPlaylist: 刪到沒歌時要停止播放");
                    mLocalPlayback.stop(true);
                }
            }
        }

        /** 加歌至歌單動作處理 */
        private void handleAddSongToPlaylist(int playlistId) {
            Log.d(TAG, "handleAddSongToPlaylist: ");
            if (mCurrentPlayListId != playlistId) {
                Log.d(TAG, "handleAddSongToPlaylist: mCurrentPlayListId != playlistId :" + mCurrentPlayListId);
                return;
            }
            mMusicProvider.queryDBPlayList(playlistId);
        }

        /**
         * 點畫面播放的動作處理
         * @param playlistId
         * @param playingSongId
         */
        private void handlePlayingNow(int playlistId, int playingSongId) {
            Log.d(TAG, "handlePlayingNow: ");
            mMusicProvider.queryDBPlayList(playlistId);
            mMusicProvider.setSongPositionNow(playingSongId);//播指定加入歌曲的位置
            handlePlayRequest();
        }

        /** 切換歌單動作處理 */
        private void handleChangePlayList(int playlistId) {
            Log.d(TAG, "handleChangePlayList: ");
            mMusicProvider.queryDBPlayList(playlistId);
            //切換歌單，從第一首開始播放
            mMusicProvider.setSongPosition(0);
            mCurrentPlayListId = playlistId;
            if (mMusicProvider.getPlayListSize() == 0) {
                //切換到沒歌曲的歌單要停止播放
                handleStopRequest();
            } else {
                if (mLocalPlayback.isPlaying()) {
                    //正在播放，切換歌單繼續播放
                    handlePlayRequest();
                } else {
                    //更新畫面歌曲UI)
                    updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
                }
            }
        }

        @Override
        public void onSeekTo(long pos) {
            Log.d(TAG, "onSeekTo: " + pos);
            mLocalPlayback.seekTo((int) pos);
        }
    };

    private void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: " + mLocalPlayback.getState());
        mLocalPlayback.pause(false);
    }

    private void handleStopRequest() {
        mLocalPlayback.stop(true);
        updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
        updatePlaybackState(null);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    }

    private void handlePlayRequest() {
        Log.d(TAG, "handlePlayRequest: " + mLocalPlayback.getState());
        Log.d(TAG, "handlePlayRequest mServiceStarted : " + mServiceStarted);
        mDelayedStopHandler.removeCallbacksAndMessages(null);

        if (!mServiceStarted) {
            // The MusicService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            Log.d(TAG, "handlePlayRequest: startService");
            startService(new Intent(getApplicationContext(), PlayMusicService.class));
            mServiceStarted = true;
        }

        if (!mMediaSessionCompat.isActive()) {
            mMediaSessionCompat.setActive(true);
        }

        mLocalPlayback.play(mMusicProvider.getCurrentPlayingIndex());
        updateMetadata(mMusicProvider.getCurrentPlayingMediaMetadata());
    }

    /**
     * 開始播放音樂後更新Session的metadata
     * @param metadata 更新音樂資料通知畫面{@link android.support.v4.media.session.MediaControllerCompat.Callback#onMetadataChanged(MediaMetadataCompat)}
     */
    private void updateMetadata(MediaMetadataCompat metadata) {

        if (metadata == null) {
            Log.w(TAG, "updateMetadata: metadata null ");
            mMediaSessionCompat.setMetadata(metadata);
            return;
        }

        Log.d(TAG, "updateMetadata: " + metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mMediaSessionCompat.setMetadata(metadata);
    }

    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends android.os.Handler {
        private final WeakReference<PlayMusicService> mWeakReference;

        private DelayedStopHandler(PlayMusicService playMusicService) {
            mWeakReference = new WeakReference<>(playMusicService);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayMusicService playMusicService = mWeakReference.get();
            Log.d(TAG, "handleMessage: ");
            if (playMusicService != null && playMusicService.mLocalPlayback != null) {
                if (playMusicService.mLocalPlayback.isPlaying()) {
                    Log.d(TAG, "handleMessage: mLocalPlayback.isPlaying");
                    return;
                }
                Log.w(TAG, "handleMessage: Stopping service with delay handler.");
                playMusicService.stopSelf();
                playMusicService.mServiceStarted = false;
            }
        }
    }
}
