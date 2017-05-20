package com.tonynowater.smallplayer.base;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonynowate on 2017/5/20.
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements OnClickSomething<Playable> {
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected T mBinding;
    protected MediaControllerCompat.TransportControls mTransportControls;
    private BaseFragment mPlayerFragment;
    private PlaybackStateCompat mPlaybackStateCompat;
    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
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

    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "updateState: " + mMediaBrowserCompat.getSessionToken());

            if (mMediaBrowserCompat.getSessionToken() == null) {
                throw new IllegalArgumentException("No session token");
            }

            try {
                mMediaControllerCompat = new MediaControllerCompat(BaseActivity.this, mMediaBrowserCompat.getSessionToken());
                MediaControllerCompat.setMediaController(BaseActivity.this, mMediaControllerCompat);//設定後，在Fragment可以用MediaControllerCompat.getMediaController取得
                mPlayerFragment.onConnected();//手動觸發PlayerFragment的連線
                mTransportControls = mMediaControllerCompat.getTransportControls();
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(BaseActivity.this, mMediaControllerCompat);
                mPlaybackStateCompat = mMediaControllerCompat.getPlaybackState();
                onPlaybackStateChanged(mPlaybackStateCompat);
                onMetadataChanged(mMediaControllerCompat.getMetadata());
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutResource());
        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, PlayMusicService.class), mConnectionCallback, null);
    }

    protected abstract int getLayoutResource();

    @Override
    protected void onStart() {
        super.onStart();
        mPlayerFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_player_fragment);
        if (mMediaBrowserCompat != null && mPlayerFragment != null) {
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
     * 將播放資料傳至Service
     * @param mediaMetadata
     */
    protected void sendMetaDataToService(MediaMetadataCompat mediaMetadata) {
        Log.d(TAG, "sendMetaDataToService: " + mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        Intent it = new Intent(this,PlayMusicService.class);
        it.setAction(PlayMusicService.ACTION_ADD_NEW_MUSIC);
        it.putExtra(PlayMusicService.BUNDLE_KEY_MEDIAMETADATA, mediaMetadata);
        startService(it);
    }

    @Override
    public void onClick(Playable playable) {

    }
}
