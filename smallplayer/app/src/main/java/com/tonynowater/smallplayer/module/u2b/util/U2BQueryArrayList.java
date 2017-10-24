package com.tonynowater.smallplayer.module.u2b.util;

import com.tonynowater.smallplayer.module.u2b.U2BApi;

import java.util.List;

/**
 * Created by tonynowater on 2017/10/23.
 */

public class U2BQueryArrayList<U2BUserPlayListEntity> extends BaseQueryArrayList {

    private String mNextPageToken;
    private String mUserToken;

    public U2BQueryArrayList(String mUserToken, IOnU2BQuery callback) {
        super(callback);
        this.mUserToken = mUserToken;
        mNextPageToken = "";
    }

    @Override
    public void query() {
        U2BApi.newInstance().queryUserPlaylist(mUserToken, new U2BApi.OnNewCallback<com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity>() {
            @Override
            public void onSuccess(List<com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity> response, String nextPageToken) {
                mNextPageToken = nextPageToken;
                for (int i = 0; i < response.size(); i++) {
                    add(response.get(i));
                }
                callback.onQuerySuccess();
            }

            @Override
            public void onFailure(String errorMsg) {
                callback.onQueryFail(errorMsg);
            }
        });
    }
}
