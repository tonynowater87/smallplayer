package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.tonynowater.smallplayer.module.dto.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.service.EnumPlayMode;
import com.tonynowater.smallplayer.service.PlayMusicService;

import java.io.Serializable;
import java.lang.reflect.Field;
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
    public static String getVideoIdsForQueryDuration(List<PlayListSongEntity> items) {
        StringBuilder sVideoIds = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sVideoIds.append(items.get(i).getSource());
            if (i < items.size() - 1) {
                sVideoIds.append(",");
            }
        }
        return sVideoIds.toString();
    }


    /**
     * 設定格式化後的歌曲播放時間
     * @param u2BVideoDurationDTO
     * @param items
     */
    public static void processDuration(U2BVideoDurationDTO u2BVideoDurationDTO, List<PlayListSongEntity> items) {
        U2BVideoDurationDTO.ItemsBean itemDuration;
        HashMap<String, PlayListSongEntity> hashMap2 = new HashMap<>();
        for (PlayListSongEntity item : items) {
            if (item.getDuration() == -1) {
                //沒Duration的才放
                hashMap2.put(item.getSource(), item);
            }
        }

        PlayListSongEntity itemVideo2;
        for (int i = 0; i < u2BVideoDurationDTO.getItems().size(); i++) {
            itemDuration = u2BVideoDurationDTO.getItems().get(i);
            itemVideo2 = hashMap2.get(itemDuration.getId());
            if (itemVideo2 != null) {
                itemVideo2.setDuration(U2BApiUtil.formateU2BDurationToMilionSecond(itemDuration.getContentDetails().getDuration()));
            }
        }
    }

    /** 設定ToolBar文字為跑馬燈 */
    public static void setToolBarMarquee(Toolbar toolbar) {
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView titleTextView = (TextView) f.get(toolbar);
            titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            titleTextView.setFocusable(true);
            titleTextView.setFocusableInTouchMode(true);
            titleTextView.requestFocus();
            titleTextView.setSingleLine(true);
            titleTextView.setSelected(true);
            titleTextView.setMarqueeRepeatLimit(-1);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 從PlaybackStateCompat的extra裡取得EnumPlayMode
     * @param bundle
     * @return
     */
    public static EnumPlayMode getPlayModeFromBundle(Bundle bundle) {
        return (EnumPlayMode) bundle.getSerializable(PlayMusicService.BUNDLE_KEY_PLAYMODE);
    }


    public static Serializable getNextMode(EnumPlayMode playMode) {
        if (playMode == EnumPlayMode.NORMAL)
            return EnumPlayMode.RANDOM_NO_SAME;
        else
            return EnumPlayMode.NORMAL;
    }
}
