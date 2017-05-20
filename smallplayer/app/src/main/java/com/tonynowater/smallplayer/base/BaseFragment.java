package com.tonynowater.smallplayer.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.service.PlayMusicService;

/**
 * Created by tonynowate on 2017/5/20.
 */
public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    protected T mBinding;
    public static final int DELAY_TIME = 1000;//歌曲位置更新時間
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private MediaControllerCompat.TransportControls mTransportControls;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //送自定事件請求歌曲位置
            mTransportControls.sendCustomAction(PlayMusicService.ACTION_UPDATE_MUSIC_POSITION, null);
            mHandler.postDelayed(mRunnable, DELAY_TIME);
        }
    };

    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            BaseFragment.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            BaseFragment.this.onMetadataChanged(metadata);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            BaseFragment.this.onSessionDestroyed();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        onConnected();
    }

    @Override
    public void onStop() {
        super.onStop();
        MediaControllerCompat mediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        if (mediaControllerCompat != null) {
            mediaControllerCompat.unregisterCallback(mMediaControllerCallback);
        }
    }

    public void onConnected() {
        MediaControllerCompat mediaControllerCompat = MediaControllerCompat.getMediaController(getActivity());
        Log.d(TAG, "onConnected: " + (mediaControllerCompat == null));
        if (mediaControllerCompat != null) {
            mTransportControls = mediaControllerCompat.getTransportControls();
            onPlaybackStateChanged(mediaControllerCompat.getPlaybackState());
            onMetadataChanged(mediaControllerCompat.getMetadata());
            mediaControllerCompat.registerCallback(mMediaControllerCallback);
        }
    }

    protected abstract int getLayoutResource();
    protected abstract void onPlaybackStateChanged(PlaybackStateCompat state);
    protected abstract void onSessionDestroyed();
    protected abstract void onMetadataChanged(MediaMetadataCompat metadata);

    protected void skipToNext() {
        if (mTransportControls != null) {
            mTransportControls.skipToNext();
            initialUpdateProgressHandler();
        }
    }

    protected void skipToPrevious() {
        if (mTransportControls != null) {
            mTransportControls.skipToPrevious();
            initialUpdateProgressHandler();
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

        uninitialUpdateProgressHandler();
    }

    private void uninitialUpdateProgressHandler() {
        if (mHandlerThread != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandlerThread.quitSafely();
            mHandlerThread.interrupt();
            mHandlerThread = null;
        }
    }

    protected void play() {
        if (mTransportControls != null) {
            mTransportControls.play();
            initialUpdateProgressHandler();
        }
    }

    private void initialUpdateProgressHandler() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread(TAG);
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        mHandler.post(mRunnable);
    }
}
