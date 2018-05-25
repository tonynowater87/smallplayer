@file:JvmName("DBConstants")
package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 * Created by tonyliao on 2018/5/21.
 */
@Entity(tableName = AlbumTable, indices = [Index(AlbumName, unique = true)])
data class AlbumEntity(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = AlbumId) val album_id: Long = 0
                       , @ColumnInfo(name = AlbumName) var album_name: String)
