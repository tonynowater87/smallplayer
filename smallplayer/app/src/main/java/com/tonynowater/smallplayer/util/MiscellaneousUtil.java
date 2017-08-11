package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.service.PlayMusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/27.
 */
public class MiscellaneousUtil {
    private static final String TAG = MiscellaneousUtil.class.getSimpleName();

    private MiscellaneousUtil() {}

    /**
     * 檢查List
     * @return true:OK false:不OK
     */
    public static boolean isListOK(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static void hideKeyboard(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(token, 0);
    }

    /**
     * @param logName log要顯示的訊息
     * @param startTime 開始時間
     * @return 開始時間到call這個method的秒數
     */
    public static void calcRunningTime(String logName, long startTime) {
        Log.d(TAG, logName + "花費 : " + (System.currentTimeMillis() - startTime) / 1000d + "秒");
    }

    /**
     * 送從歌單刪除歌曲的Action
     * @param mPlayListId
     * @param playListSongEntityId
     * @param mTransportControls
     */
    public static void sendRemoveSongFromListAction(int mPlayListId, int playListSongEntityId, MediaControllerCompat.TransportControls mTransportControls) {
        Bundle bundle = new Bundle();
        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, mPlayListId);
        bundle.putInt(PlayMusicService.BUNDLE_KEY_SONG_ID, playListSongEntityId);
        if (mTransportControls != null) {
            mTransportControls.sendCustomAction(PlayMusicService.ACTION_REMOVE_SONG_FROM_PLAYLIST, bundle);
        }
    }

    /**
     * @param playListEntities 本地儲存的歌單
     * @return 只有本地儲存的歌單名稱的字串List
     */
    public static List<String> getPlayListTitles(List<PlayListEntity> playListEntities) {
        List<String> listRet = new ArrayList<>();
        for (int i = 0; i < playListEntities.size(); i++) {
            listRet.add(playListEntities.get(i).getPlayListName());
        }
        return listRet;
    }
}
