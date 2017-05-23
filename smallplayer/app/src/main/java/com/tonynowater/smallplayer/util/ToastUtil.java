package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.widget.Toast;

import com.tonynowater.smallplayer.MyApplication;

/**
 * Created by tonyliao on 2017/5/1.
 */

public class ToastUtil {
    private static ToastUtil mInstance = null;
    private Toast mToast = null;

    private ToastUtil() {
        mToast = Toast.makeText(MyApplication.getContext(), "", Toast.LENGTH_SHORT);
    }

    public static ToastUtil newInstance() {
        if (mInstance == null) {
            mInstance = new ToastUtil();
        }

        return mInstance;
    }

    public void showToast(String toastMsg) {
        mToast.cancel();
        mToast.setText(toastMsg);
        mToast.show();
    }

    public void cancelToast() {
        mToast.cancel();
    }
}
