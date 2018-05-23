package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.tonynowater.smallplayer.BuildConfig

/**
 * Created by tonyliao on 2018/5/23.
 */
@Database(entities = [AlbumEntity::class, SongEntity::class], version = 1)
abstract class SRoomDataBase : RoomDatabase() {
    abstract fun albumDao(): AlbumDAO
    abstract fun songDao(): SongDAO

    companion object {

        private var INSTANCE: SRoomDataBase? = null

        fun getInstance(context: Context): SRoomDataBase {
            if (INSTANCE == null) {
                synchronized(SRoomDataBase::class) {
                    INSTANCE = if (BuildConfig.DEBUG) {
                        Room.inMemoryDatabaseBuilder(context, SRoomDataBase::class.java)
                                .allowMainThreadQueries()
                                .build()
                    } else {
                        Room.databaseBuilder(context, SRoomDataBase::class.java, "small_player.db")
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}