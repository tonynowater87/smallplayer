package com.tonynowtaer87.myutil;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by tonyliao on 2017/5/1.
 */

public class ToastUtil {
    private static ToastUtil mInstance = null;
    private Toast mToast = null;

    private ToastUtil(Context context) {
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public static ToastUtil newInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ToastUtil(context);
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
