package com.tonynowater.smallplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.MediaUtils;
import com.tonynowater.smallplayer.util.PlayerState;
import com.tonynowater.smallplayer.util.SongPlayer;

import java.util.List;

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
                        mSongPlayer.prepareForStart(sPath);
                        tvSongStatus.setText("播放中");
                    }
                    else if(mSongPlayer.getmPlayerState() == PlayerState.PAUSE)
                    {
                        mSongPlayer.startPlayer();
                        tvSongStatus.setText("播放中");
                    }
                    break;
                case R.id.buttonStop:
                    mSongPlayer.pause();
                    tvSongStatus.setText("已暫停");
                    break;
            }
        }
    };
    private SongPlayer mSongPlayer;
    private TextView tvSongName;
    private TextView tvSongStatus;
    private String sPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSongName = (TextView) findViewById(R.id.textViewSongName);
        tvSongStatus = (TextView) findViewById(R.id.textViewStatus);
        Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        Button buttonStop = (Button) findViewById(R.id.buttonStop);

        buttonPlay.setOnClickListener(mOnClickListener);
        buttonStop.setOnClickListener(mOnClickListener);


        mSongPlayer = new SongPlayer();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        List<Song> songList = MediaUtils.getAudioList(getApplicationContext());
        for (int i = 0; i < songList.size(); i++)
        {
            sPath = MediaUtils.getAudioList(getApplicationContext()).get(i).getmData();
            if (sPath != null)
            {
                tvSongName.setText(sPath);
                break;
            }
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mSongPlayer.stopPlayer();
    }
}
