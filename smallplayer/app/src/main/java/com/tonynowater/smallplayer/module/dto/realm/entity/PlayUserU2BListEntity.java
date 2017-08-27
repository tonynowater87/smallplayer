package com.tonynowater.smallplayer.module.dto.realm.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by tonynowater on 2017/8/27.
 */

public class PlayUserU2BListEntity extends RealmObject implements EntityInterface{

    @PrimaryKey
    private int id;

    @Required
    private String title;

    @Required
    private String listId;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    @Override
    public String toString() {
        return title;
    }
}
