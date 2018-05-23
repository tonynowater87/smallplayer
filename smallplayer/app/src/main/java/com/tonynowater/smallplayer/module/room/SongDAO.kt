package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

/**
 * Created by tonyliao on 2018/5/21.
 */
@Dao
interface SongDAO {

    @Insert(onConflict = REPLACE)
    fun insertSong(song: SongEntity): Long

}