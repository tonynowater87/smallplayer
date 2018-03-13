package com.tonynowater.smallplayer.api;

import android.util.Log;

import com.tonynowater.smallplayer.BuildConfig;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.util.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tonynowater on 2017/11/16.
 */

public class ApiClient {

    private static final String BASE_URL = "https://content.googleapis.com/youtube/v3/";
    public static final int DEFAULT_QUERY_RESULT = 25;//預設每次查詢的比數(1-50)
    private static final String TAG = "ApiClient";

    private final U2BVideoApi videoApi;

    public ApiClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Logger.getInstance().d(TAG, "ApiClient: " + request.url().toString());
                        HttpUrl httpUrl = request.url().newBuilder()
                                .addQueryParameter("key", BuildConfig.YoutubeDataAPIKey)
                                .addQueryParameter("part", "snippet")
                                .build();
                        request = request.newBuilder().url(httpUrl).build();
                        Logger.getInstance().d(TAG, "ApiClient: " + httpUrl.toString());
                        return chain.proceed(request);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        videoApi = retrofit.create(U2BVideoApi.class);
    }

    public void getVideo(Map<String, String> map) {
        Call<U2BVideoDTO> response = videoApi.getVideos(map, DEFAULT_QUERY_RESULT);
        response.enqueue(new Callback<U2BVideoDTO>() {
            @Override
            public void onResponse(Call<U2BVideoDTO> call, Response<U2BVideoDTO> response) {

                List<U2BVideoDTO.ItemsBean> videos = response.body().getItems();

                for (int i = 0, len = videos.size(); i < len; i++) {
                    Logger.getInstance().d(TAG, "onResponse: " + videos.get(i).getSnippet().getTitle());
                }
            }

            @Override
            public void onFailure(Call<U2BVideoDTO> call, Throwable throwable) {
                Logger.getInstance().d(TAG, "onResponse: " + throwable.toString());
            }
        });
    }
}
