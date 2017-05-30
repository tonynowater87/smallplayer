package com.tonynowater.smallplayer.module.dto.realm;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class RealmUtils {
    private static final String TAG = RealmUtils.class.getSimpleName();

    private static int getNextKey(Class realmObject) {
        Realm realm = Realm.getDefaultInstance();
        if (realm.where(realmObject).max("id") == null) {
            Log.d(TAG, "getNextKey: null" );
            return 0;
        } else {
            int id = realm.where(realmObject).max("id").intValue() + 1;
            Log.d(TAG, "getNextKey:" + id );
            return id;
        }
    }

    /** @return 所有的播放清單 */
    public static List<PlayListDTO> queryAllPlayList() {
        Realm realm = Realm.getDefaultInstance();
        PlayFolder playFolder = realm.where(PlayFolder.class).equalTo("id", 0).findFirst();
        if (playFolder != null) {
            return playFolder.getPlayList();
        } else {
            return new ArrayList<>();
        }
    }

    /** 初始化播放清單的根目錄 */
    public static PlayFolder initalPlayFolder() {
        Realm realm = Realm.getDefaultInstance();
        PlayFolder playFolder = realm.where(PlayFolder.class).equalTo("id", 0).findFirst();
        if (playFolder != null) {
            playFolder = realm.copyToRealmOrUpdate(playFolder);
        } else {
            playFolder = realm.createObject(PlayFolder.class, getNextKey(PlayFolder.class));
        }

        return playFolder;
    }

    /** 新增播放清單 */
    public static void addNewPlayList(final String playListName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PlayFolder playFolder = initalPlayFolder();
                PlayListDTO playListDTO = realm.createObject(PlayListDTO.class, getNextKey(PlayListDTO.class));
                playListDTO.setPlayListName(playListName);
                playFolder.getPlayList().add(playListDTO);
            }
        });
    }

    /** 新增歌曲至播放清單 */
    public static void addSongToPlayList(final int id, final PlayListSongDTO playListSong) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PlayListDTO playListDTO = queryPlayList(id);
                PlayListSongDTO playListSongDTO = queryPlayListSong(playListSong);
                playListDTO.getPlayListSong().add(playListSongDTO);
            }
        });
    }

    /**
     * 用id查詢是否PlayListDTO存在
     */
    private static PlayListDTO queryPlayList(int id) {
        Realm realm = Realm.getDefaultInstance();
        PlayListDTO playListDTO = realm.where(PlayListDTO.class).equalTo("id", id).findFirst();
        if (playListDTO != null) {
            playListDTO = realm.copyToRealmOrUpdate(playListDTO);
        } else {
            playListDTO = realm.createObject(PlayListDTO.class, getNextKey(PlayListDTO.class));
        }

        return playListDTO;
    }

    /**
     * 用id查詢是否PlayListSongDTO存在
     */
    private static PlayListSongDTO queryPlayListSong(PlayListSongDTO playListSong) {
        Realm realm = Realm.getDefaultInstance();
        PlayListSongDTO playListSongDTO = realm.where(PlayListSongDTO.class).equalTo("id", playListSong.getId()).findFirst();
        if (playListSongDTO != null) {
            //若已存在時call copyToRealm會出錯, 所以改用copyToRealmOrUpdate
            playListSongDTO = realm.copyToRealmOrUpdate(playListSongDTO);
        } else {
            playListSongDTO = realm.copyToRealm(playListSong);
        }

        return playListSongDTO;
    }
}
