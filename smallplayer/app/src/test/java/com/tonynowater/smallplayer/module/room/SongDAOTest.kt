package com.tonynowater.smallplayer.module.room

import android.arch.persistence.room.Room
import android.database.sqlite.SQLiteConstraintException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

/**
 * Created by tonyliao on 2018/5/24.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SongDAOTest {
    private lateinit var db: SRoomDataBase
    private lateinit var songDAO: SongDAO

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ShadowApplication.getInstance().applicationContext, SRoomDataBase::class.java)
                .allowMainThreadQueries()
                .build()
        songDAO = db.songDao()
    }

    @Test
    fun insertSong01() {
        val album_id = db.albumDao().insertAlbum(AlbumEntity(album_name = "測試歌單"))

        val id = songDAO.insertSong(SongEntity(source = "source"
                , title = "title"
                , singer = "singer"
                , duration = 0
                , image = "image"
                , albumId = album_id
                , isLocal = false))

        assertEquals(1, id)

        val list = songDAO.querySongs()

        assertEquals(1, list.size)
        assertEquals("title", list[0].title)
        assertEquals("singer", list[0].singer)
        assertEquals("image", list[0].image)
        assertEquals(0, list[0].duration)
        assertEquals(false, list[0].isLocal)
        assertEquals(album_id, list[0].albumId)
    }

    /**
     * test if insert same source & album, the newer item will be replace older item
     */
    @Test
    fun insertSong02() {
        val album_id = db.albumDao().insertAlbum(AlbumEntity(album_name = "測試歌單"))

        songDAO.insertSong(SongEntity(source = "source"
                , title = "title1"
                , singer = "singer1"
                , duration = 0
                , image = "image1"
                , albumId = album_id
                , isLocal = false))

        songDAO.insertSong(SongEntity(source = "source"
                , title = "title2"
                , singer = "singer2"
                , duration = 0
                , image = "image2"
                , albumId = album_id
                , isLocal = false))

        val list = songDAO.querySongs()

        assertEquals(1, list.size)
        assertEquals("title2", list[0].title)
        assertEquals("singer2", list[0].singer)
        assertEquals("image2", list[0].image)
        assertEquals(0, list[0].duration)
        assertEquals(false, list[0].isLocal)
        assertEquals(album_id, list[0].albumId)
    }

    /**
     * test if album id doesn't exist,
     *
     * expected to catch [SQLiteConstraintException (foreign key constraint failed)]
     */
    @Test(expected = SQLiteConstraintException::class)
    fun insertSong03() {
        songDAO.insertSong(SongEntity(source = "source"
                , title = "title"
                , singer = "singer"
                , duration = 0
                , image = "image"
                , albumId = 111
                , isLocal = false))
    }

    @Test
    fun updateSongAlbum01() {
        val album_id1 = db.albumDao().insertAlbum(AlbumEntity(album_name = "測試歌單1"))
        val album_id2 = db.albumDao().insertAlbum(AlbumEntity(album_name = "測試歌單2"))

        val song_id = songDAO.insertSong(SongEntity(source = "source"
                , title = "title"
                , singer = "singer"
                , duration = 0
                , image = "image"
                , albumId = album_id1
                , isLocal = false))

        songDAO.updateSongAlbum(song_id, album_id2)

        val list = songDAO.querySongs()
        assertEquals("測試歌單2", db.albumDao().queryAlbumById(list[0].albumId).album_name)
    }

    @Test
    fun deleteSong01() {
        val album_id1 = db.albumDao().insertAlbum(AlbumEntity(album_name = "測試歌單1"))

        songDAO.insertSong(SongEntity(source = "source"
                , title = "title"
                , singer = "singer"
                , duration = 0
                , image = "image"
                , albumId = album_id1
                , isLocal = false))

        var list = songDAO.querySongs()

        assertEquals(1, list.size)

        songDAO.deleteSong(list[0])

        list = songDAO.querySongs()

        assertEquals(0, list.size)
    }
}