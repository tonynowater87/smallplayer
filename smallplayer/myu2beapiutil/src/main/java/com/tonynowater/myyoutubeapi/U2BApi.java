package com.tonynowater.myyoutubeapi;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.util.concurrent.TimeUnit;

/**
 * Created by tonyliao on 2017/4/30.
 */

public class U2BApi {
    private static final String TAG = U2BApi.class.getSimpleName();

    public static final int TIMEOUT = 10;
    public static final int QUERY_MAX_RESULT = 25;

    private static U2BApi mInstance = null;
    private OkHttpClient mOkHttp;

    private U2BApi() {
        mOkHttp = new OkHttpClient();
        mOkHttp.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
        mOkHttp.setWriteTimeout(TIMEOUT, TimeUnit.SECONDS);
        mOkHttp.setConnectTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    public static U2BApi newInstance() {
        if (mInstance == null) {
            mInstance = new U2BApi();
        }
        return mInstance;
    }

    public void queryU2BVideo(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL + "&q=%s&maxResults=%d&regionCode=TW&type=video", keyword, QUERY_MAX_RESULT))
                .build();

        mOkHttp.newCall(request).enqueue(callback);
    }

    public void queryU2BPlayList(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL + "&q=%s&maxResults=%d&regionCode=TW&type=playlist", keyword, QUERY_MAX_RESULT))
                .build();

        mOkHttp.newCall(request).enqueue(callback);
    }

    public void queryU2BChannel(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL + "&q=%s&maxResults=%d&regionCode=TW&type=channel", keyword, QUERY_MAX_RESULT))
                .build();

        mOkHttp.newCall(request).enqueue(callback);
    }
}
