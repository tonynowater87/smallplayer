package com.tonynowater.smallplayer.service;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import android.util.Log;

import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by tonyliao on 2017/5/12.
 */
public class MusicProvider {
    private static final String TAG = MusicProvider.class.getSimpleName();
    private ArrayList<MediaMetadataCompat> mMusicPlayList;
    private ArrayList<MediaMetadataCompat> mRandomMusicPlayList;
    private int mSongPosition = 0;
    private boolean mIsRepeat = false;
    private EnumPlayMode mEnumPlayMode = EnumPlayMode.NORMAL;

    /**
     * @param playlistPosition 預設要載入的播放清單id
     */
    public MusicProvider(int playlistPosition) {
        mMusicPlayList = new ArrayList<>();
        queryDBPlayList(playlistPosition);
    }

    /**
     * 產生亂數排序的歌單
     */
    public void generateRandomList() {
        if (MiscellaneousUtil.isObjOK(mMusicPlayList)) {
            Log.d(TAG, "generateRandomList: ");
            mRandomMusicPlayList = new ArrayList<>(mMusicPlayList);
            Collections.shuffle(mRandomMusicPlayList, new Random(System.currentTimeMillis()));
        }
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
        generateRandomList();
    }

    /**
     * @param index
     * @return
     */
    public MediaMetadataCompat getPlayItemByIndex(int index, EnumPlayMode enumPlayMode) {
        switch (enumPlayMode) {
            case NORMAL:
                return index < mMusicPlayList.size() && mMusicPlayList.size() >= 0 ? mMusicPlayList.get(index) : null;
            case RANDOM_NO_SAME:
                return index < mRandomMusicPlayList.size() && mRandomMusicPlayList.size() >= 0 ? mRandomMusicPlayList.get(index) : null;
            default:
                return null;
        }
    }

    public int getPlayListSize() {
        return mMusicPlayList.size();
    }

    public boolean isPlayListAvailable () {
        return mMusicPlayList.size() > 0;
    }

    /**
     * @return 目前播放清單的List 給畫面UI端顯示
     */
    public List<MediaBrowserCompat.MediaItem> getMediaItemList() {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        List<MediaMetadataCompat> list;
        switch (mEnumPlayMode) {
            case NORMAL:
                list = mMusicPlayList;
                break;
            case RANDOM_NO_SAME:
                list = mRandomMusicPlayList;
                break;
            default:
                list = new ArrayList<>();
                break;
        }

        for (int i = 0; i < list.size(); i++) {
            mediaItems.add(createMediaItem(i,list.get(i)));
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

    public void setSongPosition(int mSongPosition) {
        this.mSongPosition = mSongPosition;
    }

    /**
     * 跳到 指定歌曲的 位置
     * @param songId
     */
    public void setSongPositionNow(int songId) {
        switch (mEnumPlayMode) {
            case NORMAL:
                for (int i = 0; i < mMusicPlayList.size(); i++) {
                    if (getSongIDFromList(mMusicPlayList, i) == songId) {
                        this.mSongPosition = i;
                        return;
                    }
                }
                break;
            case RANDOM_NO_SAME:
                for (int i = 0; i < mRandomMusicPlayList.size(); i++) {
                    if (getSongIDFromList(mRandomMusicPlayList, i) == songId) {
                        this.mSongPosition = i;
                        return;
                    }
                }
                break;
        }

        //若是沒找到就是跳到第一首，應該是不會發生
        Log.e(TAG, "setSongPositionNow Song Id Not Found!! : " + songId);
        this.mSongPosition = 0;
    }

    /**
     * @param listSongs
     * @param position
     * @return 歌曲的ID
     */
    private int getSongIDFromList(ArrayList<MediaMetadataCompat> listSongs, int position) {
        return Integer.parseInt(listSongs.get(position).getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID));
    }

    public int getSongPosition() {
        return mSongPosition;
    }

    /**
     * @return 目前正在播放的MediaMetaData
     */
    public MediaMetadataCompat getCurrentPlayingMediaMetadata() {
        return getPlayItemByIndex(mSongPosition, mEnumPlayMode);
    }

    /**
     * @return 目前正在播放的位置
     */
    public int getCurrentPlayingIndex() {
        Log.d(TAG, "getCurrentPlayingIndex: " + mSongPosition);
        Log.d(TAG, "getMediaItemList size: " + getMediaItemList().size());

        if (getPlayItemByIndex(mSongPosition, mEnumPlayMode) == null) {
            mSongPosition = getMediaItemList().size() - 1;
        }

        return mSongPosition;
    }

    /**
     * @return 切換模式後，原播放的歌曲在另一個模式的哪個位置
     */
    private int getPostionInByMode(EnumPlayMode enumPlayMode) {

        MediaMetadataCompat normalModeMediaItem;
        MediaMetadataCompat randomModeMediaItem;

        switch (enumPlayMode) {
            case NORMAL:
                randomModeMediaItem = getPlayItemByIndex(mSongPosition, EnumPlayMode.RANDOM_NO_SAME);
                for (int i = 0; i < mMusicPlayList.size(); i++) {
                    normalModeMediaItem = mMusicPlayList.get(i);
                    if (TextUtils.equals(normalModeMediaItem.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                            , randomModeMediaItem.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
                        return i;
                    }

                }
                return 0;
            case RANDOM_NO_SAME:
                normalModeMediaItem = getPlayItemByIndex(mSongPosition, EnumPlayMode.NORMAL);
                for (int i = 0; i < mRandomMusicPlayList.size(); i++) {
                    randomModeMediaItem = mRandomMusicPlayList.get(i);
                    if (TextUtils.equals(normalModeMediaItem.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                            , randomModeMediaItem.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))) {
                        return i;
                    }
                }
            default:
                return 0;
        }
    }

    public void addSongPosition(boolean manualAdd) {
        switch (mEnumPlayMode) {
            case NORMAL:
            case RANDOM_NO_SAME:
                if(!manualAdd && mIsRepeat) {
                    break;
                }
                mSongPosition++;
                if (mSongPosition >= getPlayListSize()) {
                    mSongPosition = 0;
                }
                break;
        }

        if (manualAdd) {
            mIsRepeat = false;
        }

        Log.d(TAG, "addSongPosition mode : " + mEnumPlayMode.name());
        Log.d(TAG, "addSongPosition: " + mSongPosition);
    }

    public void minusSongPosition(boolean manualMinus) {
        switch (mEnumPlayMode) {
            case NORMAL:
            case RANDOM_NO_SAME:
                if(!manualMinus && mIsRepeat) {
                    break;
                }
                mSongPosition--;
                if (mSongPosition < 0) {
                    mSongPosition = mMusicPlayList.size() - 1;
                }
                break;
        }

        if (manualMinus) {
            mIsRepeat = false;
        }

        Log.d(TAG, "minusSongPosition mode : " + mEnumPlayMode.name());
        Log.d(TAG, "minusSongPosition : " + mSongPosition);
    }

    public EnumPlayMode getmEnumPlayMode() {
        return mEnumPlayMode;
    }

    public void setmEnumPlayMode(EnumPlayMode mEnumPlayMode) {
        this.mEnumPlayMode = mEnumPlayMode;
        mSongPosition = getPostionInByMode(mEnumPlayMode);
    }

    public void changeRepeatStatus() {
        this.mIsRepeat = !this.mIsRepeat;
    }

    public boolean getIsReapeated() {
        return mIsRepeat;
    }
}
