package com.tonynowater.smallplayer.module.u2b.util;

import java.util.ArrayList;

/**
 * Created by tonynowater on 2017/10/24.
 */

public abstract class BaseQueryArrayList extends ArrayList {

    public interface IOnU2BQuery {
        void onQuerySuccess();
        void onQueryFail(String errMsg);
    }

    protected U2BQueryParamsItem ueryParamsItem;
    protected IOnU2BQuery callback;

    public BaseQueryArrayList(U2BQueryParamsItem u2BQueryParamsItem, IOnU2BQuery callback) {
        super();
        this.ueryParamsItem = u2BQueryParamsItem;
        this.callback = callback;
    }

    public void setKeyword(String keyword) {
        ueryParamsItem.setKeyword(keyword);
    }

    public String getNextPageToken() {
        return ueryParamsItem.getNextPageToken();
    }

    public abstract void query();
}
