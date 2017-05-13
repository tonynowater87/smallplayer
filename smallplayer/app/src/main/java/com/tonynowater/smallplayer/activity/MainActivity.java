package com.tonynowater.smallplayer.activity;

import android.content.ComponentName;
import android.databinding.DataBindingUtil;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.tonynowater.myyoutubeapi.Playable;
import com.tonynowater.myyoutubeapi.U2BVideoDTO;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.YoutubeExtratorUtil;

public class MainActivity extends AppCompatActivity implements OnClickSomething<Playable> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private String mSongPath;
    private MediaBrowser mMediaBrowserCompat;
    private MediaController mediaControllerCompat;

    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback(){
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected: " + mMediaBrowserCompat.getSessionToken());

            if (mMediaBrowserCompat.getSessionToken() == null) {
                throw new IllegalArgumentException("No session token");
            }



        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonPlay:
                    break;
                case R.id.buttonStop:
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

        //mMediaBrowserCompat = new MediaBrowserCompat(this,new ComponentName(this, PlayMusicService.class), mConnectionCallback, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaBrowserCompat != null) {
            mMediaBrowserCompat.connect();
        }
    }

    @Override
    protected void onPause() {
        if (mMediaBrowserCompat != null) {
            mMediaBrowserCompat.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onClick(Playable playable) {

        if (playable instanceof Song) {
            mSongPath = ((Song) playable).getmData();
            mBinding.textViewSongNameValue.setText(((Song) playable).getmTitle());
        } else {
            final U2BVideoDTO.ItemsBean u2bVideoItem = ((U2BVideoDTO.ItemsBean) playable);
            YoutubeExtratorUtil.extratYoutube(getApplicationContext(), u2bVideoItem.getId().getVideoId(), new YoutubeExtratorUtil.CallBack() {
                @Override
                public void getU2BUrl(String url) {
                    mSongPath = url;
                    mBinding.textViewSongNameValue.setText(u2bVideoItem.getSnippet().getTitle());
                }
            });
        }
    }

    /**
     * BottomView切換Fragment
     *
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
