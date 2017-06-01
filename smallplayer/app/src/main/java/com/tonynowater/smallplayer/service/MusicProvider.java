package com.tonynowater.smallplayer.service;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyliao on 2017/5/12.
 */
public class MusicProvider {
    private static final String TAG = MusicProvider.class.getSimpleName();
    private ArrayList<MediaMetadataCompat> mMusicPlayList;

    public MusicProvider() {
        //預設先載入第0組的播放清單
        mMusicPlayList = new ArrayList<>();
        queryDBPlayList(0);
    }

    /** 切換歌單 */
    public void queryDBPlayList(int position) {
        mMusicPlayList.clear();
        RealmUtils realmUtils = new RealmUtils();
        List<PlayListSongEntity> playListSongEntities = realmUtils.queryPlayListSongByListIdSortByPosition(position);
        for (int i = 0; i < playListSongEntities.size(); i++) {
            mMusicPlayList.add(playListSongEntities.get(i).getMediaMetadata());
        }
        realmUtils.close();
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
            mediaItems.add(createMediaItem(i,mMusicPlayList.get(i)));
        }
        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createMediaItem(int idIsPosition, MediaMetadataCompat mediaMetadataCompat) {

        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(String.valueOf(idIsPosition))
                .setTitle(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .setSubtitle(mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION))
                .build();
        return new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }
}
