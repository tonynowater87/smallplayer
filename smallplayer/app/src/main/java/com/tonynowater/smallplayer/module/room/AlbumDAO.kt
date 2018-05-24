package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.*

/**
 * Created by tonyliao on 2018/5/23.
 */
@Dao
interface AlbumDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(albumEntity: AlbumEntity): Long

    @Query("SELECT * FROM album")
    fun queryAlbums(): List<AlbumEntity>

    @Query("SELECT * FROM album WHERE album_id = :id")
    fun queryAlbumById(id: Long): AlbumEntity

    @Query("SELECT * FROM album WHERE album_name = :name")
    fun queryAlbumByName(name: String): AlbumEntity

    @Delete()
    fun deleteAlbum(albumEntity: AlbumEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAlbum(albumEntity: AlbumEntity)
}