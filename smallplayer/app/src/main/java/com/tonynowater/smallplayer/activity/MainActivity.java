package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.util.PlayerState;
import com.tonynowater.smallplayer.util.SongPlayer;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.buttonPlay:
                    if(mSongPlayer.getmPlayerState() == PlayerState.INIT)
                    {
                        mSongPlayer.prepareForStart(mSongPath);
                        mActivityMainBinding.textViewStatusValue.setText("播放中");
                    }
                    else if(mSongPlayer.getmPlayerState() == PlayerState.PAUSE)
                    {
                        mSongPlayer.startPlayer();
                        mActivityMainBinding.textViewStatusValue.setText("播放中");
                    }
                    break;
                case R.id.buttonStop:
                    mSongPlayer.pause();
                    mActivityMainBinding.textViewStatusValue.setText("已暫停");
                    break;
            }
        }
    };

    private SongPlayer mSongPlayer;
    private ActivityMainBinding mActivityMainBinding;
    private String mSongPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        mActivityMainBinding.buttonPlay.setOnClickListener(mOnClickListener);
        mActivityMainBinding.buttonStop.setOnClickListener(mOnClickListener);

        mSongPlayer = new SongPlayer();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout_mainactivity,SongListFragment.newInstance(),SongListFragment.class.getSimpleName())
                .commit();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
