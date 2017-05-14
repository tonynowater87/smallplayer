package com.tonynowater.smallplayer.service;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

/**
 * Created by tonyliao on 2017/5/12.
 */

public class MusicProvider {
    private static final String TAG = MusicProvider.class.getSimpleName();
    public static final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    private ArrayList<MediaMetadataCompat> mMusicPlayList;
    private volatile State mCurrentState = State.NON_INITIALIZED;

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED;
    }

    public interface CallBack {
        void onMusicReady(boolean sucess);
    }

    public MusicProvider() {
        mMusicPlayList = new ArrayList<>();
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public void putNewMusic(MediaMetadataCompat mediaMetadata) {
        mMusicPlayList.add(mediaMetadata);
    }

    public MediaMetadataCompat getPlayList(int index) {
        return index > mMusicPlayList.size() ? null : mMusicPlayList.get(index);
    }

    public int getPlayListSize() {
        return mMusicPlayList.size();
    }

    public boolean isPlayListAvailable () {
        return mMusicPlayList.size() > 0;
    }
}
