package com.tonynowater.smallplayer.module.u2b.util;

import com.tonynowater.smallplayer.module.u2b.U2BApi;

import java.util.List;

/**
 * Created by tonynowater on 2017/10/24.
 */

public class U2BPlayListQueryArray<U2BUserPlayListEntity> extends BaseQueryArrayList {

    public U2BPlayListQueryArray(IOnU2BQuery callback) {
        super(callback);
    }

    @Override
    public void query() {

    }


    public void query(final String keyword) {
        U2BApi.newInstance().queryU2BPlayList(keyword, new U2BApi.OnNewCallback<com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity>() {
            @Override
            public void onSuccess(List<com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity> response, String nextPageToken) {
                mNextPageToken = nextPageToken;
                mKeyword = keyword;
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

    public void queryNextPage() {
        U2BApi.newInstance().queryU2BPlayList(mKeyword, mNextPageToken, new U2BApi.OnNewCallback<com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity>() {
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
