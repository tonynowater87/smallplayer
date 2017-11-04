package com.tonynowater.smallplayer.base;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.view.PlayerFragment;

import java.util.List;

/**
 * Activity需要和Service交互的基底類別
 *
 * 事件觸發順序
 *
 * onStart => onConnected => onMediaServiceConnected => onPlaybackStateChanged => onMetadataChanged => onChildrenLoaded
 *
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BaseMediaControlActivity<T extends ViewDataBinding> extends BaseActivity<T> implements OnClickSomething {
    private static final String TAG = BaseMediaControlActivity.class.getSimpleName();
    protected MediaControllerCompat.TransportControls mTransportControls;
    private PlaybackStateCompat mPlaybackStateCompat;
    protected MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    protected RealmUtils mRealmUtils;

    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            Log.d(TAG, "onPlaybackStateChanged: ");
            BaseMediaControlActivity.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.d(TAG, "onMetadataChanged: ");
            BaseMediaControlActivity.this.onMetadataChanged(metadata);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "onSessionDestroyed: ");
            BaseMediaControlActivity.this.onSessionDestroyed();
        }
    };

    protected abstract void onPlaybackStateChanged(PlaybackStateCompat state);

    protected abstract void onSessionDestroyed();

    protected abstract void onMetadataChanged(MediaMetadataCompat metadata);

    protected abstract void onMediaServiceConnected();

    protected abstract void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children);

    protected abstract void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children, Bundle options);

    /** 若畫面端要訂閱資料需覆寫 */
    protected String getSubscribeID() {
        return "";
    }

    private MediaBrowserCompat.SubscriptionCallback mSubScriptionCallBack = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            BaseMediaControlActivity.this.onChildrenLoaded(children);
        }

        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children, @NonNull Bundle options) {
            super.onChildrenLoaded(parentId, children, options);
            BaseMediaControlActivity.this.onChildrenLoaded(children, options);
        }

        @Override
        public void onError(@NonNull String parentId) {
            super.onError(parentId);
        }

        @Override
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            super.onError(parentId, options);
        }
    };

    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "onConnected: " + mMediaBrowserCompat.getSessionToken());

            if (mMediaBrowserCompat.getSessionToken() == null) {
                throw new IllegalArgumentException("No session token");
            }

            try {
                if (!TextUtils.isEmpty(getSubscribeID())) {
                    subscribe();
                }
                mMediaControllerCompat = new MediaControllerCompat(BaseMediaControlActivity.this, mMediaBrowserCompat.getSessionToken());
                mTransportControls = mMediaControllerCompat.getTransportControls();
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(BaseMediaControlActivity.this, mMediaControllerCompat);
                mPlaybackStateCompat = mMediaControllerCompat.getPlaybackState();
                onMediaServiceConnected();
                onPlaybackStateChanged(mPlaybackStateCompat);
                onMetadataChanged(mMediaControllerCompat.getMetadata());
                connectPlayerFragment();

            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onConnected: " + e.toString());
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.d(TAG, "onConnectionFailed: ");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.d(TAG, "onConnectionSuspended: ");
        }
    };

    /**
     * 供子類別重新訂閱取新的歌單資料，必須重new Call帶入才會觸發
     * @see BaseMediaControlActivity#onChildrenLoaded(List)
     * @see BaseMediaControlActivity#onChildrenLoaded(List, Bundle)
     */
    protected void subscribe() {
        mSubScriptionCallBack =  new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                super.onChildrenLoaded(parentId, children);
                BaseMediaControlActivity.this.onChildrenLoaded(children);
            }

            @Override
            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children, @NonNull Bundle options) {
                super.onChildrenLoaded(parentId, children, options);
                BaseMediaControlActivity.this.onChildrenLoaded(children, options);
            }

            @Override
            public void onError(@NonNull String parentId) {
                super.onError(parentId);
            }

            @Override
            public void onError(@NonNull String parentId, @NonNull Bundle options) {
                super.onError(parentId, options);
            }
        };
        mMediaBrowserCompat.subscribe(getSubscribeID(), mSubScriptionCallBack);
    }

    private void connectPlayerFragment() {
        MediaControllerCompat.setMediaController(BaseMediaControlActivity.this, mMediaControllerCompat);//設定後，在Fragment可以用MediaControllerCompat.getMediaController取得
        PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_player_fragment);
        if (playerFragment != null) {
            Log.d(TAG, "connectPlayerFragment: ");
            playerFragment.onConnected();//手動觸發PlayerFragment的連線
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealmUtils = new RealmUtils();
        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, PlayMusicService.class), mConnectionCallback, null);
    }

    @Override
    protected void onDestroy() {
        mRealmUtils.close();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMediaBrowserCompat != null) {
            Log.d(TAG, "onStart: connect");
            mMediaBrowserCompat.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mMediaBrowserCompat != null) {
            Log.d(TAG, "onStop: disconnect");
            mMediaBrowserCompat.disconnect();
        }
        super.onStop();
    }

    /**
     * 「播放」動作
     * @param playListPosition
     */
    public void sendActionPlayingNow(int playListPosition, int playListSongEntityId) {
        Log.d(TAG, "sendActionPlayingNow: " + playListPosition);
        Bundle bundle = new Bundle();
        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, playListPosition);
        bundle.putInt(PlayMusicService.BUNDLE_KEY_CURRENT_PLAY_SONG_ID, playListSongEntityId);
        mTransportControls.sendCustomAction(PlayMusicService.ACTION_PLAYING_NOW, bundle);
    }

    /**
     * 「切換歌單」動作
     * @param playListPosition
     */
    public void sendActionChangePlaylist(int playListPosition) {
        Log.d(TAG, "sendActionChangePlaylist: " + playListPosition);
        Bundle bundle = new Bundle();
        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, playListPosition);
        mTransportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_PLAYLIST, bundle);
    }

    @Override
    public void onClick(Object o) {

    }
}
