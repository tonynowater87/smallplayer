package com.tonynowater.smallplayer.module.dto.realm;

import android.util.Log;

import com.tonynowater.smallplayer.module.dto.realm.dao.BaseDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.DBQueryCondition;
import com.tonynowater.smallplayer.module.dto.realm.dao.DBQueryResult;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayFolderDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayListDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayListSongDAO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayFolderEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.util.DateUtil;
import com.tonynowater.smallplayer.util.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.realm.Sort;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class RealmUtils implements Closeable{
    private static final String TAG = RealmUtils.class.getSimpleName();
    private static final String DEFAULT_PLAY_LIST_NAME = "預設歌單";
    private PlayFolderDAO playFolderDAO;
    private PlayListDAO playListDAO;
    private PlayListSongDAO playListSongDAO;

    public RealmUtils() {
        playFolderDAO = new PlayFolderDAO();
        playListDAO = new PlayListDAO();
        playListSongDAO = new PlayListSongDAO();
        initialData();
    }

    /** @return 所有的播放清單 */
    public List<PlayListEntity> queryAllPlayList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListDAO.COLUMN_FOLDER_ID, BaseDAO.DEFAULT_ID);
        return playListDAO.query(DBQueryResult.Copy, DBQueryCondition.EqualTo, params);
    }
    
    /** @return 指定id的播放清單 */
    public List<PlayListEntity> queryPlayListById(int id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListDAO.COLUMN_ID, id);
        return playListDAO.query(DBQueryResult.Copy, DBQueryCondition.EqualTo, params);
    }

    /** @return 所有的播放清單依Position排序 */
    public List<PlayListEntity> queryAllPlayListSortByPosition() {
        return playListDAO.copyFromRealm(playListDAO.getQuery()
                .equalTo(PlayListDAO.COLUMN_FOLDER_ID, BaseDAO.DEFAULT_ID)
                .findAllSorted(PlayListDAO.COLUMN_POSITION, Sort.ASCENDING));
    }

    /**
     * @param listID 播放清單的id
     * @return 播放清單裡的歌曲
     */
    public List<PlayListSongEntity> queryPlayListSongByListId(int listID) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListSongDAO.COLUMN_LIST_ID, listID);
        return playListSongDAO.query(DBQueryResult.Copy, DBQueryCondition.EqualTo, params);
    }

    /**
     * @param listID 播放清單的id
     * @return 播放清單裡的歌曲依Postition排序
     */
    public List<PlayListSongEntity> queryPlayListSongByListIdSortByPosition(int listID) {
        return playListSongDAO.copyFromRealm(playListSongDAO.getQuery()
                        .equalTo(PlayListSongDAO.COLUMN_LIST_ID, listID)
                        .findAllSorted(PlayListSongDAO.COLUMN_POSITION, Sort.ASCENDING));
    }

    /**
     * @return 所有的歌曲
     */
    public List<PlayListSongEntity> queryAllPlayListSong() {
        return playListSongDAO.queryAll();
    }

    /** @return 現正播放的PlayListID */
    public int queryCurrentPlayListID() {
        return playFolderDAO.queryAll().get(0).getCurrentPlayListId();
    }

    /** 更新現正播放的PlayListID */
    public int setCurrentPlayListID(final int playListID) {
        PlayFolderEntity playFolderEntity = playFolderDAO.copyFromReal(playFolderDAO.queryAll().get(0));
        playFolderEntity.setCurrentPlayListId(playListID);
        return playFolderDAO.update(playFolderEntity);
    }

    /**
     * 新增播放清單
     * @return 新建歌單的id
     */
    public int addNewPlayList(final String playListName) {
        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setFolderId(BaseDAO.DEFAULT_ID);
        playListEntity.setPlayListName(playListName);
        playListEntity.setCreateDate(DateUtil.getCurrentDate());
        return playListDAO.insert(playListEntity);
    }

    /**
     * 新增歌曲至播放清單
     * @return 成功加入 => 回傳 ID
     *         重覆加入 => 回傳 已存在裡面的歌曲ID
     */
    public int addSongToPlayList(final int playlistID, final PlayListSongEntity playListSong) {

        HashMap<String, Object> param = new HashMap<>();
        param.put(PlayListSongDAO.COLUMN_LIST_ID, playlistID);
        param.put(PlayListSongDAO.COLUMN_TITLE, playListSong.getTitle());
        List<PlayListSongEntity> listSongEntities = playListSongDAO.query(DBQueryResult.Copy, DBQueryCondition.EqualTo, param);
        if (listSongEntities.size() > 0) {
            Logger.getInstance().d(TAG, "addSongToPlayList: 重覆加入 " + playListSong.getTitle());
            return listSongEntities.get(0).getId();
        }

        playListSong.setListId(playlistID);
        return playListSongDAO.insert(playListSong);
    }

    /**
     * 從播放清單刪除一首歌曲
     * @param songId
     */
    public void deleteSong(int songId) {
        HashMap<String, Object> param = new HashMap<>();
        param.put(PlayListSongDAO.COLUMN_ID, songId);
        List<PlayListSongEntity> listSongEntities = playListSongDAO.query(DBQueryResult.NotCopy, DBQueryCondition.EqualTo, param);
        if (listSongEntities.size() > 0) {
            playListSongDAO.delete(listSongEntities.get(0));
        }
    }

    /**
     * 從播放清單刪除清單裡的所有歌曲
     * @param playlistId
     */
    public void deleteAllSongsFromPlaylist(int playlistId) {
        List<PlayListSongEntity> listSongEntities = queryPlayListSongByListId(playlistId);
        for (int i = 0; i < listSongEntities.size(); i++) {
            deleteSong(listSongEntities.get(i).getId());
        }
    }

    /**
     * 刪除播放清單以及清單裡的所有歌曲
     * @param playListEntity
     */
    public void deletePlayList(PlayListEntity playListEntity) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListSongDAO.COLUMN_LIST_ID, playListEntity.getId());
        List<PlayListSongEntity> playListSongEntities = playListSongDAO.query(DBQueryResult.NotCopy, DBQueryCondition.EqualTo, params);
        for (PlayListSongEntity entity : playListSongEntities) {
            //Logger.getInstance().d(TAG, "deletePlayList: " + entity.getTitle());
            playListSongDAO.delete(entity);
        }
        playListDAO.delete(playListEntity);
    }

    /**
     * 更新播放清單的位置
     * @param from
     * @param to
     */
    public void updatePlayListPosition(PlayListEntity from, PlayListEntity to) {
        Logger.getInstance().d(TAG, "updatePlayListPosition: from:" + from.getPosition());
        Logger.getInstance().d(TAG, "updatePlayListPosition: to:" + to.getPosition());
        int fromPosition = from.getPosition();
        from.setPosition(to.getPosition());
        to.setPosition(fromPosition);
        playListDAO.update(from);
        playListDAO.update(to);
    }

    /**
     * 更新播放清單的位置
     * @param playListEntityList
     */
    public void updatePlayList(List<PlayListEntity> playListEntityList) {
        for (PlayListEntity entity : playListEntityList) {
            playListDAO.update(entity);
        }
    }

    /**
     * 更新播放清單
     * @param playListEntity
     */
    public void updatePlayList(PlayListEntity playListEntity) {
        playListDAO.update(playListEntity);
    }

    /**
     * 更新播放歌曲的位置
     * @param from
     * @param to
     */
    public void updatePlayListSongPosition(PlayListSongEntity from, PlayListSongEntity to) {
        int fromPosition = from.getPosition();
        from.setPosition(to.getPosition());
        to.setPosition(fromPosition);
        playListSongDAO.update(from);
        playListSongDAO.update(to);
    }

    @Override
    public void close() {
        try {
            playListDAO.close();
            playFolderDAO.close();
            playListSongDAO.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 初始化預設歌單 */
    private void initialData() {
        List<PlayFolderEntity> playFolderEntities = playFolderDAO.queryAll();
        if (playFolderEntities == null || playFolderEntities.size() == 0) {
            PlayFolderEntity playFolderEntity = new PlayFolderEntity();
            PlayListEntity playListEntity = new PlayListEntity();
            playListEntity.setPlayListName(DEFAULT_PLAY_LIST_NAME);
            playListEntity.setDeletable(false);//預設歌單不可刪除
            playListEntity.setFolderId(playFolderDAO.insert(playFolderEntity));
            playListEntity.setCreateDate(DateUtil.getCurrentDate());
            playListDAO.insert(playListEntity);
        }
    }

    /**
     * 將歌曲List全部加到某個播放清單
     * @param playlistId 播放清單ID
     * @param playableList 歌曲List
     */
    public void addSongsToPlayList(int playlistId, List<? extends Playable> playableList) {
        Playable playable;
        for (int i = 0; i < playableList.size(); i++) {
            playable = playableList.get(i);
            if (playable.isDeletedOrPrivatedVideo()) {
                //不要加入Private Video或Delete Video
                Logger.getInstance().d(TAG, "addSongsToPlayList isDeletedOrPrivatedVideo : " + i);
                continue;
            }
            addSongToPlayList(playlistId, playableList.get(i).getPlayListSongEntity());
        }
    }

    /**
     * @return 目前播放清單的名稱
     */
    public String getCurrentPlayListName() {
        return queryPlayListById(queryCurrentPlayListID()).get(0).getPlayListName();
    }

    /**
     * @return 目前播放清單的位置
     */
    public int queryCurrentPlayListPosition() {
        int curId = queryCurrentPlayListID();
        List<PlayListEntity> listEntities = queryAllPlayListSortByPosition();
        for (int i = 0, len = listEntities.size(); i < len; i++) {
            if (listEntities.get(i).getId() == curId) {
                return i;
            }
        }
        return 0;
    }
}
