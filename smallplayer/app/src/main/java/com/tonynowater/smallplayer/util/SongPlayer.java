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
        try
        {
            initialPlayer(source);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            uninitialPlayer();
        }
    }

    private void initialPlayer(String source) throws IOException
    {
        if (mPlayer == null)
        {
            mPlayer = new MediaPlayer();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setDataSource(source);
            mPlayer.prepare();
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
            mPlayer = null;
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

    /**
     * @return 目前播放器的狀態
     */
    public int getmPlayerState()
    {
        return mPlayerState;
    }
}
