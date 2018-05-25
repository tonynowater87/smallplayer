package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.*

/**
 * Created by tonyliao on 2018/5/21.
 */
@Entity(tableName = SongTable
        , foreignKeys = [(ForeignKey(entity = AlbumEntity::class, parentColumns = [AlbumId], childColumns = [AlbumId], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))]
        , indices = [Index(value = [SongSource, AlbumId], unique = true)])
data class SongEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0
                      , @ColumnInfo(name = SongSource) var source: String
                      , @ColumnInfo(name = SongTitle) var title: String
                      , @ColumnInfo(name = SongSinger) var singer: String
                      , @ColumnInfo(name = SongDuration) var duration: Long
                      , @ColumnInfo(name = SongImage) var image: String
                      , @ColumnInfo(name = AlbumId) var albumId: Long
                      , @ColumnInfo(name = SongIsLocal) var isLocal: Boolean)
