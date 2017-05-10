package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.tonynowater.myyoutubeapi.Playable;
import com.tonynowater.myyoutubeapi.U2BVideoDTO;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.PlayerState;
import com.tonynowater.smallplayer.util.SongPlayer;
import com.tonynowater.smallplayer.util.YoutubeExtratorUtil;

public class MainActivity extends AppCompatActivity implements OnClickSomething<Playable> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SongPlayer mSongPlayer;
    private ActivityMainBinding mBinding;
    private String mSongPath;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonPlay:
                    if (mSongPlayer.getmPlayerState() == PlayerState.INIT) {
                        prepareSongPlayer();
                    } else if (mSongPlayer.getmPlayerState() == PlayerState.PAUSE) {
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
            changeFragment(item.getItemId());
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.buttonPlay.setOnClickListener(mOnClickListener);
        mBinding.buttonStop.setOnClickListener(mOnClickListener);
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationSelectedListener);
        mBinding.bottomNavigationView.setSelectedItemId(R.id.local_music_bottom_navigation_view);
        mSongPlayer = new SongPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(Playable playable) {

        if (playable instanceof Song) {
            mSongPath = ((Song) playable).getmData();
            mBinding.textViewSongNameValue.setText(((Song) playable).getmTitle());
            prepareSongPlayer();
        } else {
            final U2BVideoDTO.ItemsBean u2bVideoItem = ((U2BVideoDTO.ItemsBean) playable);
            YoutubeExtratorUtil.extratYoutube(getApplicationContext(), u2bVideoItem.getId().getVideoId(), new YoutubeExtratorUtil.CallBack() {
                @Override
                public void getU2BUrl(String url) {
                    mSongPath = url;
                    mBinding.textViewSongNameValue.setText(u2bVideoItem.getSnippet().getTitle());
                    prepareSongPlayer();
                }
            });
        }
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

    /**
     * BottomView切換Fragment
     * @param itemId buttomView項目ID
     */
    private void changeFragment(int itemId) {

        Fragment fragmentSongList = getSupportFragmentManager().findFragmentByTag(SongListFragment.class.getSimpleName());
        Fragment fragmentU2BSearch = getSupportFragmentManager().findFragmentByTag(U2BSearchFragment.class.getSimpleName());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (itemId) {
            case R.id.local_music_bottom_navigation_view:

                if (fragmentSongList != null && fragmentSongList.isHidden()) {
                    transaction.show(fragmentSongList);
                } else {
                    transaction.add(R.id.framelayout_mainactivity, SongListFragment.newInstance(), SongListFragment.class.getSimpleName());
                }

                if (fragmentU2BSearch != null && fragmentU2BSearch.isVisible()) {
                    transaction.hide(fragmentU2BSearch);
                }

                break;
            case R.id.u2b_search_bottom_navigation_view:
                fragmentU2BSearch = getSupportFragmentManager().findFragmentByTag(U2BSearchFragment.class.getSimpleName());
                if (fragmentU2BSearch != null && fragmentU2BSearch.isHidden()) {
                    transaction.show(fragmentU2BSearch);
                } else {
                    transaction.add(R.id.framelayout_mainactivity, U2BSearchFragment.newInstance(), U2BSearchFragment.class.getSimpleName());
                }

                if (fragmentSongList != null && fragmentSongList.isVisible()) {
                    transaction.hide(fragmentSongList);
                }

                break;
        }

        transaction.commit();
    }

}
