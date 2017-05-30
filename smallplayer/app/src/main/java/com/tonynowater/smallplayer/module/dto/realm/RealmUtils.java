package com.tonynowater.smallplayer.module.dto.realm;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmList;

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

    /** 新增播放清單 */
    public static void addNewPlayList(final String playListName) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PlayFolder playFolder = realm.where(PlayFolder.class).equalTo("id", 0).findFirst();
                if (playFolder != null) {
                    playFolder = realm.copyToRealmOrUpdate(playFolder);
                } else {
                    playFolder = realm.createObject(PlayFolder.class, getNextKey(PlayFolder.class));
                }
                PlayListDTO playListDTO = realm.createObject(PlayListDTO.class, getNextKey(PlayListDTO.class));
                playListDTO.setPlayListName(playListName);
                playFolder.getPlayList().add(playListDTO);
            }
        });
    }
}
