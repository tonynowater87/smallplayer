package com.tonynowater.smallplayer.activity;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;

/**
 * 顯示Youtube搜尋歌曲清單Activity
 */
public class PlayListActivity extends BaseActivity {

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {

    }

    @Override
    protected void onSessionDestroyed() {

    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    @Override
    public void onClick(Object o) {

    }
}
