package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by tonyliao on 2018/5/21.
 */
@Entity(tableName = "Album")
data class AlbumEntity(@PrimaryKey(autoGenerate = true) val album_id: Long = 0
                       , @ColumnInfo(name = "album_name") var album_name: String)
