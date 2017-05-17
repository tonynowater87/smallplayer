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

    public MusicProvider() {
        mMusicPlayList = new ArrayList<>();
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
