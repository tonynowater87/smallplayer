package com.tonynowater.smallplayer.service;

import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;

/**
 * Created by tonyliao on 2017/5/14.
 */
public interface PlaybackCallback {
// TODO: 2017/5/14 需加入此Callback將Mediaplayer播放的事件傳至Service
    void onCompletion();

    void onPlaybackStateChanged(@Nullable MediaMetadataCompat mediaMetadataCompat);

    void onError(String error);
}
