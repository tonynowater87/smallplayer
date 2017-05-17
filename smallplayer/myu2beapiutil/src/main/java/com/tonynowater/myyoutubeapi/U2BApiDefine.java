package com.tonynowater.myyoutubeapi;

/**
 * Created by tonyliao on 2017/4/30.
 */

public class U2BApiDefine {
    private static final String API_KEY = "AIzaSyBTrvGPjVOU4iI2-Jg6gZ2RdeegWp3yDo0";

    public static final String U2B_API_URL = "https://content.googleapis.com/youtube/v3/search?part=snippet&key=" + API_KEY + "&q=%s&maxResults=%d&regionCode=TW&type=%s";
    public static final String U2B_API_QUERY_DURATION_URL = "https://www.googleapis.com/youtube/v3/videos?id=%s&part=contentDetails&key=" + API_KEY + "&maxResults=%d";
    public static final String U2B_EXTRACT_VIDEO_URL = "http://youtube.com/watch?v=%s";
    public static final String U2B_API_SUGGESTION_URL = "http://suggestqueries.google.com/complete/search?q=%s&client=firefox&ds=yt&hl=zh";
}
