package com.tonynowater.smallplayer.module.dto.realm.entity;

import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by tonynowater on 2017/5/31.
 */

public class PlayListEntity extends RealmObject implements EntityInterface {

    @PrimaryKey
    private int id;

    private int folderId;

    private int position;

    @Required
    private String playListName;

    private boolean deletable = true;

    private String createDate;

    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    @Override
    public String toString() {
        return playListName;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCreateDate() {
        return String.format(MyApplication.getMyString(R.string.playlist_create_date), createDate);
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
