package com.tonynowater.smallplayer.util;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tonyliao on 2017/5/1.
 */
public class ToastUtil {
    private static final String TAG = ToastUtil.class.getSimpleName();

    private static Toast mToast = null;

    @UiThread
    public static void showToast(final Activity activity, final String toastMsg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast = Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT);
                mToast.show();
                Log.d(TAG, "showToast : " +toastMsg);
            }
        });
    }

    public static void cancelToast() {
        if (mToast == null) {
            Log.e(TAG, "cancelToast null ");
            return;
        }
        mToast.cancel();
        Log.d(TAG, "cancelToast");
    }
}
