package com.tonynowater.smallplayer.module.u2b;

import com.tonynowater.smallplayer.BuildConfig;

/**
 * Created by tonyliao on 2017/4/30.
 */

public class U2BApiDefine {
    public static final int DEFAULT_QUERY_RESULT = 25;//預設每次查詢的比數(1-50)
    private static final String API_KEY = BuildConfig.YoutubeDataAPIKey;
    //查影片URL
    public static final String U2B_API_VIDEO_URL = "https://content.googleapis.com/youtube/v3/search?part=snippet&key=" + API_KEY + "&q=%s&maxResults=%d&regionCode=TW&type=video&pageToken=%s";
    //查播放清單URL
    public static final String U2B_API_PLAYLIST_URL = "https://content.googleapis.com/youtube/v3/search?part=snippet&key=" + API_KEY + "&q=%s&maxResults=%d&regionCode=TW&type=playlist&pageToken=%s";
    //查播放清單裡的歌曲的URL
    public static final String U2B_API_QUERY_PLAYLIST_VIDEO_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=%s&maxResults=%s&key=" + API_KEY + "&pageToken=%s";
    //查使用者的播放清單URL
    public static final String U2B_USER_PLAYLIST_QUERY_URL = "https://content.googleapis.com/youtube/v3/playlists?maxResults=" + DEFAULT_QUERY_RESULT + "&mine=true&part=snippet&contentDetails&key=" + API_KEY;
    //查使用者的Channels URL
    public static final String U2B_USER_CHANNELS_QUERY_URL = "https://content.googleapis.com/youtube/v3/channels?maxResults=" + DEFAULT_QUERY_RESULT + "&mine=true&part=contentDetails&forUsername=tony10532&key=" + API_KEY;
    //查使用者喜歡的影片URL
    public static final String U2B_API_USER_FAVORITE_VIDEO_URL = "https://content.googleapis.com/youtube/v3/videos?part=snippet&key=" + API_KEY + "&maxResults=" + DEFAULT_QUERY_RESULT + "&regionCode=TW&type=video&pageToken=%s&myRating=like";
    //查使用者訂閱頻道的 URL
    public static final String U2B_USER_SUBSCRIPTIONS_QUERY_URL = "https://content.googleapis.com/youtube/v3/subscriptions?maxResults=" + DEFAULT_QUERY_RESULT + "&mine=true&part=snippet&contentDetails&key=" + API_KEY;
    //查影片的長度URL
    public static final String U2B_API_QUERY_DURATION_URL = "https://www.googleapis.com/youtube/v3/videos?id=%s&part=contentDetails&key=" + API_KEY + "&maxResults=%d";
    //第三方函式庫要解析的 URL
    public static final String U2B_EXTRACT_VIDEO_URL = "http://youtube.com/watch?v=%s";
    //關鍵字查詢的 URL
    public static final String U2B_API_SUGGESTION_URL = "http://suggestqueries.google.com/complete/search?q=%s&client=firefox&ds=yt&hl=zh";
    //檢查VideoID是否有效的URL
    public static final String CHECK_U2B_VIDEO_ID_URL = "https://www.googleapis.com/youtube/v3/videos?part=id&id=%s&key=" + API_KEY;
    //檢查VideoID是否有地區限制的URL
    public static final String CHECK_U2B_REGION_RESTRICT_URL = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=%s&key=" + API_KEY;
    //檢查VideoID的詳細資料
    public static final String U2B_API_VIDEO_DETAIL_INFORMATION_URL = "https://www.googleapis.com/youtube/v3/videos?id=%s&key=" + API_KEY;

    public static final String DOWNLOAD_MP3_URL = "http://www.youtubeinmp3.com/fetch/?format=JSON&video=https://www.youtube.com/watch?v=%s";
    public static final String DOWNLOAD_MP3_API_URL = "http://www.youtubeinmp3.com/%s";
}
