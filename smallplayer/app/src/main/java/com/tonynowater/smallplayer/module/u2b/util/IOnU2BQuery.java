package com.tonynowater.smallplayer.module.u2b.util;

/**
 * Created by tonynowater on 2017/10/23.
 */

public interface IOnU2BQuery {
    void onQuerySuccess();
    void onQueryFail(String errMsg);
}
