package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tonyliao on 2017/5/1.
 */
public class ToastUtil {
    private static final String TAG = ToastUtil.class.getSimpleName();

    private static Toast mToast = null;

    // FIXME: 2017/6/19 呼叫端需要呼叫runOnMainThread ... java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
    @UiThread
    public static void showToast(Context context, String toastMsg) {
        mToast = Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT);
        mToast.show();
        Log.d(TAG, "showToast : " +toastMsg);
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
