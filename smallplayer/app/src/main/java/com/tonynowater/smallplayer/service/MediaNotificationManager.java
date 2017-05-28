package com.tonynowater.smallplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.activity.MainActivity;
import com.tonynowater.smallplayer.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.util.AlbumArtCache;

/**
 * 處理通知類別
 * Created by tonyliao on 2017/5/13.
 */
public class MediaNotificationManager extends BroadcastReceiver {
    private static final String TAG = MediaNotificationManager.class.getSimpleName();
    private static final int REQUEST_CODE = 1611;
    private static final int NOTIFICATION_ID = 2340;
    private static final String ACTION_PLAY = "com.tonynowater.smallplayer.play";
    private static final String ACTION_PAUSE = "com.tonynowater.smallplayer.pause";
    private static final String ACTION_NEXT = "com.tonynowater.smallplayer.next";
    private static final String ACTION_PREVIOUS = "com.tonynowater.smallplayer.previous";


    private PlayMusicService mPlayMusicService;
    private NotificationManager mNotificationManager;
    private MediaSessionCompat.Token mToken;
    private MediaControllerCompat mMediaController;
    private MediaControllerCompat.TransportControls mTransportControls;
    private PlaybackStateCompat mPlaybackState;
    private MediaMetadataCompat mMediaMetadata;
    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d(TAG, "onPlaybackStateChanged: " + state);
            mPlaybackState = state;

            if (state.getState() == PlaybackStateCompat.STATE_PLAYING
                    || state.getState() == PlaybackStateCompat.STATE_PAUSED
                    || state.getState() == PlaybackStateCompat.STATE_BUFFERING) {
                Notification notification = createNofification();
                if (notification != null) {
                    Log.d(TAG, "onPlaybackStateChanged: refresh notification");
                    mNotificationManager.notify(NOTIFICATION_ID, notification);
                }
            } else {
                stopNotification();
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.d(TAG, "onMetadataChanged: " + metadata);
            mMediaMetadata = metadata;
            Notification notification = createNofification();
            if (notification != null) {
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }
        @Override
        public void onSessionDestroyed() {
            Log.d(TAG, "onSessionDestroyed: ");
            updateSessionToken();
        }
    };

    private void stopNotification() {
        Log.d(TAG, "stopNotification:" + mStarted);
        if (mStarted) {
            mStarted = false;
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mNotificationManager.cancel(NOTIFICATION_ID);
            mPlayMusicService.unregisterReceiver(this);
            mPlayMusicService.stopForeground(true);
        }
    }

    private PendingIntent mPlayIntent;
    private PendingIntent mPauseIntent;
    private PendingIntent mNextIntent;
    private PendingIntent mPreviousIntent;

    private boolean mStarted = false;
    private int mNotificationColor;

    public MediaNotificationManager(PlayMusicService mPlayMusicService) {
        this.mPlayMusicService = mPlayMusicService;
        updateSessionToken();

        mNotificationColor = ResourceHelper.getThemeColor(mPlayMusicService.getApplicationContext(), android.R.attr.colorPrimary, Color.GRAY);
        mNotificationManager = (NotificationManager) mPlayMusicService.getSystemService(Context.NOTIFICATION_SERVICE);

        String sPkg = mPlayMusicService.getPackageName();
        mPlayIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_PLAY).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPauseIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_PAUSE).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_NEXT).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_PREVIOUS).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    /**
     * Update the state based on a change on the session token. Called either when
     * we are running for the first time or when the media session owner has destroyed the session
     * (see {@link android.media.session.MediaController.Callback#onSessionDestroyed()})
     */
    private void updateSessionToken() {
        MediaSessionCompat.Token freshToken = mPlayMusicService.getSessionToken();
        if (freshToken == null || !freshToken.equals(mToken)) {
            if (mMediaController != null) {
                mMediaController.unregisterCallback(mMediaControllerCallback);
            }
            mToken = freshToken;
            try {
                mMediaController = new MediaControllerCompat(mPlayMusicService, mToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mTransportControls = mMediaController.getTransportControls();
            if (mStarted) {
                //會在startNofitication註冊
                mMediaController.registerCallback(mMediaControllerCallback);
            }
        }
    }

    public void startNotification() {
        if (!mStarted) {
            mMediaMetadata = mMediaController.getMetadata();
            mPlaybackState = mMediaController.getPlaybackState();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_PLAY);
            intentFilter.addAction(ACTION_PAUSE);
            intentFilter.addAction(ACTION_PREVIOUS);
            intentFilter.addAction(ACTION_NEXT);
            mPlayMusicService.registerReceiver(this, intentFilter);
            // The notification must be updated after setting started to true
            Notification notification = createNofification();
            if (notification != null) {
                mMediaController.registerCallback(mMediaControllerCallback);
                mPlayMusicService.startForeground(NOTIFICATION_ID, createNofification());
                mStarted = true;
            } else {
                Log.w(TAG, "startNotification: notification null");
            }
        }
    }

    private Notification createNofification() {
        Log.d(TAG, "createNofification: MetaData = " + mMediaMetadata);
        if (mMediaMetadata == null || mPlaybackState == null) {
            return null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mPlayMusicService);
        int playPauseButtonPosition = 0;

        if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
            builder.addAction(android.R.drawable.ic_media_previous, mPlayMusicService.getString(R.string.play_state_previous), mPreviousIntent);
            // If there is a "skip to previous" button, the play/pause button will
            // be the second one. We need to keep track of it, because the MediaStyle notification
            // requires to specify the index of the buttons (actions) that should be visible
            // when in compact view.
            playPauseButtonPosition = 1;
        }
        updatePlayPauseAction(builder);
        // If skip to next action is enabled
        if ((mPlaybackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
            builder.addAction(android.R.drawable.ic_media_next, mPlayMusicService.getString(R.string.play_state_next), mNextIntent);
        }

        MediaDescriptionCompat mediaDescription = mMediaMetadata.getDescription();

        builder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(new int[]{playPauseButtonPosition}).setMediaSession(mToken))
                .setColor(mNotificationColor)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setUsesChronometer(true)//TODO
                .setContentIntent(createContentIntent())
                .setContentTitle(mediaDescription.getTitle())
                .setContentText(mediaDescription.getSubtitle())
                .setCustomContentView(getRemoteViews(R.layout.notification_layout_normal
                        , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                        , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                        , mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                        , getAlbumArt(mMediaMetadata, builder)))
                .setCustomBigContentView(getRemoteViews(R.layout.notification_layout_large
                        , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                        , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                        , mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                        , getAlbumArt(mMediaMetadata, builder)));
        setNotificationPlayState(builder);

        return builder.build();
    }

    private Bitmap getAlbumArt(MediaMetadataCompat mMediaMetadata, NotificationCompat.Builder builder) {
        String fetchArtUrl = mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);

        boolean isLocal = TextUtils.equals(MetaDataCustomKeyDefine.ISLOCAL,mMediaMetadata.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL));
        Bitmap bitmap = null;

        if (!TextUtils.isEmpty(fetchArtUrl)) {

            if (isLocal) {
                bitmap = BitmapFactory.decodeFile(fetchArtUrl);
            } else {

                bitmap = AlbumArtCache.getInstance().getIconImage(fetchArtUrl);
                if (bitmap == null) {

                    bitmap = BitmapFactory.decodeResource(mPlayMusicService.getResources(),
                            R.mipmap.ic_launcher);
                }

                fetchBitmapFromURLAsync(fetchArtUrl, builder);
            }
        }

        return bitmap;
    }

    /** @return 自訂通知的Layout */
    private RemoteViews getRemoteViews(int layoutId, String title, String artist, boolean isPlay, Bitmap art) {
        RemoteViews remoteViews = new RemoteViews(mPlayMusicService.getPackageName(), layoutId);
        remoteViews.setOnClickPendingIntent(R.id.notification_image_view_next, mNextIntent);
        remoteViews.setOnClickPendingIntent(R.id.notification_image_view_play, mPlayIntent);
        remoteViews.setOnClickPendingIntent(R.id.notification_image_view_previous, mPreviousIntent);
        remoteViews.setImageViewResource(R.id.notification_image_view_play, isPlay ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
        remoteViews.setImageViewBitmap(R.id.notification_image_icon, art);
        remoteViews.setTextViewText(R.id.notification_textview, String.format("%s %s", title, artist));
        return remoteViews;
    }

    private void setNotificationPlayState(NotificationCompat.Builder builder) {
        Log.d(TAG, "setNotificationPlayState: " + mPlaybackState);
        if (mPlaybackState == null || !mStarted) {
            Log.d(TAG, "setNotificationPlayState: cancel notification");
            return;
        }

        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING && mPlaybackState.getPosition() >= 0) {
            long playTime = System.currentTimeMillis() - mPlaybackState.getPosition();
            Log.d(TAG, "setNotificationPlayState , position : " + playTime / 1000 + " seconds.");
            builder.setWhen(playTime)
                    .setShowWhen(true)
                    .setUsesChronometer(true);
        } else {
            Log.d(TAG, "setNotificationPlayState: position 0");
            builder.setWhen(0)
                    .setShowWhen(false)
                    .setUsesChronometer(false);
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING);
    }

    private PendingIntent createContentIntent() {
        Intent intentMainActivity = new Intent(mPlayMusicService, MainActivity.class);
        intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(mPlayMusicService, REQUEST_CODE, intentMainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void updatePlayPauseAction(NotificationCompat.Builder builder) {
        Log.d(TAG, "updatePlayPauseAction: ");
        String label;
        int icon;
        PendingIntent pendingIntent;
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            label = mPlayMusicService.getString(R.string.play_state_pause);
            icon = android.R.drawable.ic_media_pause;
            pendingIntent = mPauseIntent;
        } else {
            label = mPlayMusicService.getString(R.string.play_state_playing);
            icon = android.R.drawable.ic_media_play;
            pendingIntent = mPlayIntent;
        }
        builder.addAction(icon, label, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //處理通知的點擊事件
        String action = intent.getAction();
        Log.d(TAG, "onReceive: " + action);
        switch (action) {
            case ACTION_PLAY:
                mTransportControls.play();
                break;
            case ACTION_PAUSE:
                mTransportControls.pause();
                break;
            case ACTION_NEXT:
                mTransportControls.skipToNext();
                break;
            case ACTION_PREVIOUS:
                mTransportControls.skipToPrevious();
                break;
            default:
                Log.d(TAG, "onReceive: unknow " + action);
        }
    }

    // TODO: 2017/5/28
    private void fetchBitmapFromURLAsync(final String bitmapUrl,
                                         final NotificationCompat.Builder builder) {
        AlbumArtCache.getInstance().fetch(bitmapUrl, new AlbumArtCache.FetchListener() {
            @Override
            public void onFetched(String artUrl, Bitmap bitmap, Bitmap icon) {
                // 更新通知的icon
                Log.d(TAG, "fetchBitmapFromURLAsync: set bitmap to " + artUrl);
                builder.setCustomContentView(getRemoteViews(R.layout.notification_layout_normal
                        , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                        , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                        , mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                        , icon))
                        .setCustomBigContentView(getRemoteViews(R.layout.notification_layout_large
                                , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                                , mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                                , mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                                , bitmap));
                mNotificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        });
    }
}
