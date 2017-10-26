package com.tonynowater.smallplayer.module.u2b.util;

import com.tonynowater.smallplayer.module.u2b.U2BApi;

import java.util.List;

/**
 * Created by tonynowater on 2017/10/26.
 */

public class U2BPlayListVideoQueryArray<PlayListSongEntity> extends BaseQueryArrayList {

    private String mAuthToken = null;

    public U2BPlayListVideoQueryArray(IOnU2BQuery callback) {
        super(callback);
    }

    @Override
    public void query() {

    }

    public void query(final String keyword, final String authToken) {
        U2BApi.newInstance().queryU2BPlayListVideo(keyword, authToken, new U2BApi.OnNewCallback<com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity>() {
            @Override
            public void onSuccess(List<com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity> response, String nextPageToken) {
                mKeyword = keyword;
                mNextPageToken = nextPageToken;
                mAuthToken = authToken;
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
        U2BApi.newInstance().queryU2BPlayListVideo(mKeyword, mAuthToken, mNextPageToken, new U2BApi.OnNewCallback<com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity>() {
            @Override
            public void onSuccess(List<com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity> response, String nextPageToken) {
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
