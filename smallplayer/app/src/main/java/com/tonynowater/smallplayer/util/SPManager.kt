package com.tonynowater.smallplayer.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by tony10532 on 2017/8/8.
 */
class SPManager(context: Context) {

    private val sharedPreferences: SharedPreferences

    var isGoogleLogin: Boolean
        get() = sharedPreferences.getBoolean(SP_KEY_LOGIN, false)
        set(login) {
            sharedPreferences.edit().putBoolean(SP_KEY_LOGIN, login).commit()
        }

    var accessToken: String
        get() = sharedPreferences.getString(SP_KEY_ACCESS_TOKEN, "")
        set(token) {
            sharedPreferences.edit().putString(SP_KEY_ACCESS_TOKEN, token).commit()
        }

    var refreshToken: String
        get() = sharedPreferences.getString(SP_KEY_REFRESH_TOKEN, "")
        set(token) {
            sharedPreferences.edit().putString(SP_KEY_REFRESH_TOKEN, token).commit()
        }

    var tokenExpireTime: Long
        get() = sharedPreferences.getLong(SP_KEY_EXPIRES_IN, -1)
        set(expires_in) {
            sharedPreferences.edit().putLong(SP_KEY_EXPIRES_IN, expires_in).commit()
        }

    var currentPlayAlbumId: Long
    get() = sharedPreferences.getLong(SP_KEY_CURRENT_PLAYING_ALBUM_ID, 1)
    set(albumId) {
        sharedPreferences.edit().putLong(SP_KEY_CURRENT_PLAYING_ALBUM_ID, albumId)
    }

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_COMMON, Context.MODE_PRIVATE)
    }

    companion object {

        val SHARED_PREFERENCE_COMMON = "SHARED_PREFERENCE_COMMON"
        private val SP_KEY_LOGIN = "SP_KEY_LOGIN"
        private val SP_KEY_ACCESS_TOKEN = "SP_KEY_ACCESS_TOKEN"
        private val SP_KEY_REFRESH_TOKEN = "SP_KEY_REFRESH_TOKEN"
        private val SP_KEY_EXPIRES_IN = "SP_KEY_EXPIRES_IN"
        private val SP_KEY_CURRENT_PLAYING_ALBUM_ID = "SP_KEY_CURRENT_PLAYING_ALBUM_ID"

        private var mInstance: SPManager? = null

        fun getInstance(context: Context): SPManager {
            if (mInstance == null) {
                mInstance = SPManager(context)
            }
            return mInstance!!
        }
    }
}
