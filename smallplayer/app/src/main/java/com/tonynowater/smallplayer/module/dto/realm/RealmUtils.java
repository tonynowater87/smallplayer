package com.tonynowater.smallplayer.module.dto.realm;

import com.tonynowater.smallplayer.module.dto.realm.dao.PlayFolderDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayListDAO;
import com.tonynowater.smallplayer.module.dto.realm.dao.PlayListSongDAO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayFolderEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
        params.put(PlayListDAO.COLUMN_FOLDER_ID, 0);
        return playListDAO.query(params);
    }

    /**
     * @param listID 播放清單的id
     * @return 播放清單裡的歌曲
     */
    public List<PlayListSongEntity> queryPlayListSongByListId(int listID) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(PlayListSongDAO.COLUMN_LIST_ID, listID);
        return playListSongDAO.query(params);
    }

    /** @return 現正播放的PlayListID */
    public int queryCurrentPlayListID() {
        return playFolderDAO.queryAll().get(0).getCurrentPlayListId();
    }

    /** 更新現正播放的PlayListID */
    public int setCurrentPlayListID(final int playListID) {
        PlayFolderEntity playFolderEntity = playFolderDAO.queryAll().get(0);
        playFolderEntity.setCurrentPlayListId(playListID);
        return playFolderDAO.insert(playFolderEntity);
    }

    /** 新增播放清單 */
    public void addNewPlayList(final String playListName) {
        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setFolderId(0);
        playListEntity.setPlayListName(playListName);
        playListDAO.insert(playListEntity);
    }

    /** 新增歌曲至播放清單 */
    public void addSongToPlayList(final int playlistID, final PlayListSongEntity playListSong) {
        playListSong.setListId(playlistID);
        playListSongDAO.insert(playListSong);
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
            playListEntity.setFolderId(playFolderDAO.insert(playFolderEntity));
            playListDAO.insert(playListEntity);
        }
    }
}
