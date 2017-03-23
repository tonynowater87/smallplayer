package com.tonynowater.smallplayer.util;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by tonynowater on 2017/3/23.
 */

public class SongPlayer implements MediaPlayer.OnPreparedListener
{
    private MediaPlayer mPlayer = null;
    private int mPlayerState = PlayerState.INIT;


    public SongPlayer()
    {

    }

    public void prepareForStart(String source)
    {
        initialPlayer();

        try
        {
            mPlayer.setDataSource(source);
            mPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            uninitialPlayer();
        }
    }

    private void initialPlayer()
    {
        if (mPlayer == null)
        {
            mPlayer = new MediaPlayer();
            mPlayer.setOnPreparedListener(this);
        }
    }

    private void uninitialPlayer()
    {
        if (mPlayer != null)
        {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void startPlayer()
    {
        mPlayer.start();
        mPlayerState = PlayerState.PLAY;
    }

    public void stopPlayer()
    {
        if (mPlayer != null)
        {
            mPlayer.stop();
            mPlayerState = PlayerState.INIT;
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp)
    {
        startPlayer();
    }

    public void pause()
    {
        if (mPlayer != null)
        {
            mPlayer.pause();
            mPlayerState = PlayerState.PAUSE;
        }
    }

    public int getmPlayerState()
    {
        return mPlayerState;
    }
}
