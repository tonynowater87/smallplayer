package com.tonynowater.smallplayer.module.u2b;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

/**
 * 能播放歌曲的物件都要實作此介面，
 * 因為要能夠輸出PlayListSongEntity(資料庫歌曲的Entity)
 * Created by tonyliao on 2017/5/1.
 */
public interface Playable {
    PlayListSongEntity getPlayListSongEntity();
}
