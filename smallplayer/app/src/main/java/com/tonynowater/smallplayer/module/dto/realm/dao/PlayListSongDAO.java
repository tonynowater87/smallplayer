package com.tonynowater.smallplayer.module.dto.realm.dao;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

/**
 * 播放清單裡的歌曲
 * Created by tonynowater on 2017/5/29.
 */
public class PlayListSongDAO extends BaseDAO<PlayListSongEntity> {

    public static final String COLUMN_LIST_ID = "listId";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_TITLE = "title";

    public PlayListSongDAO() {
        super(PlayListSongEntity.class);
    }

    /**
     * 插入一筆資料
     */
    @Override
    public int insert(PlayListSongEntity playListSongEntity) {
        playListSongEntity.setId(getNextKey());
        playListSongEntity.setPosition(playListSongEntity.getId());
        inserOrUpdate(playListSongEntity);
        return playListSongEntity.getId();
    }
}
