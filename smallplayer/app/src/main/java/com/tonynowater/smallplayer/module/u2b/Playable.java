package com.tonynowater.smallplayer.module.u2b;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

/**
 * 能播放歌曲的物件都要實作此介面
 * Created by tonyliao on 2017/5/1.
 */
public interface Playable {
    /**
     * @return 資料庫儲存歌曲的Entity
     */
    PlayListSongEntity getPlayListSongEntity();

    /**
     * @return 是否為Youtube私人影片或Youtube已刪除的影片
     */
    boolean isDeletedOrPrivatedVideo();
}
