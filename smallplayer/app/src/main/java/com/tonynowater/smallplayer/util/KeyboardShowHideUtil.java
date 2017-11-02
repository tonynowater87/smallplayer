package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.tonynowater.smallplayer.MyApplication;

/**
 * Created by tonynowater on 2017/11/2.
 */

public class KeyboardShowHideUtil {
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
