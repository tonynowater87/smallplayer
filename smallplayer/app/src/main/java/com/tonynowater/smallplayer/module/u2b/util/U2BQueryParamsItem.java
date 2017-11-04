package com.tonynowater.smallplayer.module.u2b.util;

import com.squareup.okhttp.Headers;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.module.u2b.U2BApiDefine;

/**
 * Created by tonynowater on 2017/10/26.
 */

public class U2BQueryParamsItem {
    private int queryCount = U2BApiDefine.DEFAULT_QUERY_RESULT;
    private EnumU2BSearchType enumU2BSearchType;
    private Headers.Builder headerBuilder;
    private String keyword = "";
    private String nextPageToken = "";
    private boolean isNeedAuthToken = false;

    public U2BQueryParamsItem(EnumU2BSearchType enumU2BSearchType, String keyword, boolean isNeedAuthToken) {
        this.enumU2BSearchType = enumU2BSearchType;
        this.keyword = keyword;
        this.isNeedAuthToken = isNeedAuthToken;
        headerBuilder = new Headers.Builder();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public boolean isNeedAuthToken() {
        return isNeedAuthToken;
    }

    public void setNeedAuthToken(boolean needAuthToken) {
        isNeedAuthToken = needAuthToken;
    }

    public void addHeader(String headerName, String headerValue) {
        headerBuilder.add(headerName, headerValue);
    }

    public Headers getHeader() {
        return headerBuilder.build();
    }

    public String getUrl() {
        switch (enumU2BSearchType) {
            case VIDEO:
            case PLAYLISTVIDEO:
            case PLAYLIST:
                return String.format(enumU2BSearchType.getUrl(), keyword, queryCount, nextPageToken);
            case USER_LIST:
            case USER_CHANNELS:
                return enumU2BSearchType.getUrl();
        }

        return "";
    }
}
