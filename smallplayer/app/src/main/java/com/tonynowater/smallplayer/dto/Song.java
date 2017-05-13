package com.tonynowater.smallplayer.dto;

import android.media.MediaMetadata;
import android.os.Bundle;
import android.provider.MediaStore;

import com.tonynowater.myyoutubeapi.Playable;
import com.tonynowater.smallplayer.service.MusicProvider;
import com.tonynowtaer87.myutil.TimeUtil;

/**
 * Created by tonynowater on 2017/3/23.
 */
public class Song implements Playable
{
    private int mId;
    private int mDuration;
    private int mSize;
    private int mYear;
    private int mTrack;
    private int mIsMusic;
    private int mAlbumID;
    private String mTitle;
    private String mArtist;
    private String mComposer;
    private String mAlbum;
    private String mDisplayName;
    private String mMimeType;
    private String mData;
    private Album mAlbumObj;

    public Song(Bundle bundle)
    {
        mId = bundle.getInt(MediaStore.Audio.Media._ID);
        mDuration = bundle.getInt(MediaStore.Audio.Media.DURATION);
        mSize = bundle.getInt(MediaStore.Audio.Media.SIZE);
        mYear = bundle.getInt(MediaStore.Audio.Media.YEAR);
        mTrack = bundle.getInt(MediaStore.Audio.Media.TRACK);
        mIsMusic = bundle.getInt(MediaStore.Audio.Media.IS_MUSIC);
        mTitle = bundle.getString(MediaStore.Audio.Media.TITLE);
        mArtist = bundle.getString(MediaStore.Audio.Media.ARTIST);
        mComposer = bundle.getString(MediaStore.Audio.Media.COMPOSER);
        mAlbum = bundle.getString(MediaStore.Audio.Media.ALBUM);
        mAlbumID = bundle.getInt(MediaStore.Audio.Media.ALBUM_ID);
        mDisplayName = bundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
        mMimeType = bundle.getString(MediaStore.Audio.Media.MIME_TYPE);
        mData = bundle.getString(MediaStore.Audio.Media.DATA);
    }

    public void setmAlbumObj(Album mAlbumObj) {
        this.mAlbumObj = mAlbumObj;
    }

    public Album getmAlbumObj() {
        return mAlbumObj;
    }

    @Override
    public String toString() {
        return mAlbum + "\t" + mArtist + "\t" + mTitle;
    }

    public int getmId() {
        return mId;
    }

    public int getmDuration() {
        return mDuration;
    }

    public String getFormatDuration() {
        return TimeUtil.formatSongDuration(getmDuration());
    }

    public int getmSize() {
        return mSize;
    }

    public int getmYear() {
        return mYear;
    }

    public int getmTrack() {
        return mTrack;
    }

    public int getmIsMusic() {
        return mIsMusic;
    }

    public String getmComposer() {
        return mComposer;
    }

    public int getmAlbumID() {
        return mAlbumID;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public String getmMimeType() {
        return mMimeType;
    }

    public String getmData() {
        return mData;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public MediaMetadata genMediaMetadata() {

        return new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, String.valueOf(mId))
                .putString(MusicProvider.CUSTOM_METADATA_TRACK_SOURCE, mData)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, mAlbum)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, mArtist)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mDuration)
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, mAlbumObj.getmAlbumArt())
                .putString(MediaMetadata.METADATA_KEY_TITLE, mTitle)
                .build();
    }
}
