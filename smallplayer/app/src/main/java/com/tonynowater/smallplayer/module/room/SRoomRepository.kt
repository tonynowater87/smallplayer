package com.tonynowater.smallplayer.module.room

import com.tonynowater.smallplayer.MyApplication
import com.tonynowater.smallplayer.util.SPManager

/**
 * Created by tonyliao on 2018/5/25.
 */
object SRoomRepository : AlbumDAO, SongDAO {
    override fun deleteAlbum(album_id: Long) {
        SRoomDataBase.getInstance(MyApplication.getContext()).albumDao().deleteAlbum(album_id)
    }

    override fun deleteSongsByAlbum(album_id: Long) {
        SRoomDataBase.getInstance(MyApplication.getContext()).songDao().deleteSongsByAlbum(album_id)
    }

    override fun querySongsByAlbum(album_id: Long): List<SongEntity> {
        return SRoomDataBase.getInstance(MyApplication.getContext()).songDao().querySongsByAlbum(album_id)
    }

    override fun insertAlbum(albumEntity: AlbumEntity): Long {
        return SRoomDataBase.getInstance(MyApplication.getContext()).albumDao().insertAlbum(albumEntity)
    }

    override fun queryAlbums(): List<AlbumEntity> {
        return SRoomDataBase.getInstance(MyApplication.getContext()).albumDao().queryAlbums()
    }

    override fun queryAlbumById(id: Long): AlbumEntity {
        return SRoomDataBase.getInstance(MyApplication.getContext()).albumDao().queryAlbumById(id)
    }

    override fun queryAlbumByName(name: String): AlbumEntity {
        return SRoomDataBase.getInstance(MyApplication.getContext()).albumDao().queryAlbumByName(name)
    }

    override fun updateAlbum(albumEntity: AlbumEntity) {
        return SRoomDataBase.getInstance(MyApplication.getContext()).albumDao().updateAlbum(albumEntity)
    }

    override fun insertSong(song: SongEntity): Long {
        return SRoomDataBase.getInstance(MyApplication.getContext()).songDao().insertSong(song)
    }

    override fun querySongs(): List<SongEntity> {
        return SRoomDataBase.getInstance(MyApplication.getContext()).songDao().querySongs()
    }

    override fun updateSongAlbum(id: Long, album_id: Long) {
        return SRoomDataBase.getInstance(MyApplication.getContext()).songDao().updateSongAlbum(id, album_id)
    }

    override fun deleteSong(song: SongEntity) {
        return SRoomDataBase.getInstance(MyApplication.getContext()).songDao().deleteSong(song)
    }

    /** @return 指定id的播放清單 */
    fun queryPlayListById(album_id: Long) = queryAlbumById(album_id)

    /** @return 所有的播放清單依Position排序 */
    fun queryAllPlayListSortByPosition() = queryAlbums()

    /**
     * @param listID 播放清單的id
     * @return 播放清單裡的歌曲
     */
    fun queryPlayListSongByListId(album_id: Long) = querySongsByAlbum(album_id)

    /**
     * @param listID 播放清單的id
     * @return 播放清單裡的歌曲依Postition排序
     */
    fun queryPlayListSongByListIdSortByPosition(album_id: Long) = querySongsByAlbum(album_id)

    /**
     * @return 所有的歌曲
     */
    fun queryAllPlayListSong() = querySongs()

    /** @return 現正播放的PlayListID */
    fun queryCurrentPlayListID() = SPManager.getInstance(MyApplication.getContext()).currentPlayAlbumId

    /** 更新現正播放的PlayListID */
    fun setCurrentPlayListID(albumId: Long) {
        SPManager.getInstance(MyApplication.getContext()).currentPlayAlbumId = albumId
    }

    /**
     * 新增播放清單
     * @return 新建歌單的id
     */
    fun addNewPlayList(albumName: String) = insertAlbum(AlbumEntity(album_name = albumName))

    /**
     * 新增歌曲至播放清單
     * @return 回傳 ID
     */
    fun addSongToPlayList(song: SongEntity) = insertSong(song)

    /**
     * 從播放清單刪除清單裡的所有歌曲
     * @param album_id
     */
    fun deleteAllSongsFromPlaylist(album_id: Long) {
        deleteSongsByAlbum(album_id)
    }

    /**
     * 將歌曲List全部加到某個播放清單
     */
    fun addSongsToPlayList(songs: List<SongEntity>) {
        // TODO: 2018/5/26 not handle Private Video或Delete Video
        for (song in songs) {
            insertSong(song)
        }
    }

    /**
     * @return 目前播放清單的名稱
     */
    fun getCurrentPlayListName() = queryAlbumById(SPManager.getInstance(MyApplication.getContext()).currentPlayAlbumId).album_name

    /**
     * @return 目前播放清單的位置
     */
    fun queryCurrentPlayListPosition(): Long {
        val albums = queryAlbums()
        for (index in 0..albums.size) {
            if (albums[index].album_id == SPManager.getInstance(MyApplication.getContext()).currentPlayAlbumId) index
        }
        return 0
    }
}