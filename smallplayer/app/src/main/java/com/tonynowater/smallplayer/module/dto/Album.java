package com.tonynowater.smallplayer.module.dto;

import android.os.Bundle;

import com.tonynowater.smallplayer.util.MediaUtils;

/**
 * Created by tonyliao on 2017/4/29.
 */
public class Album {
    private final int mId;
    private final String mAlbum;
    private final String mAlbumKey;
    private final String mAlbumArt;
    private final String mAlbumArtist;
    private final int mAlbumNumberOfSong;
    private final int mAlbumFirstYear;
    private final int mAlbumLastYear;

    public Album(Bundle bundle) {
        mId = bundle.getInt(MediaUtils.ALBUM_COLUMNS[0]);
        mAlbum = bundle.getString(MediaUtils.ALBUM_COLUMNS[1]);
        mAlbumKey = bundle.getString(MediaUtils.ALBUM_COLUMNS[2]);
        mAlbumArt = bundle.getString(MediaUtils.ALBUM_COLUMNS[3]);
        mAlbumArtist = bundle.getString(MediaUtils.ALBUM_COLUMNS[4]);
        mAlbumNumberOfSong = bundle.getInt(MediaUtils.ALBUM_COLUMNS[5]);
        mAlbumFirstYear = bundle.getInt(MediaUtils.ALBUM_COLUMNS[6]);
        mAlbumLastYear = bundle.getInt(MediaUtils.ALBUM_COLUMNS[7]);
    }

    public int getmId() {
        return mId;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public String getmAlbumKey() {
        return mAlbumKey;
    }

    public String getmAlbumArt() {
        return mAlbumArt;
    }

    public String getmAlbumArtist() {
        return mAlbumArtist;
    }

    public int getmAlbumNumberOfSong() {
        return mAlbumNumberOfSong;
    }

    public int getmAlbumFirstYear() {
        return mAlbumFirstYear;
    }

    public int getmAlbumLastYear() {
        return mAlbumLastYear;
    }
}
