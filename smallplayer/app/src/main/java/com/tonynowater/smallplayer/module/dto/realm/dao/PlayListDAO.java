package com.tonynowater.smallplayer.module.dto.realm.dao;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;

/**
 * 播放清單
 * Created by tonynowater on 2017/5/29.
 */
public class PlayListDAO extends BaseDAO<PlayListEntity> {
    private static final String TAG = PlayListDAO.class.getSimpleName();

    public static final String COLUMN_FOLDER_ID = "folderId";
    public static final String COLUMN_PLAY_LIST_NAME = "playListName";
    public static final String COLUMN_POSITION = "position";

    public PlayListDAO() {
        super(PlayListEntity.class);
    }

    /**
     * 插入一筆資料
     */
    @Override
    public int insert(PlayListEntity playListEntity) {
        playListEntity.setId(getNextKey());
        playListEntity.setPosition(playListEntity.getId());
        inserOrUpdate(playListEntity);
        return playListEntity.getId();
    }
}
