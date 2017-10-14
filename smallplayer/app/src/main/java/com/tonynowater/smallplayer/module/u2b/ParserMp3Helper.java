package com.tonynowater.smallplayer.module.u2b;

import android.util.Log;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

/**
 * Created by tonynowater on 2017/6/21.
 */
public class ParserMp3Helper {
    private static final String TAG = ParserMp3Helper.class.getSimpleName();
    public static final String DOWNLOADURL = "/download/get/?i=";
//    public static final int INDEX_OF_END_DOWNLOADURL = 37;
    public static final int INDEX_OF_END_DOWNLOADURL = 51;

    public static void parserMp3UrlFromHtml(String xml, PlayListSongEntity playListSongEntity, U2BApi.OnU2BApiCallback callback) {
        int index = xml.indexOf(DOWNLOADURL);
        if (index != -1) {
            String getUrl = xml.substring(index, index + DOWNLOADURL.length() + INDEX_OF_END_DOWNLOADURL);
            Log.d(TAG, "XmlPullParserHelper get url : " + getUrl);
            U2BApi.newInstance().downloadMP3FromU2B(getUrl, playListSongEntity, callback);
        }
    }
}
