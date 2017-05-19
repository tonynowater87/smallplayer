package com.tonynowater.smallplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.u2b.U2BVideoDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.YoutubeExtratorUtil;

public class MainActivity extends AppCompatActivity implements OnClickSomething<Playable> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private BaseFragment[] mBaseFragments = new BaseFragment[]{new SongListFragment(), new U2BSearchFragment()};
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

            int duration = state.getExtras() == null ? 0 : state.getExtras().getInt(PlayMusicService.BUNDLE_KEY_SONG_DURATION);
            mPlaybackStateCompat = state;
            MainActivity.this.onPlaybackStateChange(state);

            Log.d(TAG, "onPlaybackStateChanged: position " + state.getPosition());
            Log.d(TAG, "onPlaybackStateChanged: duration " + duration );

            if (duration != 0) {
                if (mBinding.progressBar.getmMax() != duration) {
                    mBinding.progressBar.setmMax(state.getExtras().getInt(PlayMusicService.BUNDLE_KEY_SONG_DURATION));
                }

                mBinding.progressBar.setmProgress((int) state.getPosition());
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.d(TAG, "onMetadataChanged: ");
            mBinding.textViewSongNameValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            mBinding.textViewSongArtistValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
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
        mBinding.buttonPlay.setImageDrawable(enablePlay ? getDrawable(android.R.drawable.ic_media_play) : getDrawable(android.R.drawable.ic_media_pause));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            long state = mPlaybackStateCompat != null ? mPlaybackStateCompat.getState() : PlaybackStateCompat.STATE_NONE;

            switch (v.getId()) {
                case R.id.buttonPlay:
                    if (state == PlaybackStateCompat.STATE_PAUSED
                      ||state == PlaybackStateCompat.STATE_STOPPED
                      ||state == PlaybackStateCompat.STATE_NONE) {
                        play();
                    } else if (state == PlaybackStateCompat.STATE_PLAYING) {
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
            initialUpdateProgressHandler();
        }
    }

    private void skipToPrevious() {
        if (mTransportControls != null) {
            mTransportControls.skipToPrevious();
            initialUpdateProgressHandler();
        }
    }

    private void pause() {
        if (mTransportControls != null) {
            mBinding.buttonPlay.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
            mTransportControls.pause();
        }
    }

    private void stop() {
        if (mTransportControls != null) {
            mTransportControls.stop();
        }

        uninitialUpdateProgressHandler();
    }

    private void uninitialUpdateProgressHandler() {
        if (mHandlerThread != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandlerThread.quitSafely();
            mHandlerThread.interrupt();
            mHandlerThread = null;
        }
    }

    private void play() {
        if (mTransportControls != null) {
            mBinding.buttonPlay.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
            mTransportControls.play();
            initialUpdateProgressHandler();
        }
    }

    private void initialUpdateProgressHandler() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread(TAG);
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        mHandler.post(mRunnable);
    }

    public static final int DELAY_TIME = 1000;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mTransportControls.sendCustomAction(PlayMusicService.ACTION_UPDATE_MUSIC_POSITION, null);
            mHandler.postDelayed(mRunnable, DELAY_TIME);
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
        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, PlayMusicService.class), mConnectionCallback, null);
        setSupportActionBar(mBinding.toolbarMainActivity);
        initialViewPager();
    }

    private void initialViewPager() {
        mBinding.viewpager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        mBinding.tabLayoutMainActivity.setupWithViewPager(mBinding.viewpager);
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

    private class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mBaseFragments[position];
        }

        @Override
        public int getCount() {
            return mBaseFragments.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseFragment fragment = (BaseFragment)super.instantiateItem(container, position);
            mBaseFragments[position] = fragment;
            return fragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mBaseFragments[position].getPageTitle(MainActivity.this);
        }
    }
}
