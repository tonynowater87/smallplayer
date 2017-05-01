package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
                        prepareSongPlayer();
                    }
                    else if(mSongPlayer.getmPlayerState() == PlayerState.PAUSE)
                    {
                        resumePlayer();
                    }
                    break;
                case R.id.buttonStop:
                    pausePlayer();
                    break;
            }
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.local_music_bottom_navigation_view:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout_mainactivity,SongListFragment.newInstance(),SongListFragment.class.getSimpleName())
                            .commit();
                    break;
                case R.id.u2b_search_bottom_navigation_view:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout_mainactivity,U2BSearchFragment.newInstance(),U2BSearchFragment.class.getSimpleName())
                            .commit();
                    break;
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mBinding.buttonPlay.setOnClickListener(mOnClickListener);
        mBinding.buttonStop.setOnClickListener(mOnClickListener);
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationSelectedListener);
        mBinding.bottomNavigationView.setSelectedItemId(R.id.local_music_bottom_navigation_view);
        mSongPlayer = new SongPlayer();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onClick(Song song) {
        mSongPath = song.getmData();

        mBinding.textViewSongNameValue.setText(song.getmTitle());

        prepareSongPlayer();
    }

    private void prepareSongPlayer() {
        mSongPlayer.prepareForStart(mSongPath);
        mBinding.textViewStatusValue.setText(R.string.play_state_playing);
    }

    private void pausePlayer() {
        mSongPlayer.pause();
        mBinding.textViewStatusValue.setText(R.string.play_state_pause);
    }

    private void resumePlayer() {
        mSongPlayer.startPlayer();
        mBinding.textViewStatusValue.setText(R.string.play_state_playing);
    }
}
