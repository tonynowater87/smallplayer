package com.tonynowater.smallplayer.module.dto;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import com.tonynowater.smallplayer.module.dto.realm.PlayListSongDTO;
import com.tonynowater.smallplayer.u2b.Playable;
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

    public MediaMetadataCompat getMediaMetadata() {

        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(mId))
                .putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_SOURCE, mData)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mAlbum)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mArtist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mDuration)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mAlbumObj.getmAlbumArt())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, mAlbumObj.getmAlbum())
                .putString(MetaDataCustomKeyDefine.CUSTOM_METADATA_KEY_IS_LOCAL, MetaDataCustomKeyDefine.ISLOCAL)
                .build();
    }

    public PlayListSongDTO getPlayListSongDTO() {
        PlayListSongDTO playListSongDTO = new PlayListSongDTO();
        playListSongDTO.setId(mId);
        playListSongDTO.setSource(mData);
        playListSongDTO.setArtist(mArtist);
        playListSongDTO.setTitle(mTitle);
        playListSongDTO.setDuration(mDuration);
        playListSongDTO.setAlbumArtUri(mAlbumObj.getmAlbumArt());
        playListSongDTO.setIsLocal(MetaDataCustomKeyDefine.ISLOCAL);
        return playListSongDTO;
    }
}
