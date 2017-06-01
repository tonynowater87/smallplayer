package com.tonynowater.smallplayer.module.dto.realm.entity;

import com.tonynowater.smallplayer.module.dto.realm.dao.BaseDAO;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tonynowater on 2017/5/31.
 */
public class PlayFolderEntity extends RealmObject {
    @PrimaryKey
    private int id;

    private int currentPlayListId = BaseDAO.DEFAULT_ID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrentPlayListId() {
        return currentPlayListId;
    }

    public void setCurrentPlayListId(int currentPlayListId) {
        this.currentPlayListId = currentPlayListId;
    }
}
