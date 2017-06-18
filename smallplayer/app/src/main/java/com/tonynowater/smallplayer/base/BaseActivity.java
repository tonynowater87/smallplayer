package com.tonynowater.smallplayer.base;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.view.PlayerFragment;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements OnClickSomething {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected T mBinding;
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
            BaseActivity.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.d(TAG, "onMetadataChanged: ");
            BaseActivity.this.onMetadataChanged(metadata);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "onSessionDestroyed: ");
            BaseActivity.this.onSessionDestroyed();
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
            BaseActivity.this.onChildrenLoaded(children);
        }

        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children, @NonNull Bundle options) {
            super.onChildrenLoaded(parentId, children, options);
            BaseActivity.this.onChildrenLoaded(children, options);
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
            Log.d(TAG, "updateState: " + mMediaBrowserCompat.getSessionToken());

            if (mMediaBrowserCompat.getSessionToken() == null) {
                throw new IllegalArgumentException("No session token");
            }

            try {
                startService(new Intent(BaseActivity.this, PlayMusicService.class));
                if (!TextUtils.isEmpty(getSubscribeID())) {
                    mMediaBrowserCompat.subscribe(getSubscribeID(), mSubScriptionCallBack);
                }
                mMediaControllerCompat = new MediaControllerCompat(BaseActivity.this, mMediaBrowserCompat.getSessionToken());
                mTransportControls = mMediaControllerCompat.getTransportControls();
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(BaseActivity.this, mMediaControllerCompat);
                mPlaybackStateCompat = mMediaControllerCompat.getPlaybackState();
                onMediaServiceConnected();
                onPlaybackStateChanged(mPlaybackStateCompat);
                onMetadataChanged(mMediaControllerCompat.getMetadata());
                connectPlayerFragment();

            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "updateState: " + e.toString());
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

    private void connectPlayerFragment() {
        MediaControllerCompat.setMediaController(BaseActivity.this, mMediaControllerCompat);//設定後，在Fragment可以用MediaControllerCompat.getMediaController取得
        PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_player_fragment);
        if (playerFragment != null) {
            Log.d(TAG, "connectPlayerFragment: ");
            playerFragment.onConnected();//手動觸發PlayerFragment的連線
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealmUtils = new RealmUtils();
        mBinding = DataBindingUtil.setContentView(this, getLayoutResource());
        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, PlayMusicService.class), mConnectionCallback, null);
    }

    @Override
    protected void onDestroy() {
        mRealmUtils.close();
        super.onDestroy();
    }

    protected abstract int getLayoutResource();

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

    // TODO: 2017/6/17 播放歌曲若是已存在歌單的歌曲要可以直接切到當首播放
    /**
     * 「播放」動作
     * @param playListPosition
     */
    public void sendActionPlayingNow(int playListPosition) {
        Log.d(TAG, "sendActionPlayingNow: " + playListPosition);
        Bundle bundle = new Bundle();
        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, playListPosition);
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
