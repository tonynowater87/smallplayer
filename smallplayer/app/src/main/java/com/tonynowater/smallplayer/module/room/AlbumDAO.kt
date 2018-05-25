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

//    @Query("""UPDATE album SET album_name = CASE WHEN album_name = :name1 THEN :name2
//                                                       WHEN album_name = :name2 THEN :name1
//                                                  END WHERE album_name IN (:name1, :name2)""")
//    fun switchAlbum(name1: String, name2: String)
}