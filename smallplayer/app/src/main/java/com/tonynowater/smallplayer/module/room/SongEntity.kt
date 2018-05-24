package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.*

/**
 * Created by tonyliao on 2018/5/21.
 */
@Entity(tableName = "Song"
        , foreignKeys = [(ForeignKey(entity = AlbumEntity::class, parentColumns = ["album_id"], childColumns = ["album_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))]
        , indices = [Index(value = ["source", "album_id"], unique = true)])
data class SongEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0
                      , @ColumnInfo(name = "source") var source: String
                      , @ColumnInfo(name = "title") var title: String
                      , @ColumnInfo(name = "singer") var singer: String
                      , @ColumnInfo(name = "duration") var duration: Long
                      , @ColumnInfo(name = "image") var image: String
                      , @ColumnInfo(name = "album_id") var albumId: Long
                      , @ColumnInfo(name = "isLocal") var isLocal: Boolean)
