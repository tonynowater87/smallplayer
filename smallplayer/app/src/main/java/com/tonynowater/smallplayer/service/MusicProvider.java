package com.tonynowater.smallplayer.service;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyliao on 2017/5/12.
 */
public class MusicProvider {
    private static final String TAG = MusicProvider.class.getSimpleName();
    public static final String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    private ArrayList<MediaMetadataCompat> mMusicPlayList;

    public MusicProvider() {
        mMusicPlayList = new ArrayList<>();
    }

    public void putNewMusic(MediaMetadataCompat mediaMetadata) {
        mMusicPlayList.add(mediaMetadata);
    }

    public MediaMetadataCompat getPlayList(int index) {
        return index > mMusicPlayList.size() && mMusicPlayList.size() != 0 ? null : mMusicPlayList.get(index);
    }

    public int getPlayListSize() {
        return mMusicPlayList.size();
    }

    public boolean isPlayListAvailable () {
        return mMusicPlayList.size() > 0;
    }

    public List<MediaBrowserCompat.MediaItem> getMediaItemList() {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (int i = 0; i < mMusicPlayList.size(); i++) {
            mediaItems.add(createMediaItem(mMusicPlayList.get(i)));
        }
        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat mediaMetadataCompat) {

        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                .setTitle(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .setSubtitle(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION))
                .build();
        return new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }
}
