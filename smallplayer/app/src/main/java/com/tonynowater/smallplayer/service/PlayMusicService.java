package com.tonynowater.smallplayer.service;

import android.content.Intent;
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

import com.tonynowater.smallplayer.BuildConfig;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class PlayMusicService extends MediaBrowserServiceCompat {

    private static final String TAG = PlayMusicService.class.getSimpleName();
    public static final String PLAY_PLAYLIST = "PLAY_PLAYLIST";
    public static final String ACTION_CHANGE_EQUALIZER_TYPE = "ACTION_CHANGE_EQUALIZER_TYPE";
    public static final String ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST = "ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST";
    public static final String BUNDLE_KEY_PLAYLIST_POSITION = "BUNDLE_KEY_PLAYLIST_POSITION";
    public static final String BUNDLE_KEY_SONG_DURATION = "BUNDLE_KEY_SONG_DURATION";
    public static final String BUNDLE_KEY_EQUALIZER_TYPE = "BUNDLE_KEY_EQUALIZER_TYPE";
    public static final String BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION = "BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION";
    public static final String BUNDLE_KEY_CHANGE_NO_SONG_PLAYLIST = "BUNDLE_KEY_CHANGE_NO_SONG_PLAYLIST";
    public static final String GET_CURRENT_PLAY_LIST_ID = "GET_CURRENT_PLAY_LIST_ID";
    private static final String ROOT_ID_TEST = "ROOT_ID_TEST";
    private static final int STOP_DELAY = 30000;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackStateCompatBuilder;
    private MusicProvider mMusicProvider;
    private MediaNotificationManager mMediaNotificationManager;
    private LocalPlayback mLocalPlayback;
    private Playback.Callback mPlaybackCallback = new Playback.Callback() {
        @Override
        public void onCompletion() {
            Log.d(TAG, "onCompletion: ");
            skipToNext();
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
    private int mSongTrackPosition = 0;
    private int mCurrentPlayListId;

    public PlayMusicService() {
        RealmUtils realmUtils = new RealmUtils();
        mMusicProvider = new MusicProvider(realmUtils.queryCurrentPlayListID());
        realmUtils.close();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalPlayback = new LocalPlayback(this, mMusicProvider, mPlaybackCallback);

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), TAG);
        // Enable callbacks from MediaButtons and TransportControls
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mPlaybackStateCompatBuilder = new PlaybackStateCompat.Builder().setActions(getAvailableAction());
        mMediaSessionCompat.setPlaybackState(mPlaybackStateCompatBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSessionCompat.setCallback(new MySessionCall());
        mMediaSessionCompat.setActive(true);
        setSessionToken(mMediaSessionCompat.getSessionToken());

        mMediaNotificationManager = new MediaNotificationManager(this);
        updatePlaybackState(null);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:");
        updatePlaybackState(null);
        stopSelf();
        mServiceStarted = false;
        mMediaSessionCompat.release();
        super.onDestroy();
    }

    /** 更新播放狀態至Notification */
    private void updatePlaybackState(String sError) {
        updatePlaybackState(sError, null);
    }

    /** 更新播放狀態至Notification */
    private void updatePlaybackState(String sError, Bundle extraBundle) {
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

        stateBuilder.setState(state,position,1.0f, SystemClock.elapsedRealtime());

        Bundle bundle = new Bundle();
        if (extraBundle != null) {
            bundle.putBoolean(BUNDLE_KEY_CHANGE_NO_SONG_PLAYLIST, extraBundle.getBoolean(BUNDLE_KEY_CHANGE_NO_SONG_PLAYLIST));
        }
        bundle.putInt(BUNDLE_KEY_SONG_DURATION, mLocalPlayback.getCurrentDuration());
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
        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.equals(action, PLAY_PLAYLIST)) {
                int playlistPosition = intent.getIntExtra(BUNDLE_KEY_PLAYLIST_POSITION,0);
                if (mCurrentPlayListId != playlistPosition) {
                    //切換歌單，從頭播放歌曲
                    mSongTrackPosition = 0;
                } else {
                    //切換原歌單就是播最新加的一首歌
                    mSongTrackPosition = mMusicProvider.getPlayListSize() - 1;
                }
                mCurrentPlayListId = playlistPosition;
                mMusicProvider.queryDBPlayList(playlistPosition);
                if (mMusicProvider.getPlayListSize() == 0) {
                    //切換到沒歌曲的歌單要停止播放
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BUNDLE_KEY_CHANGE_NO_SONG_PLAYLIST, true);
                    handleStopRequest(bundle);
                } else {
                    handlePlayRequest();
                }
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot: " + clientPackageName);
        // not allowing any arbitrary app to browse your app's contents, you
        // 這裡必須回傳，畫面端才能成功連線
        
        if (clientPackageName.equals(BuildConfig.APPLICATION_ID)) {
            Log.d(TAG, "onGetRoot equals my app");
            return new BrowserRoot(ROOT_ID_TEST, null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren: " + parentId);
        // 目前沒用到, 在畫面端subcribe後，回傳項目回畫面
        if (TextUtils.equals(parentId, GET_CURRENT_PLAY_LIST_ID)) {
            result.sendResult(mMusicProvider.getMediaItemList());
        }
    }

    private final class MySessionCall extends MediaSessionCompat.Callback {

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
            skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            if (!mMusicProvider.isPlayListAvailable()) {
                return;
            }

            mSongTrackPosition--;
            if (mSongTrackPosition < 0) {
                mSongTrackPosition = mMusicProvider.getPlayListSize() - 1;
            }
            handlePlayRequest();
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            Log.d(TAG, "onCustomAction: " + action);
            switch (action) {
                case ACTION_CHANGE_EQUALIZER_TYPE:
                    mLocalPlayback.setEqualizer((EqualizerType) extras.getSerializable(BUNDLE_KEY_EQUALIZER_TYPE));
                    break;
                case ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST:
                    mSongTrackPosition = extras.getInt(BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION);
                    handlePlayRequest();
                    break;
            }
        }

        @Override
        public void onSeekTo(long pos) {
            Log.d(TAG, "onSeekTo: " + pos);
            mLocalPlayback.seekTo((int) pos);
        }
    }

    private void skipToNext() {
        mSongTrackPosition++;
        if (mSongTrackPosition >= mMusicProvider.getPlayListSize()) {
            mSongTrackPosition = 0;
        }
        handlePlayRequest();
    }

    private void handlePauseRequest() {
        Log.d(TAG, "handlePauseRequest: " + mLocalPlayback.getState());
        mLocalPlayback.pause();
    }

    private void handleStopRequest() {
        handleStopRequest(null);
    }

    private void handleStopRequest(Bundle bundle) {
        Log.d(TAG, "handleStopRequest: " + mLocalPlayback.getState());
        mLocalPlayback.stop(true);
        updatePlaybackState(null, bundle);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    }

    private void handlePlayRequest() {
        Log.d(TAG, "handlePlayRequest: " + mLocalPlayback.getState());
        if (!mServiceStarted) {
            // The MusicService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            startService(new Intent(getApplicationContext(), PlayMusicService.class));
            mServiceStarted = true;
        }

        if (!mMediaSessionCompat.isActive()) {
            mMediaSessionCompat.setActive(true);
        }

        mLocalPlayback.play(mSongTrackPosition);
        updateMetadata(mMusicProvider.getPlayList(mSongTrackPosition));
    }

    /**
     * 開始播放音樂後更新Session的metadata
     * @param metadata 更新音樂資料通知畫面{@link android.support.v4.media.session.MediaControllerCompat.Callback#onMetadataChanged(MediaMetadataCompat)}
     */
    private void updateMetadata(MediaMetadataCompat metadata) {

        if (metadata == null) {
            Log.w(TAG, "updateMetadata: metadata null ");
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
            if (playMusicService != null && playMusicService.mLocalPlayback != null) {
                if (playMusicService.mLocalPlayback.isPlaying()) {
                    Log.d(TAG, "handleMessage: mLocalPlayback.isPlaying");
                    return;
                }
                Log.w(TAG, "Stopping service with delay handler.");
                playMusicService.stopSelf();
                playMusicService.mServiceStarted = false;
            }
        }
    }
}
