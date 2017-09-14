package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tonyliao on 2017/4/30.
 */

public class InternetUtil {
    private static final String TAG = InternetUtil.class.getSimpleName();

    private InternetUtil() {}

    public boolean isDeviceOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
