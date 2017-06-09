package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

/**
 * Created by tonyliao on 2017/5/1.
 */
public class YoutubeExtratorUtil extends YouTubeExtractor{

    private static final String TAG = YouTubeExtractor.class.getSimpleName();

    private CallBack callBack;

    public YoutubeExtratorUtil(Context context, CallBack callBack) {
        super(context);
        this.callBack = callBack;
    }

    @Override
    protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
        if (ytFiles != null) {
            for (int i = 0, itag = 0; i < ytFiles.size(); i++) {
                itag = ytFiles.keyAt(i);
                YtFile ytFile = ytFiles.get(itag);// ytFile represents one file with its url and meta data
                if (ytFile.getMeta().getHeight() == -1 || ytFile.getMeta().getHeight() >= 360) {// Just add videos in a decent format => height -1 = audio
                    callBack.getU2BUrl(ytFile.getUrl());
                    break;
                }
            }
        } else {

            Log.e(TAG, "onExtractionComplete: Get StreamUrl failed");
            callBack.getU2BUrl("");
        }
    }

    public interface CallBack {
        void getU2BUrl(String url);
    }
}
