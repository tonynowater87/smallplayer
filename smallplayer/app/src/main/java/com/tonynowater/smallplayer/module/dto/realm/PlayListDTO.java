package com.tonynowater.smallplayer.module.dto.realm;

import com.tonynowater.smallplayer.u2b.Playable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 播放清單
 * Created by tonynowater on 2017/5/29.
 */
public class PlayListDTO extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String playListName;

    private RealmList<PlayListSongDTO> playListSong;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public RealmList<PlayListSongDTO> getPlayListSong() {
        return playListSong;
    }

    public void setPlayListSong(RealmList<PlayListSongDTO> playListSong) {
        this.playListSong = playListSong;
    }

    @Override
    public String toString() {
        return playListName;
    }
}
