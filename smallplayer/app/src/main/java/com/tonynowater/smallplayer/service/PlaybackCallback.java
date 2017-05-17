package com.tonynowater.smallplayer.service;

/**
 * Created by tonyliao on 2017/5/14.
 */
public interface PlaybackCallback {

    void onCompletion();

    void onPlaybackStateChanged();

    void onError(String error);
}
