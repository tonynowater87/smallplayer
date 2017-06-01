package com.tonynowater.smallplayer.module.dto.realm.dao;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayFolderEntity;

/**
 * Created by tonynowater on 2017/5/30.
 */

public class PlayFolderDAO extends BaseDAO<PlayFolderEntity> {

    public PlayFolderDAO() {
        super(PlayFolderEntity.class);
    }

    /**
     * 插入一筆資料
     */
    public int insert(PlayFolderEntity playFolderEntity) {
        playFolderEntity.setId(getNextKey());
        inserOrUpdate(playFolderEntity);
        return playFolderEntity.getId();
    }

    /**
     * 更新一筆資料
     */
    public int update(PlayFolderEntity playFolderEntity) {
        PlayFolderEntity entity = getQuery().equalTo(COLUMN_ID, playFolderEntity.getId()).findFirst();
        if (entity != null) {
            inserOrUpdate(playFolderEntity);
            return playFolderEntity.getId();
        } else {
            return -1;
        }
    }

    /**
     * 刪除一筆資料
     */
    public boolean delete(PlayFolderEntity playFolderEntity) {
        PlayFolderEntity entity = getQuery().equalTo(COLUMN_ID, playFolderEntity.getId()).findFirst();
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
