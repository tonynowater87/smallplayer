package com.tonynowater.smallplayer.module.u2b;

import android.util.Log;

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
    private static final String video =  "video";
    private static final String playlist = "playlist";
    private static final String channel = "channel";

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

    /** 搜尋下一頁U2B的影片 */
    public void queryU2BVideo(String keyword, String pageToken, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, video, pageToken))
                .build();

        Log.d(TAG, "queryU2BVideo: " + request.urlString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    /** 搜尋第一頁U2B的影片 */
    public void queryU2BVideo(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, video, ""))
                .build();

        Log.d(TAG, "queryU2BVideo: " + request.urlString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    /** 搜尋下一頁U2B的播放清單 */
    public void queryU2BPlayList(String keyword, String pageToken, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, playlist, pageToken))
                .build();

        Log.d(TAG, "queryU2BPlayList: " + request.urlString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    /** 搜尋第一頁U2B的播放清單 */
    public void queryU2BPlayList(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, playlist, ""))
                .build();

        Log.d(TAG, "queryU2BPlayList: " + request.urlString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    public void queryU2BPlayListVideo(String playlistId, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_QUERY_PLAYLIST_VIDEO_URL, playlistId, QUERY_MAX_RESULT))
                .build();

        Log.d(TAG, "queryU2BPlayListVideo: " + request.urlString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    public void queryU2BChannel(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, channel))
                .build();

        Log.d(TAG, "queryU2BChannel: " + request.urlString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    public void queryU2BSUGGESTION(String keyword, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_SUGGESTION_URL, keyword))
                .build();

        Log.d(TAG, "queryU2BSUGGESTION: " + request.urlString());

        mOkHttp.newCall(request).enqueue(callback);
    }

    public void queryU2BVedioDuration(String videoid, Callback callback) {

        Request request = new Request.Builder()
                .get()
                .url(String.format(U2BApiDefine.U2B_API_QUERY_DURATION_URL, videoid, QUERY_MAX_RESULT))
                .build();

        Log.d(TAG, "queryU2BVedioDuration: " + request.urlString());

        mOkHttp.newCall(request).enqueue(callback);
    }
}
