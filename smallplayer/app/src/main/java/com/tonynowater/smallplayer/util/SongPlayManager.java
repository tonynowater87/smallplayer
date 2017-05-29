package com.tonynowater.smallplayer.util;

import android.content.Context;

import com.tonynowater.smallplayer.module.dto.Album;
import com.tonynowater.smallplayer.module.dto.Song;

import java.util.List;

/**
 * Created by tonyliao on 2017/4/30.
 */
public class SongPlayManager {

    private static SongPlayManager mInstance = null;
    private List<Song> mListSong;
    private List<Album> mListAlbum;

    private SongPlayManager(Context context) {
        mListSong = MediaUtils.getAudioList(context);
        mListAlbum = MediaUtils.getAlbumList(context);
        for (Song song : mListSong) {
            song.setmAlbumObj(getAlbumObj(song.getmAlbumID()));
        }

    }

    private Album getAlbumObj(int songAlbumId) {

        for (Album album : mListAlbum) {
            if (album.getmId() == songAlbumId) {
                return album;
            }
        }

        return null;
    }

    public static SongPlayManager getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new SongPlayManager(context);
        }

        return mInstance;
    }

    public List<Song> getSongList() {
        return mListSong;
    }

    public List<Album> getAlbumList() {
        return mListAlbum;
    }
}
