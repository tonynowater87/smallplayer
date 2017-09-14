package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.tonynowater.smallplayer.module.dto.HasVideoId;
import com.tonynowater.smallplayer.module.dto.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.service.PlayMusicService;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * 檢查Null
     * @return true:OK false:不OK
     */
    public static boolean isObjOK(Object object) {
        return object != null;
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

    public static void logList(String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            Log.d(TAG, "logList: " + strings[i]);
        }
    }

    /**
     * @param items
     * @return 串起來的video id，為了搜尋歌曲的播放時間
     */
    public static StringBuilder getVideoIdsForQueryDuration(List<? extends HasVideoId> items) {
        StringBuilder sVideoIds = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sVideoIds.append(items.get(i).getVideoId());
            if (i < items.size() - 1) {
                sVideoIds.append(",");
            }
        }
        return sVideoIds;
    }


    /**
     * 設定格式化後的歌曲播放時間
     * @param u2BVideoDurationDTO
     * @param items
     */
    public static void processDuration(U2BVideoDurationDTO u2BVideoDurationDTO, List<? extends HasVideoId> items) {
        U2BVideoDurationDTO.ItemsBean itemDuration;
        HashMap<String, HasVideoId> hashMap2 = new HashMap<>();
        for (HasVideoId item : items) {
            if (item.getVideoDuration() == -1) {
                //沒Duration的才放
                hashMap2.put(item.getVideoId(), item);
            }
        }

        HasVideoId itemVideo2;
        for (int i = 0; i < u2BVideoDurationDTO.getItems().size(); i++) {
            itemDuration = u2BVideoDurationDTO.getItems().get(i);
            itemVideo2 = hashMap2.get(itemDuration.getId());
            if (itemVideo2 != null) {
                itemVideo2.setVideoDuration(U2BApiUtil.formateU2BDurationToMilionSecond(itemDuration.getContentDetails().getDuration()));
            }
        }
    }
}
