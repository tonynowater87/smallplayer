package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tony10532 on 2017/8/8.
 */
public class SPManager {

    public static final String SHARED_PREFERENCE_COMMON = "SHARED_PREFERENCE_COMMON";
    private static final String SP_KEY_LOGIN = "SP_KEY_LOGIN";
    private static final String SP_KEY_ACCESS_TOKEN = "SP_KEY_ACCESS_TOKEN";
    private static final String SP_KEY_REFRESH_TOKEN = "SP_KEY_REFRESH_TOKEN";
    private static final String SP_KEY_EXPIRES_IN = "SP_KEY_EXPIRES_IN";

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

    public void setIsGoogleLogin(boolean login) {
        sharedPreferences.edit().putBoolean(SP_KEY_LOGIN, login).commit();
    }

    public boolean getIsGoogleLogin() {
        return sharedPreferences.getBoolean(SP_KEY_LOGIN, false);
    }

    public void setAccessToken(String token) {
        sharedPreferences.edit().putString(SP_KEY_ACCESS_TOKEN, token).commit();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(SP_KEY_ACCESS_TOKEN, "");
    }

    public void setRefreshToken(String token) {
        sharedPreferences.edit().putString(SP_KEY_REFRESH_TOKEN, token).commit();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(SP_KEY_REFRESH_TOKEN, "");
    }

    public void setTokenExpireTime(long expires_in) {
        sharedPreferences.edit().putLong(SP_KEY_EXPIRES_IN, expires_in).commit();
    }

    public long getTokenExpireTime() {
        return sharedPreferences.getLong(SP_KEY_EXPIRES_IN, -1);
    }
}
