package com.tonynowater.smallplayer.module.room;

import android.arch.persistence.room.Room
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

/**
 * Created by tonyliao on 2018/5/23.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AlbumDAOTest {

    private lateinit var db: SRoomDataBase
    private lateinit var albumDao: AlbumDAO

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ShadowApplication.getInstance().applicationContext, SRoomDataBase::class.java)
                .allowMainThreadQueries()
                .build()
        albumDao = db.albumDao()
    }

    @Test
    fun insert0_1() {
        val name = "測試歌單"
        albumDao.insertAlbum(AlbumEntity(album_name = name))
        val list = albumDao.queryAlbums()
        Assert.assertEquals(list.size, 1)
        Assert.assertEquals(name, list[0].album_name)
    }

    @Test
    fun insert0_2() {
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單2"))
        val list = albumDao.queryAlbums()
        Assert.assertEquals(list.size, 2)
        Assert.assertEquals("測試歌單2", list[1].album_name)
    }

    /**
     * 歌單名字重複插入測試
     */
    @Test
    fun insert0_3() {
        val id1 = albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        val id2 = albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))

        Assert.assertEquals(1, id1)
        Assert.assertEquals(2, id2)

        val list = albumDao.queryAlbums()
        Assert.assertEquals(list.size, 1)
        Assert.assertEquals(1, list[0].album_id)//重複插入會覆蓋
        Assert.assertEquals("測試歌單1", list[0].album_name)
    }

    @Test
    fun delete0_1() {
        val name = "測試歌單"
        val id = albumDao.insertAlbum(AlbumEntity(album_name = name))
        albumDao.deleteAlbum(albumDao.queryAlbumById(id))

        val list = albumDao.queryAlbums()
        Assert.assertEquals(list.size, 0)
    }

    @Test
    fun update0_1() {
        val id = albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單"))
        val album = albumDao.queryAlbumById(id)
        album.album_name = "更新歌單名字"

        albumDao.updateAlbum(album)
        Assert.assertEquals("更新歌單名字", albumDao.queryAlbumById(id).album_name)
    }
}