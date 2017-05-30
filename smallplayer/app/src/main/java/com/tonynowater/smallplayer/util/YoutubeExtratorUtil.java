package com.tonynowater.smallplayer.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.tonynowater.smallplayer.u2b.U2BApiDefine;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

/**
 * Created by tonyliao on 2017/5/1.
 */

public class YoutubeExtratorUtil extends AsyncTask<Void, Void, Void> {

    private static final String TAG = YouTubeExtractor.class.getSimpleName();
    private Context context;
    private String videoId;
    private CallBack callBack;

    public YoutubeExtratorUtil(Context context, String videoId, YoutubeExtratorUtil.CallBack callBack) {
        this.context = context;
        this.videoId = videoId;
        this.callBack = callBack;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        new YouTubeExtractor(context) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles != null) {
                    for (int i = 0, itag = 0; i < ytFiles.size(); i++) {
                        itag = ytFiles.keyAt(i);
                        YtFile ytFile = ytFiles.get(itag);// ytFile represents one file with its url and meta data
                        if (ytFile.getMeta().getHeight() == -1 || ytFile.getMeta().getHeight() >= 360) {// Just add videos in a decent format => height -1 = audio
                            Log.d(TAG, "onExtractionComplete: Get StreamUrl " + ytFile.getUrl());
                            callBack.getU2BUrl(ytFile.getUrl());
                            break;
                        }
                    }
                } else {

                    Log.e(TAG, "onExtractionComplete: Get StreamUrl failed");
                    callBack.getU2BUrl("");
                }
            }
        }.extract(String.format(U2BApiDefine.U2B_EXTRACT_VIDEO_URL,videoId), false, false);

        return null;
    }

    public interface CallBack {
        void getU2BUrl(String url);
    }
}
