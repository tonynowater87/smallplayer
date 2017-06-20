package com.tonynowater.smallplayer.module.u2b;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.U2BMP3LinkDTO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.FileHelper;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
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

    public interface OnU2BApiCallback {
        void onSuccess(String response);
        void onFailure(String errorMsg);
    }

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
        if (pageToken == null) {
            Log.d(TAG, "queryU2BVideo: pageToken null");
            return;
        }

        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, video, pageToken), callback);
        Log.d(TAG, "queryU2BVideo: " + request.urlString());
    }

    /** 搜尋第一頁U2B的影片 */
    public void queryU2BVideo(String keyword, Callback callback) {
        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, video, ""), callback);
        Log.d(TAG, "queryU2BVideo: " + request.urlString());
    }

    /** 搜尋下一頁U2B的播放清單 */
    public void queryU2BPlayList(String keyword, String pageToken, Callback callback) {
        if (pageToken == null) {
            Log.d(TAG, "queryU2BPlayList: pageToken null");
        }

        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, playlist, pageToken), callback);
        Log.d(TAG, "queryU2BPlayList: " + request.urlString());
    }

    /** 搜尋第一頁U2B的播放清單 */
    public void queryU2BPlayList(String keyword, Callback callback) {
        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, playlist, ""), callback);
        Log.d(TAG, "queryU2BPlayList: " + request.urlString());
    }

    /**
     * 搜尋播放清單裡第一頁的歌曲
     * @param playlistId
     * @param callback
     */
    public void queryU2BPlayListVideo(String playlistId, Callback callback) {
        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_PLAYLIST_VIDEO_URL, playlistId, QUERY_MAX_RESULT, ""), callback);
        Log.d(TAG, "queryU2BPlayListVideo: " + request.urlString());
    }

    /**
     * 搜尋播放清單裡下一頁的歌曲
     * @param playlistId
     * @param callback
     * @param pageToken
     */
    public void queryU2BPlayListVideo(String playlistId, String pageToken, Callback callback) {
        if (pageToken == null) {
            Log.d(TAG, "queryU2BPlayListVideo: pageToken null");
            return;
        }

        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_PLAYLIST_VIDEO_URL, playlistId, QUERY_MAX_RESULT, pageToken), callback);
        Log.d(TAG, "queryU2BPlayListVideo: " + request.urlString());
    }

    public void queryU2BChannel(String keyword, Callback callback) {
        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, QUERY_MAX_RESULT, channel), callback);
        Log.d(TAG, "queryU2BChannel: " + request.urlString());
    }

    /**
     * 搜尋的關鍵字查詢
     * @param keyword
     * @param callback
     */
    public void queryU2BSUGGESTION(String keyword, Callback callback) {
        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_SUGGESTION_URL, keyword), callback);
        Log.d(TAG, "queryU2BSUGGESTION: " + request.urlString());
    }

    /**
     * 查影片的播放時間
     * @param videoid
     * @param callback
     */
    public void queryU2BVedioDuration(String videoid, Callback callback) {
        Request request = sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_DURATION_URL, videoid, QUERY_MAX_RESULT), callback);
        Log.d(TAG, "queryU2BVedioDuration: " + request.urlString());
    }

    /**
     * 透過http://www.youtubeinmp3.com/做到下載的功能
     * @param playable
     * @param callback
     */
    public void downloadMP3FromU2B(Playable playable, final OnU2BApiCallback callback) {
        final PlayListSongEntity playListSongEntity = playable.getPlayListSongEntity();
        if (TextUtils.equals(MetaDataCustomKeyDefine.ISNOTLOCAL,playListSongEntity.getIsLocal())) {
            final Request request = sendHttpRequest(String.format(U2BApiDefine.DOWNLOAD_MP3_URL, playListSongEntity.getSource()), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {

                        Gson gson = new Gson();
                        U2BMP3LinkDTO u2BMP3LinkDTO;
                        try {
                            u2BMP3LinkDTO = gson.fromJson(response.body().string(), U2BMP3LinkDTO.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            // FIXME: 2017/6/19 有些歌曲無法下載，先不處理
                            callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                            return;
                        }
                        Request requestMp3 = sendHttpRequest(u2BMP3LinkDTO.getLink(), new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                callback.onFailure(e.getMessage());
                            }

                            @Override
                            public void onResponse(final Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    if (response.header("Content-Type").contains("audio")) {
                                        Log.d(TAG, "onResponse: audio");
                                        new FileHelper(playListSongEntity, response, new FileHelper.OnFileHelperCallback() {
                                            @Override
                                            public void onSuccess(String msg) {
                                                callback.onSuccess(msg);
                                            }

                                            @Override
                                            public void onFailure() {
                                                callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                                            }
                                        }).execute();
                                    } else {
                                        Log.d(TAG, "onResponse: html");
                                        // TODO: 2017/6/18 若是回html，還需要去解析mp3的下載位置，先暫時回傳錯誤訊息
                                        XmlPullParser xmlPullParser;
                                        callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                                    }

                                } else {
                                    callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                                }
                            }
                        });
                        Log.d(TAG, "downloadMP3FromU2B phase 2: " + requestMp3.urlString());
                    } else {
                        callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                    }
                }
            });
            Log.d(TAG, "downloadMP3FromU2B phase 1: " + request.urlString());
        }
    }

    /**
     * 送HTTP POST
     * @param url
     * @param callback
     * @return
     */
    private Request sendHttpRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        mOkHttp.newCall(request).enqueue(callback);
        return request;
    }
}
