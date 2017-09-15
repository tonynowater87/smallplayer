package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tony10532 on 2017/8/8.
 */
public class SPManager {

    public static final String SHARED_PREFERENCE_COMMON = "SHARED_PREFERENCE_COMMON";

    private static SPManager mInstance = null;

    private SharedPreferences sharedPreferences;

    public static SPManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SPManager(context);
        }
        return mInstance;
    }

    public SPManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_COMMON, Context.MODE_PRIVATE);
    }
}
