package com.tonynowater.smallplayer.service;

import android.media.MediaMetadata;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tonyliao on 2017/5/12.
 */

public class MusicProvider {
    private static final String TAG = MusicProvider.class.getSimpleName();
    public static final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    private ConcurrentMap<String, MediaMetadata> mCurrentMusicPlayList;
    private volatile State mCurrentState = State.NON_INITIALIZED;

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED;
    }

    public interface CallBack {
        void onMusicReady(boolean sucess);
    }

    public MusicProvider() {
        mCurrentMusicPlayList = new ConcurrentHashMap<>();
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public void putNewMusic(String id, MediaMetadata mediaMetadata) {
        mCurrentMusicPlayList.put(id, mediaMetadata);
    }
}
