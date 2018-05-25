package com.tonynowater.smallplayer.module.room;

import android.arch.persistence.room.Room
import org.junit.Assert.*
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
class AlbumDAO_ImplTest {

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
    fun insertAlbum01() {
        val name = "測試歌單"
        albumDao.insertAlbum(AlbumEntity(album_name = name))
        val list = albumDao.queryAlbums()
        assertEquals(list.size, 1)
        assertEquals(name, list[0].album_name)
    }

    @Test
    fun insertAlbum02() {
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單2"))
        val list = albumDao.queryAlbums()
        assertEquals(list.size, 2)
        assertEquals("測試歌單2", list[1].album_name)
    }

    /**
     * test insert items with the same album name
     *
     * the newer one will replace the older one
     */
    @Test
    fun insertAlbum03() {
        val id1 = albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        val id2 = albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))

        assertEquals(1, id1)
        assertEquals(2, id2)

        val list = albumDao.queryAlbums()
        assertEquals(1, list.size)
        assertEquals(2, list[0].album_id)
        assertEquals("測試歌單1", list[0].album_name)
    }

    @Test
    fun queryByName01() {
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單2"))

        val album = albumDao.queryAlbumByName("測試歌單2")
        assertNotNull(album)
        assertEquals("測試歌單2", album.album_name)
    }

    @Test
    fun queryByName02() {
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單2"))

        val album = albumDao.queryAlbumByName("測試歌單3")
        assertNull(album)
    }

    @Test
    fun deleteAlbum01() {
        val name = "測試歌單"
        val id = albumDao.insertAlbum(AlbumEntity(album_name = name))
        albumDao.deleteAlbum(albumDao.queryAlbumById(id))

        val list = albumDao.queryAlbums()
        assertEquals(list.size, 0)
    }

    @Test
    fun updateAlbum01() {
        val id = albumDao.insertAlbum(AlbumEntity(album_name = "測試歌單"))
        val album = albumDao.queryAlbumById(id)
        album.album_name = "更新歌單名字"

        albumDao.updateAlbum(album)
        assertEquals("更新歌單名字", albumDao.queryAlbumById(id).album_name)
    }

//    @Test
//    fun switchAlbum01() {
//        val name1 = "測試歌單1"
//        val name2 = "測試歌單2"
//        val id1 = albumDao.insertAlbum(AlbumEntity(album_name = name1))
//        val id2 = albumDao.insertAlbum(AlbumEntity(album_name = name2))
//        var list = albumDao.queryAlbums()
//        albumDao.switchAlbum(name1, name2)
//        list = albumDao.queryAlbums()
//        assertEquals(albumDao.queryAlbumById(id1).album_name, name2)
//        assertEquals(albumDao.queryAlbumById(id2).album_name, name1)
//    }
}