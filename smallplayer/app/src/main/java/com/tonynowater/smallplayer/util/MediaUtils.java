package com.tonynowater.smallplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.tonynowater.smallplayer.dto.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/3/23.
 */
public class MediaUtils
{
    public static final String[] AUDIO_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,//歌名
            MediaStore.Audio.Media.ARTIST,//歌手
            MediaStore.Audio.Media.COMPOSER,//編曲
            MediaStore.Audio.Media.ALBUM,//專輯
            MediaStore.Audio.Media.DISPLAY_NAME,//顯示名稱即檔名
            MediaStore.Audio.Media.DURATION,//歌長(ms)
            MediaStore.Audio.Media.SIZE,//大小(bytes)
            MediaStore.Audio.Media.YEAR,//年份
            MediaStore.Audio.Media.TRACK,//歌曲編號
            MediaStore.Audio.Media.IS_MUSIC,//是否是音樂檔 (1=是)
            MediaStore.Audio.Media.MIME_TYPE,//檔案格式
            MediaStore.Audio.Media.DATA,//檔案路徑
    };

    /** 取得手機裡的音樂檔案列表 */
    public static List<Song> getAudioList(Context context)
    {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_COLUMNS,
                MediaStore.Audio.Media.IS_MUSIC + "=" + 1,
                null,
                null);

        return getAudioList(cursor);
    }

    private static List<Song> getAudioList(Cursor cursor)
    {
        List<Song> songList = new ArrayList<>();
        Bundle bundle;
        if (cursor.getCount() > 0)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                bundle = new Bundle();
                String sColumn;
                int iColumnIndex;
                int iField;
                for (int i = 0; i < AUDIO_COLUMNS.length; i++)
                {
                    sColumn = AUDIO_COLUMNS[i];
                    iColumnIndex = cursor.getColumnIndex(sColumn);
                    iField = cursor.getType(iColumnIndex);
                    switch (iField)
                    {
                        case Cursor.FIELD_TYPE_INTEGER:
                            System.out.println("[" + sColumn + "] cursor = FIELD_TYPE_INTEGER [" + cursor.getInt(iColumnIndex) + "]");
                            bundle.putInt(sColumn,cursor.getInt(iColumnIndex));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            System.out.println("[" + sColumn + "] cursor = FIELD_TYPE_STRING [" + cursor.getString(iColumnIndex) + "]");
                            bundle.putString(sColumn,cursor.getString(iColumnIndex));
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
        logSongList(songList);
        return songList;
    }

    private static void logSongList(List<Song> songList)
    {
        System.out.println("logSongList");
        for (int i = 0; i < songList.size(); i++)
        {
//            System.out.println("songList = [" + songList.get(i).getmData() + "]");//這個log會缺有些歌..
            System.out.println("songList = [" + songList.get(i).getmDisplayName() + "]");
        }
    }
}
