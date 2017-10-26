package com.tonynowater.smallplayer.module.u2b;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.U2BMP3LinkDTO;
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tonyliao on 2017/4/30.
 */
// TODO: 2017/10/16 Youtube AccessToken過期的問題待處理
public class U2BApi {
    private static final String TAG = U2BApi.class.getSimpleName();

    public static final int TIMEOUT = 10;
    public static final int DEFAULT_QUERY_RESULT = 25;//預設每次查詢的比數(1-50)
    public static final int MAX_QUERY_RESULT = 50;
    private static final String video =  "video";
    private static final String playlist = "playlist";
    private static final String channel = "channel";
    private static U2BApi mInstance = null;
    private OkHttpClient mOkHttp;

    public interface OnU2BApiCallback {
        void onSuccess(String response);
        void onFailure(String errorMsg);
    }

    public interface OnNewCallback<T> {
        void onSuccess(List<T> response, String nextPageToken);
        void onFailure(String errorMsg);
    }

    public interface OnMsgRequestCallback {
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
    public void queryU2BVideo(String keyword, String pageToken, final OnNewCallback<PlayListSongEntity> callback) {
        if (pageToken != null) {
            sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, DEFAULT_QUERY_RESULT, video, pageToken), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: " + res);
                        Gson gson = new Gson();
                        U2BVideoDTO u2BVideoDTO = gson.fromJson(res, U2BVideoDTO.class);
                        List<PlayListSongEntity> listEntities = new ArrayList<>();
                        for (int i = 0; i < u2BVideoDTO.getItems().size(); i++) {
                            listEntities.add(new PlayListSongEntity(u2BVideoDTO.getItems().get(i)));
                        }
                        callback.onSuccess(listEntities, u2BVideoDTO.getNextPageToken());
                    } else {
                        callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                    }
                }
            });
        } else {
            callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
        }
    }

    /** 搜尋第一頁U2B的影片 */
    public void queryU2BVideo(String keyword, OnNewCallback<PlayListSongEntity> callback) {
        queryU2BVideo(keyword, "", callback);
    }

    /** 搜尋下一頁U2B的播放清單 */
    public void queryU2BPlayList(String keyword, String pageToken, final OnNewCallback<U2BUserPlayListEntity> callback) {
        if (pageToken != null) {
            sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, DEFAULT_QUERY_RESULT, playlist, pageToken), new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        Log.d(TAG, "onResponse: " + res);
                        Gson gson = new Gson();
                        U2BPlayListDTO userPlayListDTO = gson.fromJson(res, U2BPlayListDTO.class);
                        List<U2BUserPlayListEntity> listEntities = new ArrayList<>();
                        for (int i = 0; i < userPlayListDTO.items.size(); i++) {
                            listEntities.add(new U2BUserPlayListEntity(userPlayListDTO.items.get(i)));
                        }
                        callback.onSuccess(listEntities, userPlayListDTO.nextPageToken);
                    } else {
                        callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                    }
                }
            });
        } else {
            callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
        }
    }

    /** 搜尋第一頁U2B的播放清單 */
    public void queryU2BPlayList(String keyword, final OnNewCallback<U2BUserPlayListEntity> callback) {
        queryU2BPlayList(keyword, "", callback);
    }

    /**
     * 搜尋播放清單裡第一頁的歌曲
     * @param playlistId
     * @param authToken
     * @param callback
     */
    public void queryU2BPlayListVideo(String playlistId, String authToken, Callback callback) {
        Headers headers = new Headers.Builder().add("Authorization", "Bearer " + authToken).build();
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_PLAYLIST_VIDEO_URL, playlistId, DEFAULT_QUERY_RESULT, "")
                , headers
                , callback);
    }

    /**
     * 搜尋播放清單裡下一頁的歌曲
     * @param playlistId
     * @param callback
     * @param authToken
     * @param pageToken
     */
    public void queryU2BPlayListVideo(String playlistId, String authToken, String pageToken, Callback callback) {
        if (pageToken == null) {
            return;
        }
        Headers headers = new Headers.Builder().add("Authorization", "Bearer " + authToken).build();
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_PLAYLIST_VIDEO_URL, playlistId, DEFAULT_QUERY_RESULT, pageToken), callback);
    }

    public void queryU2BChannel(String keyword, Callback callback) {
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_URL, keyword, DEFAULT_QUERY_RESULT, channel), callback);
    }

    /**
     * 搜尋的關鍵字查詢
     * @param keyword
     * @param callback
     */
    public void queryU2BSUGGESTION(String keyword, Callback callback) {
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_SUGGESTION_URL, keyword), callback);
    }

    /**
     * 查影片的播放時間
     * @param videoid
     * @param callback
     */
    public void queryU2BVedioDuration(String videoid, Callback callback) {
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_DURATION_URL, videoid, DEFAULT_QUERY_RESULT), callback);
    }

    /**
     * 透過http://www.youtubeinmp3.com/做到下載的功能
     * @param playable
     * @param callback
     */
    public void downloadMP3FromU2B(Playable playable, final OnMsgRequestCallback callback) {
        final PlayListSongEntity playListSongEntity = playable.getPlayListSongEntity();
        if (TextUtils.equals(MetaDataCustomKeyDefine.ISNOTLOCAL,playListSongEntity.getIsLocal())) {
            sendHttpRequest(String.format(U2BApiDefine.DOWNLOAD_MP3_URL, playListSongEntity.getSource()), new Callback() {
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
                            Log.e(TAG, "onResponse : JsonSyntaxException " + e.toString());
                            callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                            return;
                        }
                        sendHttpRequest(u2BMP3LinkDTO.getLink(), new Callback() {
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
                                        //若是回html，還需要去解析mp3的下載位置
                                        ParserMp3Helper.parserMp3UrlFromHtml(response.body().string(), playListSongEntity, callback);
                                    }

                                } else {
                                    callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                                }
                            }
                        });
                    } else {
                        callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                    }
                }
            });
        }
    }

    /**
     * 若第一次下載回覆為html，這裡做第二次下載的動作
     * @param url
     * @param playListSongEntity
     * @param callback
     */
    public void downloadMP3FromU2B(String url, final PlayListSongEntity playListSongEntity, final OnMsgRequestCallback callback) {
        url = String.format(U2BApiDefine.DOWNLOAD_MP3_API_URL, url);
        sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "onResponse failure: ");
                callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse success: ");
                    if (response.header("Content-Type").contains("audio")) {
                        Log.d(TAG, "onResponse audio : ");
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
                    } else if (response.header("Content-Type").contains("html")){
                        Log.d(TAG, "onResponse html : ");
                        callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity));
                    }
                } else {
                    Log.d(TAG, "onResponse fail: ");
                    callback.onFailure(String.format(MyApplication.getContext().getString(R.string.downloadMP3_error_msg), playListSongEntity));
                }
            }
        });
    }

    /**
     * 查使用者Youtube的歌單
     * @param token
     * @param callback
     */
    public void queryUserPlaylist (String token, final OnNewCallback<U2BUserPlayListEntity> callback) {
        Headers headers = new Headers.Builder().set("Authorization", "Bearer " + token).build();
        sendHttpRequest(U2BApiDefine.U2B_USER_PLAYLIST_QUERY_URL, headers, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "onFailure:" + e.toString());
                callback.onFailure(MyApplication.getContext().getString(R.string.query_user_playlist_error));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    Log.d(TAG, "onResponse: " + res);
                    Gson gson = new Gson();
                    U2BUserPlayListDTO userPlayListDTO = gson.fromJson(res, U2BUserPlayListDTO.class);
                    List<U2BUserPlayListEntity> listEntities = new ArrayList<>();
                    for (int i = 0; i < userPlayListDTO.getItems().size(); i++) {
                        listEntities.add(new U2BUserPlayListEntity(userPlayListDTO.getItems().get(i)));
                    }
                    callback.onSuccess(listEntities, userPlayListDTO.getNextPageToken());
                } else {
                    Log.e(TAG, "onFailure:" + response.message());
                    callback.onFailure(MyApplication.getContext().getString(R.string.query_user_playlist_error));
                }
            }
        });
    }

    /**
     * 送HTTP GET
     * @param url
     * @param callback
     * @return
     */
    private void sendHttpRequest(String url, Callback callback) {
        Headers headers = new Headers.Builder().build();
        sendHttpRequest(url, headers, callback);
    }

    /**
     * 送HTTP GET 加 header
     * @param url
     * @param headers
     * @param callback
     * @return
     */
    private void sendHttpRequest(String url, Headers headers, Callback callback) {
        Request request = new Request.Builder()
                .get()
                .headers(headers)
                .url(url)
                .build();

        Log.d(TAG, "sendHttpRequest:" + request.toString());
        mOkHttp.newCall(request).enqueue(callback);
    }
}


//另一種方式取Access Token的方式
//    public void getYoutubeToken(String authCode, Callback callback) {
//        String AUTH_SERVER_TOKEN = "https://www.googleapis.com/oauth2/v4/token";
//        RequestBody requestBody = new FormEncodingBuilder()
//                .add("grant_type", "authorization_code")
//                .add("client_id", MyApplication.getContext().getString(R.string.default_web_client_id))
//                .add("redirect_uri", "https://smallplayer-166212.firebaseapp.com/__/auth/handler")
//                .add("client_secret", "xvQXNCJUcDrCv72RC0tn9N4u")
//                .add("code", authCode)
//                .build();
//
//        Request request = new Request.Builder()
//                .post(requestBody)
//                .url(AUTH_SERVER_TOKEN)
//                .build();
//
//        Log.d(TAG, "getYoutubeToken: " + request.toString());
//        mOkHttp.newCall(request).enqueue(callback);
//    }
