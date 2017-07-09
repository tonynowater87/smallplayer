package com.tonynowater.smallplayer.service;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
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
    private int mSongPosition = 0;
    private EnumPlayMode mEnumPlayMode = EnumPlayMode.NORMAL;

    /**
     * @param playlistPosition 預設要載入的播放清單id
     */
    public MusicProvider(int playlistPosition) {
        mMusicPlayList = new ArrayList<>();
        queryDBPlayList(playlistPosition);
    }

    /** 切換歌單 */
    public void queryDBPlayList(int playlistId) {
        mMusicPlayList.clear();
        RealmUtils realmUtils = new RealmUtils();
        List<PlayListSongEntity> playListSongEntities = realmUtils.queryPlayListSongByListIdSortByPosition(playlistId);
        for (int i = 0; i < playListSongEntities.size(); i++) {
            mMusicPlayList.add(playListSongEntities.get(i).getMediaMetadata());
        }
        realmUtils.close();
    }

    public MediaMetadataCompat getPlayList(int index) {
        return index < mMusicPlayList.size() && mMusicPlayList.size() >= 0 ? mMusicPlayList.get(index) : null;
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

        Bundle bundle = new Bundle();
        bundle.putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE, mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        bundle.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        bundle.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION));
        bundle.putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL, mediaMetadataCompat.getString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL));

        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(String.valueOf(idIsPosition))
                .setExtras(bundle)
                .build();
        return new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public void minusSongPosition() {
        switch (mEnumPlayMode) {
            case NORMAL:
                mSongPosition--;
                if (mSongPosition < 0) {
                    mSongPosition = mMusicPlayList.size() - 1;
                }
                break;
            case RANDOM:
                int randomPosition = (int)(Math.random() * mMusicPlayList.size());
                if (randomPosition == mSongPosition) {
                    mSongPosition--;
                    if (mSongPosition < 0) {
                        mSongPosition = mMusicPlayList.size() - 1;
                    }
                } else {
                    mSongPosition = randomPosition;
                }
                break;
        }

        Log.d(TAG, "minusSongPosition mode : " + mEnumPlayMode.name());
        Log.d(TAG, "minusSongPosition : " + mSongPosition);
    }

    public void setSongPosition(int mSongPosition) {
        this.mSongPosition = mSongPosition;
    }

    public int getSongPosition() {
        return mSongPosition;
    }

    /**
     * @return 目前正在播放的MediaMetaData
     */
    public MediaMetadataCompat getCurrentPlayingMediaMetadata() {
        return getPlayList(mSongPosition);
    }

    /**
     * @return 目前正在播放的位置
     */
    public int getCurrentPlayingIndex() {
        Log.d(TAG, "getCurrentPlayingIndex: " + mSongPosition);
        Log.d(TAG, "getMediaItemList size: " + getMediaItemList().size());
        if (getPlayList(mSongPosition) == null) {
            mSongPosition = getMediaItemList().size() - 1;
        }
        return mSongPosition;
    }

    public void addSongPosition() {

        switch (mEnumPlayMode) {
            case NORMAL:
                mSongPosition++;
                if (mSongPosition >= getPlayListSize()) {
                    mSongPosition = 0;
                }
                break;
            case RANDOM:
                int randomPosition = (int) (Math.random() * mMusicPlayList.size());
                Log.d(TAG, "addSongPosition randomPosition : " + randomPosition);
                if (randomPosition == mSongPosition) {
                    mSongPosition++;
                    if (mSongPosition >= getPlayListSize()) {
                        mSongPosition = 0;
                    }
                } else {
                    mSongPosition = randomPosition;
                }
                break;
        }

        Log.d(TAG, "addSongPosition mode : " + mEnumPlayMode.name());
        Log.d(TAG, "addSongPosition: " + mSongPosition);
    }

    public EnumPlayMode getmEnumPlayMode() {
        return mEnumPlayMode;
    }

    public void setmEnumPlayMode(EnumPlayMode mEnumPlayMode) {
        this.mEnumPlayMode = mEnumPlayMode;
    }
}
