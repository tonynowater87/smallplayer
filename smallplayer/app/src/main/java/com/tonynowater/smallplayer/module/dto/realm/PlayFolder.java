package com.tonynowater.smallplayer.module.dto.realm;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tonynowater on 2017/5/30.
 */

public class PlayFolder extends RealmObject {
    @PrimaryKey
    private int id;

    private RealmList<PlayListDTO> playList = new RealmList<>();

    public RealmList<PlayListDTO> getPlayList() {
        return playList;
    }

    public void setPlayList(RealmList<PlayListDTO> playList) {
        this.playList = playList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
