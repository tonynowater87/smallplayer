package com.tonynowater.smallplayer.service;

import android.content.Intent;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import java.util.List;

public class PlayMusicService extends MediaBrowserServiceCompat {

    private static final String TAG = PlayMusicService.class.getSimpleName();
    private static final String ACTION_ADD_NEW_MUSIC = "ACTION_ADD_NEW_MUSIC";
    private static final String BUNDLE_KEY_MEDIAMETADATA = "BUNDLE_KEY_MEDIAMETADATA";

    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackStateCompatBuilder;
    private MusicProvider mMusicProvider;

    private LocalPlayback mLocalPlayback;

    public PlayMusicService() {
        mMusicProvider = new MusicProvider();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocalPlayback = new LocalPlayback(getApplicationContext(),mMusicProvider);

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), TAG);
        // Enable callbacks from MediaButtons and TransportControls
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mPlaybackStateCompatBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSessionCompat.setPlaybackState(mPlaybackStateCompatBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSessionCompat.setCallback(new MySessionCall());
        mMediaSessionCompat.setActive(true);
        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if (TextUtils.equals(action, ACTION_ADD_NEW_MUSIC)) {
                MediaMetadata mediaMetadata = intent.getParcelableExtra(BUNDLE_KEY_MEDIAMETADATA);
                mMusicProvider.putNewMusic(mediaMetadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID),mediaMetadata);

            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // not allowing any arbitrary app to browse your app's contents, you
        // 目前沒用到
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        // 目前沒用到, 在畫面端subcribe後，回傳項目回畫面
    }

    private final class MySessionCall extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();

        }
    }
}
