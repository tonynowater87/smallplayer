package com.tonynowater.smallplayer.module.u2b;

import android.support.v4.media.MediaMetadataCompat;

import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

/**
 * Created by tonyliao on 2017/5/1.
 */

public interface Playable {
    MediaMetadataCompat getMediaMetadata();
    PlayListSongEntity getPlayListSongEntity();
}
