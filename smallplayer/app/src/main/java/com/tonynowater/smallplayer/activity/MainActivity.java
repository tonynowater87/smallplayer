package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.PlayerState;
import com.tonynowater.smallplayer.util.SongPlayer;

public class MainActivity extends AppCompatActivity implements OnClickSomething<Song>
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private SongPlayer mSongPlayer;
    private ActivityMainBinding mBinding;
    private String mSongPath;

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
                        mBinding.textViewStatusValue.setText(R.string.play_state_playing);
                    }
                    else if(mSongPlayer.getmPlayerState() == PlayerState.PAUSE)
                    {
                        mSongPlayer.startPlayer();
                        mBinding.textViewStatusValue.setText(R.string.play_state_playing);
                    }
                    break;
                case R.id.buttonStop:
                    mSongPlayer.pause();
                    mBinding.textViewStatusValue.setText(R.string.play_state_pause);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        mBinding.buttonPlay.setOnClickListener(mOnClickListener);
        mBinding.buttonStop.setOnClickListener(mOnClickListener);

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

    @Override
    public void onClick(Song song) {
        if (mSongPlayer.getmPlayerState() == PlayerState.PLAY) {
            mSongPlayer.stopPlayer();
            mBinding.textViewStatusValue.setText(R.string.play_state_stop);
        }

        mSongPath = song.getmData();
        mBinding.textViewSongNameValue.setText(song.getmTitle());
    }
}
