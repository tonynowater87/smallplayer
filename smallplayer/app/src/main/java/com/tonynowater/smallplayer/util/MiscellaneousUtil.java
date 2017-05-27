package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/27.
 */

public class MiscellaneousUtil {
    private MiscellaneousUtil() {
    }

    /** 檢查List */
    public static boolean isListOK(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static void hideKeyboard(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(token, 0);
    }

}
