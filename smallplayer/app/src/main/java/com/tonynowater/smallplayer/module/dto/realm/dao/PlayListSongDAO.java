package com.tonynowater.smallplayer.module.dto.realm.dao;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

/**
 * 播放清單裡的歌曲
 * Created by tonynowater on 2017/5/29.
 */
public class PlayListSongDAO extends BaseDAO<PlayListSongEntity> {

    public static final String COLUMN_LIST_ID = "listId";
    public static final String COLUMN_POSITION = "position";

    public PlayListSongDAO() {
        super(PlayListSongEntity.class);
    }

    /**
     * 插入一筆資料
     */
    public int insert(PlayListSongEntity playListSongEntity) {
        playListSongEntity.setId(getNextKey());
        playListSongEntity.setPosition(playListSongEntity.getId());
        inserOrUpdate(playListSongEntity);
        return playListSongEntity.getId();
    }

    /**
     * 更新一筆資料
     */
    public int update(PlayListSongEntity playListSongEntity) {
        PlayListSongEntity entity = getQuery().equalTo(COLUMN_ID, playListSongEntity.getId()).findFirst();
        if (entity != null) {
            inserOrUpdate(playListSongEntity);
            return playListSongEntity.getId();
        } else {
            return -1;
        }
    }

    /**
     * 刪除一筆資料
     */
    public boolean delete(PlayListSongEntity playListSongEntity) {
        PlayListSongEntity entity = getQuery().equalTo(COLUMN_ID, playListSongEntity.getId()).findFirst();
        boolean bSuccess = false;
        if (entity != null) {
            try {
                realm.beginTransaction();
                entity.deleteFromRealm();
                bSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bSuccess) {
                    realm.commitTransaction();
                } else {
                    realm.cancelTransaction();
                }
            }
        }

        return false;
    }
}
