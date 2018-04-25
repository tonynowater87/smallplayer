package com.tonynowater.smallplayer.util

import android.content.Context
import android.net.ConnectivityManager
import com.tonynowater.smallplayer.MyApplication

/**
 * Created by tonyliao on 2018/2/13.
 */
object SNetworkInfo {
    /**
     * @return 判斷網路是否可用
     */
    fun isNetworkAvailable():Boolean {
        val connectService = MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectService.activeNetworkInfo != null && connectService.activeNetworkInfo.isAvailable
    }
}