package com.tonynowater.smallplayer.util;

import android.util.Log;

import java.util.List;

/**
 * Created by tonyliao on 2017/4/29.
 */

public class Logger<T> {

    private static final String TAG = Logger.class.getSimpleName();

    public Logger() {

    }

    public void log(T t) {
        Log.d(TAG, "log:"+t.toString());
    }

    public void log(List<T> t) {

        for (int i = 0; i < t.size(); i++) {
            Log.d(TAG, "log:"+ t.get(i).toString());
        }
    }
}
