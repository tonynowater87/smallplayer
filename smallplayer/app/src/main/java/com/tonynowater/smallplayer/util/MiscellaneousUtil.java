package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/27.
 */

public class MiscellaneousUtil {
    private static final String TAG = MiscellaneousUtil.class.getSimpleName();

    private MiscellaneousUtil() {}

    /** 檢查List */
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
}
