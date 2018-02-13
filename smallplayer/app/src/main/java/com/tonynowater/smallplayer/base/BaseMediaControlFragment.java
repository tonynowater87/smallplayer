package com.tonynowater.smallplayer.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;

/**
 * Fragment需要和Service交互的基底類別
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BaseMediaControlFragment<T extends ViewDataBinding> extends BaseFragment<T> {

    private static final String TAG = BaseMediaControlFragment.class.getSimpleName();
    protected MediaControllerCompat.TransportControls mTransportControls;
    protected RealmUtils mRealmUtils;

    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if (isAdded()) {
                if (!TextUtils.isEmpty(state.getErrorMessage())) {
                    showToast(state.getErrorMessage().toString());
                }
                BaseMediaControlFragment.this.onPlaybackStateChanged(state);
            } else {
                Log.w(TAG, "onPlaybackStateChanged: isNoAdded");
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            if (isAdded()) {
                BaseMediaControlFragment.this.onMetadataChanged(metadata);
            } else {
                Log.w(TAG, "onMetadataChanged: isNoAdded");
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            BaseMediaControlFragment.this.onSessionDestroyed();
        }
    };

    public BaseMediaControlFragment() {
        mRealmUtils = new RealmUtils();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        onConnected();
    }

    @Override
    public void onStop() {
        MediaControllerCompat mediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(mMediaControllerCallback);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mRealmUtils.close();
        super.onDestroy();
    }

    public void onConnected() {
        MediaControllerCompat mediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        Log.d(TAG, "onConnected: " + (mediaControllerCompat == null));
        if (mediaControllerCompat != null) {
            mTransportControls = mediaControllerCompat.getTransportControls();

            if (isAdded()) {
                onFragmentMediaConnected();
                onPlaybackStateChanged(mediaControllerCompat.getPlaybackState());
                onMetadataChanged(mediaControllerCompat.getMetadata());
            } else {
                Log.w(TAG, "onConnected: isNotAdded");
            }
            mediaControllerCompat.registerCallback(mMediaControllerCallback);
        }
    }

    protected abstract void onPlaybackStateChanged(PlaybackStateCompat state);
    protected abstract void onSessionDestroyed();
    protected abstract void onMetadataChanged(MediaMetadataCompat metadata);
    protected abstract void onFragmentMediaConnected();

    protected void skipToNext() {
        if (mTransportControls != null) {
            mTransportControls.skipToNext();
        }
    }

    protected void skipToPrevious() {
        if (mTransportControls != null) {
            mTransportControls.skipToPrevious();
        }
    }

    protected void pause() {
        if (mTransportControls != null) {
            mTransportControls.pause();
        }
    }

    protected void stop() {
        if (mTransportControls != null) {
            mTransportControls.stop();
        }
    }

    protected void play() {
        if (mTransportControls != null) {
            mTransportControls.play();
        }
    }

    /**
     * 送指定Action動作至Service
     * @param action
     * @param bundle
     */
    public void sendActionToService(String action, Bundle bundle) {
        mTransportControls.sendCustomAction(action, bundle);
    }
}
