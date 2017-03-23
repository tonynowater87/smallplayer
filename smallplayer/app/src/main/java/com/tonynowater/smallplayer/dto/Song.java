package com.tonynowater.smallplayer.dto;

import android.os.Bundle;
import android.provider.MediaStore;

/**
 * Created by tonynowater on 2017/3/23.
 */
public class Song
{
    private int mId;
    private int mDuration;
    private int mSize;
    private int mYear;
    private int mTrack;
    private int mIsMusic;
    private String mTitle;
    private String mArtist;
    private String mComposer;
    private String mAlbum;
    private String mDisplayName;
    private String mMimeType;
    private String mData;

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
        mDisplayName = bundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
        mMimeType = bundle.getString(MediaStore.Audio.Media.MIME_TYPE);
        mData = bundle.getString(MediaStore.Audio.Media.DATA);
    }

    public int getmId()
    {
        return mId;
    }

    public void setmId(int mId)
    {
        this.mId = mId;
    }

    public int getmDuration()
    {
        return mDuration;
    }

    public void setmDuration(int mDuration)
    {
        this.mDuration = mDuration;
    }

    public int getmSize()
    {
        return mSize;
    }

    public void setmSize(int mSize)
    {
        this.mSize = mSize;
    }

    public int getmYear()
    {
        return mYear;
    }

    public void setmYear(int mYear)
    {
        this.mYear = mYear;
    }

    public int getmTrack()
    {
        return mTrack;
    }

    public void setmTrack(int mTrack)
    {
        this.mTrack = mTrack;
    }

    public int getmIsMusic()
    {
        return mIsMusic;
    }

    public void setmIsMusic(int mIsMusic)
    {
        this.mIsMusic = mIsMusic;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public void setmTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public String getmArtist()
    {
        return mArtist;
    }

    public void setmArtist(String mArtist)
    {
        this.mArtist = mArtist;
    }

    public String getmComposer()
    {
        return mComposer;
    }

    public void setmComposer(String mComposer)
    {
        this.mComposer = mComposer;
    }

    public String getmAlbum()
    {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum)
    {
        this.mAlbum = mAlbum;
    }

    public String getmDisplayName()
    {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName)
    {
        this.mDisplayName = mDisplayName;
    }

    public String getmMimeType()
    {
        return mMimeType;
    }

    public void setmMimeType(String mMimeType)
    {
        this.mMimeType = mMimeType;
    }

    public String getmData()
    {
        return mData;
    }

    public void setmData(String mData)
    {
        this.mData = mData;
    }
}
