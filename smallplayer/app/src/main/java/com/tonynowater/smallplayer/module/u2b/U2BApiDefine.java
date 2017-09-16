package com.tonynowater.smallplayer.module.u2b;

import com.tonynowater.smallplayer.BuildConfig;

/**
 * Created by tonyliao on 2017/4/30.
 */

public class U2BApiDefine {
    private static final String API_KEY = BuildConfig.YoutubeDataAPIKey;
    public static final String U2B_API_URL = "https://content.googleapis.com/youtube/v3/search?part=snippet&key=" + API_KEY + "&q=%s&maxResults=%d&regionCode=TW&type=%s&pageToken=%s";
    public static final String U2B_API_QUERY_DURATION_URL = "https://www.googleapis.com/youtube/v3/videos?id=%s&part=contentDetails&key=" + API_KEY + "&maxResults=%d";
    public static final String U2B_API_QUERY_PLAYLIST_VIDEO_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=%s&maxResults=%s&key=" + API_KEY + "&pageToken=%s";
    public static final String U2B_EXTRACT_VIDEO_URL = "http://youtube.com/watch?v=%s";
    public static final String U2B_API_SUGGESTION_URL = "http://suggestqueries.google.com/complete/search?q=%s&client=firefox&ds=yt&hl=zh";
    public static final String DOWNLOAD_MP3_URL = "http://www.youtubeinmp3.com/fetch/?format=JSON&video=https://www.youtube.com/watch?v=%s";
    public static final String DOWNLOAD_MP3_API_URL = "http://www.youtubeinmp3.com/%s";
    public static final String U2B_USER_PLAYLIST_QUERY_URL = "https://content.googleapis.com/youtube/v3/playlists?maxResults=50&mine=true&part=snippet&contentDetails&key=" + API_KEY;
}
