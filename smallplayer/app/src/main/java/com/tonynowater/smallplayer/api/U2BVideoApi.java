package com.tonynowater.smallplayer.api;

import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by tonynowater on 2017/11/16.
 */

public interface U2BVideoApi {

//    "https://content.googleapis.com/youtube/v3/search?part=snippet&key=" + API_KEY + "&q=%s&maxResults=%d&regionCode=TW&type=video&pageToken=%s";
    @GET("search")
    Call<U2BVideoDTO> getVideos(@QueryMap Map<String, String> map, @Query("maxResults") int count);
}
