package com.tonynowater.smallplayer.u2b;

import android.support.v4.media.MediaMetadataCompat;

import com.tonynowater.smallplayer.module.dto.realm.PlayListSongDTO;

/**
 * Created by tonyliao on 2017/5/1.
 */

public interface Playable {
    MediaMetadataCompat getMediaMetadata();
    PlayListSongDTO getPlayListSongDTO();
}
