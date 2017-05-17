package com.tonynowater.smallplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    private PlaybackStateCompat mPlaybackStateCompat;
    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private MediaControllerCompat.TransportControls mTransportControls;
    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onSessionDestroyed() {
            Log.d(TAG, "onSessionDestroyed: ");
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d(TAG, "onPlaybackStateChanged: " + state);
            if (state == null) {
                return;
            }

            mPlaybackStateCompat = state;
            MainActivity.this.onPlaybackStateChange(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.d(TAG, "onMetadataChanged: ");
            mBinding.textViewSongNameValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        }
    };

    private MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected: " + mMediaBrowserCompat.getSessionToken());

            if (mMediaBrowserCompat.getSessionToken() == null) {
                throw new IllegalArgumentException("No session token");
            }

            try {
                mMediaControllerCompat = new MediaControllerCompat(MainActivity.this, mMediaBrowserCompat.getSessionToken());
                mTransportControls = mMediaControllerCompat.getTransportControls();
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(MainActivity.this, mMediaControllerCompat);
                mPlaybackStateCompat = mMediaControllerCompat.getPlaybackState();
                onPlaybackStateChange(mPlaybackStateCompat);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "onConnected: " + e.toString());
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.d(TAG, "onConnectionFailed: ");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.d(TAG, "onConnectionSuspended: ");
        }
    };

    /**
     * 更新播放的狀態
     * @param mPlaybackStateCompat
     */
    private void onPlaybackStateChange(PlaybackStateCompat mPlaybackStateCompat) {
        Log.d(TAG, "onPlaybackStateChange : " + mPlaybackStateCompat);

        if (mPlaybackStateCompat == null) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int state = mPlaybackStateCompat.getState();
        boolean enablePlay = false;
        switch (state) {
            case PlaybackStateCompat.STATE_NONE:
                stringBuilder.append("STATE_NONE");
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                stringBuilder.append(getString(R.string.play_state_playing));
                enablePlay = false;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                stringBuilder.append(getString(R.string.play_state_pause));
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                stringBuilder.append(getString(R.string.play_state_stop));
                enablePlay = true;
                break;
            default:
                stringBuilder.append(state);
                break;
        }

        Log.d(TAG, "onPlaybackStateChange: " + stringBuilder.toString());
        mBinding.textViewStatusValue.setText(stringBuilder.toString());
        mBinding.buttonPlay.setText(enablePlay ? getString(R.string.button_string_play) : getString(R.string.button_string_pause));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            long state = mPlaybackStateCompat != null ? mPlaybackStateCompat.getState() : PlaybackStateCompat.STATE_NONE;

            switch (v.getId()) {
                case R.id.buttonPlay:
                    if (TextUtils.equals(mBinding.buttonPlay.getText().toString(),getString(R.string.button_string_play))) {
                        play();
                    } else if (TextUtils.equals(mBinding.buttonPlay.getText().toString(),getString(R.string.button_string_pause))) {
                        pause();
                    }
                    break;
                case R.id.buttonStop:
                    stop();
                    break;
                case R.id.buttonNext:
                    skipToNext();
                    break;
                case R.id.buttonPrevious:
                    skipToPrevious();
                    break;
            }
        }
    };

    private void skipToNext() {
        if (mTransportControls != null) {
            mTransportControls.skipToNext();
        }
    }

    private void skipToPrevious() {
        if (mTransportControls != null) {
            mTransportControls.skipToPrevious();
        }
    }

    private void pause() {
        if (mTransportControls != null) {
            mTransportControls.pause();
        }
    }

    private void stop() {
        if (mTransportControls != null) {
            mTransportControls.stop();
        }
    }

    private void play() {
        if (mTransportControls != null) {
            mTransportControls.play();
        }
    }

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
        mBinding.buttonNext.setOnClickListener(mOnClickListener);
        mBinding.buttonPrevious.setOnClickListener(mOnClickListener);
        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationSelectedListener);
        mBinding.bottomNavigationView.setSelectedItemId(R.id.local_music_bottom_navigation_view);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, PlayMusicService.class), mConnectionCallback, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaBrowserCompat != null) {
            Log.d(TAG, "onResume: connect");
            mMediaBrowserCompat.connect();
        }
    }

    @Override
    protected void onPause() {
        if (mMediaBrowserCompat != null) {
            Log.d(TAG, "onPause: disconnect");
            mMediaBrowserCompat.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onClick(Playable playable) {

        if (playable instanceof Song) {
            sendMetaDataToService(((Song) playable).getMediaMetadata());
        } else {
            final U2BVideoDTO.ItemsBean u2bVideoItem = ((U2BVideoDTO.ItemsBean) playable);
            YoutubeExtratorUtil.extratYoutube(getApplicationContext(), u2bVideoItem.getId().getVideoId(), new YoutubeExtratorUtil.CallBack() {
                @Override
                public void getU2BUrl(String url) {
                    u2bVideoItem.setDataSource(url);
                    sendMetaDataToService(u2bVideoItem.getMediaMetadata());
                }
            });
        }
    }

    /**
     * 將播放資料傳至Service
     * @param mediaMetadata
     */
    private void sendMetaDataToService(MediaMetadataCompat mediaMetadata) {
        Log.d(TAG, "sendMetaDataToService: " + mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        Intent it = new Intent(this,PlayMusicService.class);
        it.setAction(PlayMusicService.ACTION_ADD_NEW_MUSIC);
        it.putExtra(PlayMusicService.BUNDLE_KEY_MEDIAMETADATA, mediaMetadata);
        startService(it);
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
