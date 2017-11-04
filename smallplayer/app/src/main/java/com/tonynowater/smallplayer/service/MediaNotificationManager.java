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
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.activity.FullScreenPlayerActivity;
import com.tonynowater.smallplayer.activity.MainActivity;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.util.AlbumArtCache;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
// FIXME: 2017/6/12 播到一半縮小會有通知失效的問題以及廣播未解註冊的錯誤
// FIXME: 2017/6/12 疑似Service沒有Start過，所以畫面縮小就Destroy的關係
//06-12 21:56:32.843 5702-5702/com.tonynowater.smallplayer D/PlayMusicService: updatePlaybackState: 3
//        06-12 21:56:39.191 5702-5702/com.tonynowater.smallplayer D/PlayMusicService: onDestroy:
//        06-12 21:56:39.191 5702-5702/com.tonynowater.smallplayer D/PlayMusicService: updatePlaybackState: 3
//        06-12 21:56:39.301 5702-5702/com.tonynowater.smallplayer E/ActivityThread: Service com.tonynowater.smallplayer.service.PlayMusicService has leaked IntentReceiver com.tonynowater.smallplayer.service.LocalPlayback$1@ec220d7 that was originally registered here. Are you missing a call to unregisterReceiver()?
//        android.app.IntentReceiverLeaked: Service com.tonynowater.smallplayer.service.PlayMusicService has leaked IntentReceiver com.tonynowater.smallplayer.service.LocalPlayback$1@ec220d7 that was originally registered here. Are you missing a call to unregisterReceiver()?
//        at android.app.LoadedApk$ReceiverDispatcher.<init>(LoadedApk.java:993)
//        at android.app.LoadedApk.getReceiverDispatcher(LoadedApk.java:794)
//        at android.app.ContextImpl.registerReceiverInternal(ContextImpl.java:1708)
//        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1688)
//        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1682)
//        at android.content.ContextWrapper.registerReceiver(ContextWrapper.java:488)
//        at com.tonynowater.smallplayer.service.LocalPlayback.registerAudioNoisyReceiver(LocalPlayback.java:104)
//        at com.tonynowater.smallplayer.service.LocalPlayback.tryToGetAudioFocus(LocalPlayback.java:82)
//        at com.tonynowater.smallplayer.service.LocalPlayback.play(LocalPlayback.java:241)
//        at com.tonynowater.smallplayer.service.PlayMusicService.handlePlayRequest(PlayMusicService.java:456)
//        at com.tonynowater.smallplayer.service.PlayMusicService.access$700(PlayMusicService.java:24)
//        at com.tonynowater.smallplayer.service.PlayMusicService$MySessionCall.handlePlayingNow(PlayMusicService.java:379)
//        at com.tonynowater.smallplayer.service.PlayMusicService$MySessionCall.onCustomAction(PlayMusicService.java:282)
//        at android.support.v4.media.session.MediaSessionCompat$Callback$StubApi21.onCustomAction(MediaSessionCompat.java:1063)
//        at android.support.v4.media.session.MediaSessionCompatApi21$CallbackProxy.onCustomAction(MediaSessionCompatApi21.java:235)
//        at android.media.session.MediaSession$CallbackMessageHandler.handleMessage(MediaSession.java:1315)
//        at android.os.Handler.dispatchMessage(Handler.java:102)
//        at android.os.Looper.loop(Looper.java:135)
//        at android.app.ActivityThread.main(ActivityThread.java:5272)
//        at java.lang.reflect.Method.invoke(Native Method)
//        at java.lang.reflect.Method.invoke(Method.java:372)
//        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:909)
//        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:704)
//        06-12 21:56:39.317 5702-5702/com.tonynowater.smallplayer E/ActivityThread: Service com.tonynowater.smallplayer.service.PlayMusicService has leaked IntentReceiver com.tonynowater.smallplayer.service.MediaNotificationManager@1105a109 that was originally registered here. Are you missing a call to unregisterReceiver()?
//        android.app.IntentReceiverLeaked: Service com.tonynowater.smallplayer.service.PlayMusicService has leaked IntentReceiver com.tonynowater.smallplayer.service.MediaNotificationManager@1105a109 that was originally registered here. Are you missing a call to unregisterReceiver()?
//        at android.app.LoadedApk$ReceiverDispatcher.<init>(LoadedApk.java:993)
//        at android.app.LoadedApk.getReceiverDispatcher(LoadedApk.java:794)
//        at android.app.ContextImpl.registerReceiverInternal(ContextImpl.java:1708)
//        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1688)
//        at android.app.ContextImpl.registerReceiver(ContextImpl.java:1682)
//        at android.content.ContextWrapper.registerReceiver(ContextWrapper.java:488)
//        at com.tonynowater.smallplayer.service.MediaNotificationManager.startNotification(MediaNotificationManager.java:184)
//        at com.tonynowater.smallplayer.service.PlayMusicService.updatePlaybackState(PlayMusicService.java:143)
//        at com.tonynowater.smallplayer.service.PlayMusicService.updatePlaybackState(PlayMusicService.java:112)
//        at com.tonynowater.smallplayer.service.PlayMusicService.access$300(PlayMusicService.java:24)
//        at com.tonynowater.smallplayer.service.PlayMusicService$1.onPlaybackStateChanged(PlayMusicService.java:57)
//        at com.tonynowater.smallplayer.service.LocalPlayback.onPrepared(LocalPlayback.java:181)
//        at android.media.MediaPlayer$EventHandler.handleMessage(MediaPlayer.java:2523)
//        at android.os.Handler.dispatchMessage(Handler.java:102)
//        at android.os.Looper.loop(Looper.java:135)
//        at android.app.ActivityThread.main(ActivityThread.java:5272)
//        at java.lang.reflect.Method.invoke(Native Method)
//        at java.lang.reflect.Method.invoke(Method.java:372)
//        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:909)
//        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:704)
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
    private static final String ACTION_STOP = "com.tonynowater.smallplayer.stop";
    private static final String ACTION_MODE = "com.tonynowater.smallplayer.mode";
    private static final String ACTION_REPEAT = "com.tonynowater.smallplayer.repeat";

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


            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_STOPPED) {
                Log.d(TAG, "onMetadataChanged: PlaybackStateCompat.STATE_STOPPED");
                cancelNotification();
                return;
            }

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

    /**
     * 清除通知，並停止Service
     */
    private void stopNotification() {
        Log.d(TAG, "stopNotification:" + mStarted);
        if (mStarted) {
            mStarted = false;
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mPlayMusicService.unregisterReceiver(this);
            mPlayMusicService.stopForeground(true);
            mPlayMusicService.stopSelf();
        }
    }

    /**
     * 清除通知
     */
    public void cancelNotification() {
        Log.d(TAG, "cancelNotification: " + mStarted);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent mPlayIntent;
    private PendingIntent mPauseIntent;
    private PendingIntent mNextIntent;
    private PendingIntent mPreviousIntent;
    private PendingIntent mStopIntent;
    private PendingIntent mModeIntent;
    private PendingIntent mRepeatIntent;

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
        mStopIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_STOP).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mModeIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_MODE).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mRepeatIntent = PendingIntent.getBroadcast(mPlayMusicService, REQUEST_CODE, new Intent(ACTION_REPEAT).setPackage(sPkg), PendingIntent.FLAG_CANCEL_CURRENT);

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

    /**
     * 顯示通知並設定Service為前景
     */
    public void startNotification() {
        if (!mStarted) {
            mMediaMetadata = mMediaController.getMetadata();
            mPlaybackState = mMediaController.getPlaybackState();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_PLAY);
            intentFilter.addAction(ACTION_PAUSE);
            intentFilter.addAction(ACTION_PREVIOUS);
            intentFilter.addAction(ACTION_NEXT);
            intentFilter.addAction(ACTION_STOP);
            intentFilter.addAction(ACTION_MODE);
            intentFilter.addAction(ACTION_REPEAT);
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
        Log.d(TAG, "createNofification: mPlaybackState = " + mPlaybackState);
        if (mMediaMetadata == null || mPlaybackState == null) {
            return null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mPlayMusicService);
        builder.addAction(R.drawable.ic_close_black_24dp, mPlayMusicService.getString(R.string.play_state_stop), mStopIntent);
        builder.addAction(R.drawable.ic_skip_previous_black_24px, mPlayMusicService.getString(R.string.play_state_previous), mPreviousIntent);
        addPlayPauseAction(builder);
        builder.addAction(R.drawable.ic_skip_next_black_24px, mPlayMusicService.getString(R.string.play_state_next), mNextIntent);
        addShuffleAction(builder);

        MediaDescriptionCompat mediaDescription = mMediaMetadata.getDescription();

        builder.setStyle(new NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(new int[]{1, 2, 3})// show specify button in compact view, limitation is three
                    .setMediaSession(mToken))
                .setColor(mNotificationColor)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.local_music_icon)//沒setSmallIcon，通知會直接沒有顯示
                .setContentIntent(createContentIntent())
                .setContentTitle(mediaDescription.getTitle())
                .setContentText(mediaDescription.getSubtitle())
                .setShowWhen(false);

        setNotificationPlayState(builder);
        getAlbumArt(mMediaMetadata, builder);
        return builder.build();
    }

    private void addRepeatAction(NotificationCompat.Builder builder) {
        boolean isRepeat = mPlaybackState.getExtras().getBoolean(PlayMusicService.BUNDLE_KEY_IS_REPEAT);
        if (isRepeat) {
            builder.addAction(R.drawable.icons8_repeat_select, mPlayMusicService.getString(R.string.play_state_repeat), mRepeatIntent);
        } else {
            builder.addAction(R.drawable.icons8_repeat_unselect, mPlayMusicService.getString(R.string.play_state_repeat), mRepeatIntent);
        }
    }

    private void addShuffleAction(NotificationCompat.Builder builder) {
        EnumPlayMode enumPlayMode = MiscellaneousUtil.getPlayModeFromBundle(mPlaybackState.getExtras());
        switch (enumPlayMode) {
            case NORMAL:
                builder.addAction(R.drawable.icons_shuffle_unselect, mPlayMusicService.getString(R.string.play_state_shuffle), mModeIntent);
                break;
            case RANDOM_NO_SAME:
                builder.addAction(R.drawable.icons_shuffle_select, mPlayMusicService.getString(R.string.play_state_shuffle), mModeIntent);
                break;
        }
    }

    /**
     * 設定通知的圖片
     */
    private void getAlbumArt(MediaMetadataCompat mMediaMetadata, NotificationCompat.Builder builder) {
        String fetchArtUrl = mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);

        boolean isLocal = TextUtils.equals(MetaDataCustomKeyDefine.ISLOCAL,mMediaMetadata.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL));
        Bitmap bitmap ;
        
        if (isLocal) {
            if (!TextUtils.isEmpty(fetchArtUrl)) {
                bitmap = BitmapFactory.decodeFile(fetchArtUrl);
            } else {
                //本地音樂沒AlbumArt時的預設圖片
                bitmap = BitmapFactory.decodeResource(mPlayMusicService.getResources(),
                        R.drawable.ic_default_art);
            }
        } else {
            bitmap = AlbumArtCache.getInstance().getBigImage(fetchArtUrl);
            if (bitmap == null) {
                //Loading圖
                bitmap = BitmapFactory.decodeResource(mPlayMusicService.getResources(),
                        R.drawable.ic_default_art);
            }
            fetchBitmapFromURLAsync(fetchArtUrl, builder);
        }
        builder.setLargeIcon(bitmap);
    }

    private void setNotificationPlayState(NotificationCompat.Builder builder) {
        Log.d(TAG, "setNotificationPlayState: " + mPlaybackState);
        if (!mStarted) {
            Log.d(TAG, "setNotificationPlayState: cancel notification");
            return;
        }

        switch (mPlaybackState.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                if (mPlaybackState.getPosition() >= 0) {
                    long playTime = System.currentTimeMillis() - mPlaybackState.getPosition();
                    Log.d(TAG, "setNotificationPlayState , position : " + playTime / 1000 + " seconds.");
                    builder.setWhen(playTime)
                            .setShowWhen(true)
                            .setUsesChronometer(true)
                            .setAutoCancel(false)
                            .setOngoing(true);
                    break;
                }
            case PlaybackStateCompat.STATE_PAUSED:
                // Make sure that the notification can be dismissed by the user when we are not playing:
                builder.setShowWhen(false)
                        .setAutoCancel(true)
                        .setOngoing(false);
                break;
            default:
                Log.d(TAG, "setNotificationPlayState: position 0");
                builder.setWhen(0)
                        .setShowWhen(false)
                        .setUsesChronometer(false);
        }
    }

    /**
     * 清空原來的Activity Stack，再啟動設定的Activity Stack，陣列位置越後面的在Stack上面
     *
     * @return 點擊通知的PendingIntent
     */
    private PendingIntent createContentIntent() {
        Intent[] intents = new Intent[2];

        Intent intentMainActivity = new Intent(mPlayMusicService, MainActivity.class);
        intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intents[0] = intentMainActivity;

        Intent intentFullScreenActivity = new Intent(mPlayMusicService, FullScreenPlayerActivity.class);
        intents[1] = intentFullScreenActivity;

        return PendingIntent.getActivities(mPlayMusicService, REQUEST_CODE, intents, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {
        Log.d(TAG, "addPlayPauseAction: ");
        String label;
        int icon;
        PendingIntent pendingIntent;
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            label = mPlayMusicService.getString(R.string.play_state_pause);
            icon = android.R.drawable.ic_media_pause;
            pendingIntent = mPauseIntent;
        } else if (mPlaybackState.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            label = mPlayMusicService.getString(R.string.play_state_buffering);
            icon = R.drawable.icons8_load2;
            pendingIntent = null;
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
            case ACTION_STOP:
                mTransportControls.stop();
                break;
            case ACTION_MODE:
                Bundle bundle = new Bundle();
                bundle.putSerializable(PlayMusicService.BUNDLE_KEY_PLAYMODE, MiscellaneousUtil.getNextMode((MiscellaneousUtil.getPlayModeFromBundle(mPlaybackState.getExtras()))));
                mTransportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_PLAYMODE, bundle);
                break;
            case ACTION_REPEAT:
                mTransportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_REPEAT, null);
                break;
            default:
                Log.d(TAG, "onReceive: unknown " + action);
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
                if (mMediaMetadata == null) {
                    Log.d(TAG, "onFetched: 因為歌曲已刪除，所以就不更新通知了");
                    return;
                }

                if (TextUtils.equals(artUrl,mMediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))) {
                    //抓回來的圖是播放歌曲的圖才去更新Nofification
                    builder.setLargeIcon(bitmap);
                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        });
    }
}
