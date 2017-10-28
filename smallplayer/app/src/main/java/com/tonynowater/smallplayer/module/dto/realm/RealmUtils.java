package com.tonynowater.smallplayer.module.dto.realm;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.tonynowater.smallplayer.module.dto.realm.dao.BaseDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayFolderDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayListDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayListSongDAO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayFolderEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.util.DateUtil;

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
        return playListDAO.queryForCopy(params);
    }
    
    /** @return 指定id的播放清單 */
    public List<PlayListEntity> queryPlayListById(int id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListDAO.COLUMN_ID, id);
        return playListDAO.queryForCopy(params);
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
        return playListSongDAO.queryForCopy(params);
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

    /** @return 現正播放的PlayListPosition */
    public int queryCurrentPlayListPosition() {
        try {
            return playListDAO.getQuery().equalTo(PlayListDAO.COLUMN_ID, playFolderDAO.queryAll().get(0).getCurrentPlayListId()).findFirst().getPosition();
        } catch (NullPointerException e) {
            // FIXME: 2017/6/6 這邊若刪除正在播放的清單會當機，待處理，先 try catch起來
            return BaseDAO.DEFAULT_ID;
        }
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
        List<PlayListSongEntity> listSongEntities = playListSongDAO.queryForCopy(param);
        if (listSongEntities.size() > 0) {
            Log.d(TAG, "addSongToPlayList: 重覆加入 " + playListSong.getTitle());
            return listSongEntities.get(0).getId();
        }

        playListSong.setListId(playlistID);
        return playListSongDAO.insert(playListSong);
    }

    /**
     * 從播放清單刪除一首歌曲
     * @param playListSong
     */
    public void deleteSongFromPlayList(PlayListSongEntity playListSong) {
        playListSongDAO.delete(playListSong);
    }

    /**
     * 從播放清單刪除清單裡的所有歌曲
     * @param playListEntity
     */
    public void deleteAllSongsFromPlaylist(PlayListEntity playListEntity) {
        List<PlayListSongEntity> listSongEntities = queryPlayListSongByListId(playListEntity.getId());
        for (int i = 0; i < listSongEntities.size(); i++) {
            deleteSongFromPlayList(listSongEntities.get(i));
        }
    }

    /**
     * 刪除播放清單以及清單裡的所有歌曲
     * @param playListEntity
     */
    // FIXME: 2017/10/27 刪除歌單後，位置比刪除歌單大的歌單位置要更新，否則選不到
    public void deletePlayList(PlayListEntity playListEntity) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListSongDAO.COLUMN_LIST_ID, playListEntity.getId());
        List<PlayListSongEntity> playListSongEntities = playListSongDAO.queryNotCopy(params);
        for (PlayListSongEntity entity : playListSongEntities) {
            //Log.d(TAG, "deletePlayList: " + entity.getTitle());
            playListSongDAO.delete(entity);
        }

        playListDAO.delete(playListEntity);

        int size = queryAllPlayList().size();
        HashMap<String, Object> param = new HashMap<>();
        param.put(PlayListDAO.COLUMN_POSITION, size);
        List<PlayListEntity> playListEntities = playListDAO.queryGreaterOrEqualForCopy(param);
        for (PlayListEntity entity : playListEntities) {
            entity.setPosition(entity.getPosition() - 1);
            playListDAO.update(entity);
        }
    }

    /**
     * 更新播放清單的位置
     * @param from
     * @param to
     */
    public void updatePlayListPosition(PlayListEntity from, PlayListEntity to) {
        Log.d(TAG, "updatePlayListPosition: from:" + from.getPosition());
        Log.d(TAG, "updatePlayListPosition: to:" + to.getPosition());
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
                Log.d(TAG, "addSongsToPlayList isDeletedOrPrivatedVideo : " + i);
                continue;
            }
            addSongToPlayList(playlistId, playableList.get(i).getPlayListSongEntity());
        }
    }

    /**
     * @return 目前播放清單的名稱
     */
    // TODO: 2017/10/10 這邊有可能會出現IndexOutOfBoundsException，先暫時這樣處理
    public String getCurrentPlayListName() {
        List<PlayListEntity> listEntities = queryAllPlayListSortByPosition();
        int currentPlayListPosition = queryCurrentPlayListPosition();
        if (currentPlayListPosition < listEntities.size()) {
            return listEntities.get(currentPlayListPosition).getPlayListName();
        } else {
            String msg = String.format("current pos:%s, all list size:%s", currentPlayListPosition, listEntities.size());
            Log.e(TAG, msg);
            FirebaseCrash.logcat(Log.ERROR, "RealmUtils", msg);
            return "";
        }
    }
}
