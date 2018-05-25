package com.tonynowater.smallplayer.module.u2b;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.api.ApiClient;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.U2BAccessTokenDTO;
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.dto.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.util.U2BQueryParamsItem;
import com.tonynowater.smallplayer.util.FileHelper;
import com.tonynowater.smallplayer.util.Logger;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.SPManager;
import com.tonynowater.smallplayer.util.YoutubeExtractorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tonynowater.smallplayer.module.u2b.U2BApiDefine.DEFAULT_QUERY_RESULT;

/**
 * Created by tonyliao on 2017/4/30.
 */
public class U2BApi {
    private static final String TAG = U2BApi.class.getSimpleName();
    public static final int TIMEOUT = 10;
    private static U2BApi mInstance = null;
    private OkHttpClient mOkHttp;

    public interface OnNewCallback<T> {
        void onSuccess(List<T> response, String nextPageToken);
        void onFailure(String errorMsg);
    }

    public interface OnListResponseCallback<T> {
        void onSuccess(List<T> response);
        void onFailure(String errorMsg);
    }

    public interface OnMsgRequestCallback {
        void onSuccess(String response);
        void onFailure(String errorMsg);
    }

    public interface OnRequestTokenCallback {
        void onSuccess();
        void onFailure();
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

    /** 搜尋U2B的Video */
    public void queryU2BVideo(U2BQueryParamsItem queryParamsItem, final OnNewCallback<PlayListSongEntity> callback) {
        if (queryParamsItem.getNextPageToken() != null) {
            if (queryParamsItem.isNeedAuthToken()) {
                String token = SPManager.Companion.getInstance(MyApplication.getContext()).getAccessToken();
                queryParamsItem.addHeader("Authorization", "Bearer " + token);
            }

            testRetrofit(queryParamsItem);

            sendHttpRequest(queryParamsItem, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        Logger.getInstance().d(TAG, "onResponse: " + res);
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

    private void testRetrofit(U2BQueryParamsItem queryParamsItem) {
        Map<String, String> map = new HashMap<>();
        map.put("q", queryParamsItem.getKeyword());
        map.put("pageToken", queryParamsItem.getNextPageToken());
        ApiClient apiClient = new ApiClient();
        apiClient.getVideo(map);
    }

    /** 搜尋U2B的PlayList */
    public void queryU2BPlayList(U2BQueryParamsItem queryParamsItem, final OnNewCallback<U2BUserPlayListEntity> callback) {
        if (queryParamsItem.getNextPageToken() != null) {
            if (queryParamsItem.isNeedAuthToken()) {
                String token = SPManager.Companion.getInstance(MyApplication.getContext()).getAccessToken();
                queryParamsItem.addHeader("Authorization", "Bearer " + token);
            }

            sendHttpRequest(queryParamsItem, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        Logger.getInstance().d(TAG, "onResponse: " + res);
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

    /**
     * 搜尋播放清單裡的歌曲
     * @param queryParamsItem
     * @param callback
     */
    public void queryU2BPlayListVideo(U2BQueryParamsItem queryParamsItem, final OnNewCallback<PlayListSongEntity> callback) {
        if (queryParamsItem.getNextPageToken() != null) {
            if (queryParamsItem.isNeedAuthToken()) {
                String token = SPManager.Companion.getInstance(MyApplication.getContext()).getAccessToken();
                queryParamsItem.addHeader("Authorization", "Bearer " + token);
            }

            sendHttpRequest(queryParamsItem, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String res = response.body().string();
                                Logger.getInstance().d(TAG, "onResponse: " + res);
                                Gson gson = new Gson();
                                U2bPlayListVideoDTO u2bPlayListVideoDTO = gson.fromJson(res, U2bPlayListVideoDTO.class);
                                List<PlayListSongEntity> listEntities = new ArrayList<>();
                                for (int i = 0; i < u2bPlayListVideoDTO.getItems().size(); i++) {
                                    listEntities.add(new PlayListSongEntity(u2bPlayListVideoDTO.getItems().get(i)));
                                }
                                callback.onSuccess(listEntities, u2bPlayListVideoDTO.getNextPageToken());
                            } else {
                                callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                            }
                        }
                    });
        } else {
            callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
        }
    }

    /**
     * 搜尋的關鍵字查詢
     * @param keyword
     * @param callback
     */
    public void queryU2BSuggestion(String keyword, OnListResponseCallback<String> callback) {
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_SUGGESTION_URL, keyword), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onFailure(e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String sResponse = new String(response.body().bytes());
                    List<String> mSuggestions = U2BApiUtil.getSuggestionStringList(sResponse);
                    if (MiscellaneousUtil.isListOK(mSuggestions)) {
                        callback.onSuccess(mSuggestions);
                    } else {
                        callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                    }
                } else {
                    callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                }
            }
        });
    }

    /**
     * 查影片的播放時間
     * @param listSongEntities
     * @param callback
     */
    public void queryU2BVedioDuration(final List<PlayListSongEntity> listSongEntities, final OnListResponseCallback<PlayListSongEntity> callback) {
        sendHttpRequest(String.format(U2BApiDefine.U2B_API_QUERY_DURATION_URL
                , MiscellaneousUtil.getVideoIdsForQueryDuration(listSongEntities)
                , DEFAULT_QUERY_RESULT)
                , new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String sResponse = response.body().string();
                            U2BVideoDurationDTO u2BVideoDurationDTO = new Gson().fromJson(sResponse, U2BVideoDurationDTO.class);
                            MiscellaneousUtil.processDuration(u2BVideoDurationDTO, listSongEntities);
                            callback.onSuccess(listSongEntities);
                        } else {
                            callback.onFailure(MyApplication.getContext().getString(R.string.u2b_query_failure));
                        }
                    }
                });
    }

    /**
     * 透過{@link YoutubeExtractorUtil}做到下載的功能
     * @param playable
     * @param callback
     */
    public void downloadMP3FromU2B(Playable playable, final OnMsgRequestCallback callback) {
        final PlayListSongEntity playListSongEntity = playable.getPlayListSongEntity();
        if (TextUtils.equals(MetaDataCustomKeyDefine.ISNOTLOCAL,playListSongEntity.getIsLocal())) {
            YoutubeExtractorUtil youtubeExtractorUtil = new YoutubeExtractorUtil(MyApplication.getContext(), new YoutubeExtractorUtil.CallBack() {
                @Override
                public void onSuccess(String url) {
                    sendHttpRequest(url, new Callback(){
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Logger.getInstance().d("downloadMP3FromU2B Failure");
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (response.isSuccessful()) {
                                new FileHelper(playListSongEntity, response, new FileHelper.OnFileHelperCallback() {
                                    @Override
                                    public void onSuccess(String msg) {
                                        callback.onSuccess(msg);
                                    }

                                    @Override
                                    public void onFailure() {
                                        callback.onFailure(String.format(MyApplication.getMyString(R.string.downloadMP3_error_msg), playListSongEntity.getTitle()));
                                    }
                                }).execute();
                            }
                        }
                    });
                }

                @Override
                public void onFailed() {
                    callback.onFailure(MyApplication.getMyString(R.string.downloadMP3_banned_msg));
                }
            });
            youtubeExtractorUtil.extract(String.format(U2BApiDefine.U2B_EXTRACT_VIDEO_URL, playListSongEntity.getMediaMetadata().getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE)), false, false);
        }
    }

    /**
     * 查使用者Youtube的歌單
     * @param queryParamsItem
     * @param callback
     */
    public void queryUserPlaylist (U2BQueryParamsItem queryParamsItem, final OnNewCallback<U2BUserPlayListEntity> callback) {

        String token = SPManager.Companion.getInstance(MyApplication.getContext()).getAccessToken();
        queryParamsItem.addHeader("Authorization", "Bearer " + token);

        sendHttpRequest(queryParamsItem, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "onFailure:" + e.toString());
                callback.onFailure(MyApplication.getContext().getString(R.string.query_user_playlist_error));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    Logger.getInstance().d(TAG, "onResponse: " + res);
                    Gson gson = new Gson();
                    U2BUserPlayListDTO userPlayListDTO = gson.fromJson(res, U2BUserPlayListDTO.class);
                    List<U2BUserPlayListEntity> listEntities = new ArrayList<>();
                    U2BUserPlayListEntity u2BUserPlayListEntity;
                    for (int i = 0; i < userPlayListDTO.getItems().size(); i++) {
                        u2BUserPlayListEntity = new U2BUserPlayListEntity(userPlayListDTO.getItems().get(i));
                        u2BUserPlayListEntity.setNeedAuthToken(true);
                        listEntities.add(u2BUserPlayListEntity);
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

        Logger.getInstance().d(TAG, "sendHttpRequest:" + request.toString());
        mOkHttp.newCall(request).enqueue(callback);
    }

    /**
     * 送HTTP GET 加 header
     * @param queryParamsItem
     * @param callback
     * @return
     */
    private void sendHttpRequest(U2BQueryParamsItem queryParamsItem, Callback callback) {
        Request request = new Request.Builder()
                .get()
                .headers(queryParamsItem.getHeader())
                .url(queryParamsItem.getUrl())
                .build();

        Logger.getInstance().d(TAG, "sendHttpRequest:" + request.toString());
        mOkHttp.newCall(request).enqueue(callback);
    }


    /**
     * 取Access Token
     * @param authCode
     * @param callback
     */
    public void getYoutubeToken(String authCode, final OnRequestTokenCallback callback) {
        String AUTH_SERVER_TOKEN = "https://www.googleapis.com/oauth2/v4/token";
        RequestBody requestBody = new FormEncodingBuilder()
                .add("grant_type", "authorization_code")
                .add("client_id", MyApplication.getContext().getString(R.string.default_web_client_id))
                .add("redirect_uri", "https://smallplayer-166212.firebaseapp.com/__/auth/handler")
                .add("client_secret", "xvQXNCJUcDrCv72RC0tn9N4u")
                .add("code", authCode)
                .build();

        final Request request = new Request.Builder()
                .post(requestBody)
                .url(AUTH_SERVER_TOKEN)
                .build();

        Logger.getInstance().d(TAG, "getYoutubeToken: " + request.toString());
        mOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    Logger.getInstance().d(TAG, "onResponse: " + body);
                    U2BAccessTokenDTO dto = new Gson().fromJson(body, U2BAccessTokenDTO.class);
                    SPManager.Companion.getInstance(MyApplication.getContext()).setAccessToken(dto.access_token);
                    SPManager.Companion.getInstance(MyApplication.getContext()).setRefreshToken(dto.refresh_token);
                    SPManager.Companion.getInstance(MyApplication.getContext()).setTokenExpireTime(System.currentTimeMillis() + (dto.expires_in * 1000L));
                    SPManager.Companion.getInstance(MyApplication.getContext()).setGoogleLogin(true);
                    callback.onSuccess();
                } else {
                    SPManager.Companion.getInstance(MyApplication.getContext()).setGoogleLogin(false);
                    callback.onFailure();
                }
            }
        });
    }

    /**
     * 更新AccessToken
     * @param refresh_token
     * @param callback
     */
    public void refreshYoutubeToken(String refresh_token, final OnRequestTokenCallback callback) {
        String AUTH_SERVER_TOKEN = "https://www.googleapis.com/oauth2/v4/token";
        RequestBody requestBody = new FormEncodingBuilder()
                .add("grant_type", "refresh_token")
                .add("client_id", MyApplication.getContext().getString(R.string.default_web_client_id))
                .add("client_secret", "xvQXNCJUcDrCv72RC0tn9N4u")
                .add("refresh_token", refresh_token)
                .build();

        final Request request = new Request.Builder()
                .post(requestBody)
                .url(AUTH_SERVER_TOKEN)
                .build();

        Logger.getInstance().d(TAG, "refreshYoutubeToken: " + request.toString());
        mOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callback.onFailure();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    Logger.getInstance().d(TAG, "onResponse: " + body);
                    U2BAccessTokenDTO dto = new Gson().fromJson(body, U2BAccessTokenDTO.class);
                    SPManager.Companion.getInstance(MyApplication.getContext()).setAccessToken(dto.access_token);
                    SPManager.Companion.getInstance(MyApplication.getContext()).setTokenExpireTime(System.currentTimeMillis() + (dto.expires_in * 1000L));
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        });
    }
}
