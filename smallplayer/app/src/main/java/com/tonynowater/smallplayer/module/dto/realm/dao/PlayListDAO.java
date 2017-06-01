package com.tonynowater.smallplayer.module.dto.realm.dao;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;

/**
 * 播放清單
 * Created by tonynowater on 2017/5/29.
 */
public class PlayListDAO extends BaseDAO<PlayListEntity> {

    public static final String COLUMN_FOLDER_ID = "folderId";

    public PlayListDAO() {
        super(PlayListEntity.class);
    }

    /**
     * 插入一筆資料
     */
    public int insert(PlayListEntity playListEntity) {
        playListEntity.setId(getNextKey());
        inserOrUpdate(playListEntity);
        return playListEntity.getId();
    }

    /**
     * 更新一筆資料
     */
    public int update(PlayListEntity playListEntity) {
        PlayListEntity entity = getQuery().equalTo(COLUMN_ID, playListEntity.getId()).findFirst();
        if (entity != null) {
            inserOrUpdate(playListEntity);
            return playListEntity.getId();
        } else {
            return -1;
        }
    }

    /**
     * 刪除一筆資料
     */
    public boolean delete(PlayListEntity playListEntity) {
        PlayListEntity entity = getQuery().equalTo(COLUMN_ID, playListEntity.getId()).findFirst();
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
