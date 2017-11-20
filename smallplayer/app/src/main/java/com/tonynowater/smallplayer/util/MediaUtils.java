package com.tonynowater.smallplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.tonynowater.smallplayer.module.dto.Album;
import com.tonynowater.smallplayer.module.dto.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/3/23.
 */
public class MediaUtils {
    public static final String[] AUDIO_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,//歌名
            MediaStore.Audio.Media.ARTIST,//歌手
            MediaStore.Audio.Media.COMPOSER,//編曲
            MediaStore.Audio.Media.ALBUM,//專輯
            MediaStore.Audio.Media.ALBUM_ID,//專輯ID
            MediaStore.Audio.Media.DISPLAY_NAME,//顯示名稱即檔名
            MediaStore.Audio.Media.DURATION,//歌長(ms)
            MediaStore.Audio.Media.SIZE,//大小(bytes)
            MediaStore.Audio.Media.YEAR,//年份
            MediaStore.Audio.Media.TRACK,//歌曲編號
            MediaStore.Audio.Media.IS_MUSIC,//是否是音樂檔 (1=是)
            MediaStore.Audio.Media.MIME_TYPE,//檔案格式
            MediaStore.Audio.Media.DATA,//檔案路徑
    };

    public static final String[] ALBUM_COLUMNS = new String[]{
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ALBUM_KEY,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR,
            MediaStore.Audio.Albums.LAST_YEAR,
    };
    private static final String TAG = MediaUtils.class.getSimpleName();

    private static List<Song> mSongList = new ArrayList<>();

    /**
     * 取得手機裡的音樂檔案列表
     */
    public static List<Song> getAudioList(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , AUDIO_COLUMNS
                , MediaStore.Audio.Media.IS_MUSIC + "=" + 1
                , null
                , null);

        return getAudioList(cursor);
    }

    /**
     * 取得手機裡的音樂檔案列表
     */
    private static List<Song> getAudioList(Cursor cursor) {
        List<Song> songList = new ArrayList<>();
        Bundle bundle;
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                bundle = new Bundle();
                String sColumn;
                int iColumnIndex;
                int iField;
                for (int i = 0; i < AUDIO_COLUMNS.length; i++) {
                    sColumn = AUDIO_COLUMNS[i];
                    iColumnIndex = cursor.getColumnIndex(sColumn);
                    iField = cursor.getType(iColumnIndex);
                    switch (iField) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            System.out.println("[" + sColumn + "] cursor = FIELD_TYPE_INTEGER [" + cursor.getInt(iColumnIndex) + "]");
                            bundle.putInt(sColumn, cursor.getInt(iColumnIndex));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            System.out.println("[" + sColumn + "] cursor = FIELD_TYPE_STRING [" + cursor.getString(iColumnIndex) + "]");
                            bundle.putString(sColumn, cursor.getString(iColumnIndex));
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            System.out.println("[" + sColumn + "] cursor = [ FIELD_TYPE_NULL ]");
                            break;
                    }
                }

                songList.add(new Song(bundle));
            }
        }

        cursor.close();
        return songList;
    }

    public static List<Album> getAlbumList(Context context) {
        List<Album> albumList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
                , ALBUM_COLUMNS
                , null
                , null
                , null);
        Bundle bundle;
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast() ; cursor.moveToNext()) {
                bundle = new Bundle();
                for (int i = 0; i < ALBUM_COLUMNS.length; i++) {
                    String sColumn = ALBUM_COLUMNS[i];
                    int iColumn = cursor.getColumnIndex(sColumn);
                    switch (cursor.getType(iColumn)) {
                        case Cursor.FIELD_TYPE_STRING:
                            System.out.println("[" + sColumn + "] Album cursor = FIELD_TYPE_STRING [" + cursor.getString(iColumn) + "]");
                            bundle.putString(sColumn,cursor.getString(iColumn));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            System.out.println("[" + sColumn + "] Album cursor = FIELD_TYPE_INTEGER [" + cursor.getInt(iColumn) + "]");
                            bundle.putInt(sColumn,cursor.getInt(iColumn));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            bundle.putFloat(sColumn,cursor.getFloat(iColumn));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            break;
                    }
                }
                albumList.add(new Album(bundle));
            }
        }

        return albumList;
    }

    /**
     * @param bIsRefresh 是否要重讀本地歌曲列表
     * @return 將Album放進Song裡的本地歌單
     */
    public static List<Song> getSongList(Context context, boolean bIsRefresh) {

        if (!PermissionGrantedUtil.isPermissionGranted(context, PermissionGrantedUtil.REQUEST_PERMISSIONS)
                && mSongList != null
                && !bIsRefresh) {
            return mSongList;
        }

        List<Song> songList = MediaUtils.getAudioList(context);
        List<Album> albumList = MediaUtils.getAlbumList(context);
        SparseArray<Album> albumSparseArray = new SparseArray<>();
        for (Album album : albumList) {
            albumSparseArray.put(album.getmId(), album);
        }

        for (Song song : songList) {
            song.setmAlbumObj(albumSparseArray.get(song.getmAlbumID()));
        }

        mSongList = new ArrayList<>(songList);
        return mSongList;
    }
}
