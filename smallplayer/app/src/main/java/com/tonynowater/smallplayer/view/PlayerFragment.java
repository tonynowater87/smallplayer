package com.tonynowater.smallplayer.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.FragmentPlayerBinding;
import com.tonynowater.smallplayer.service.PlayMusicService;


/**
 * 播放
 * Created by tonynowate on 2017/5/20.
 */
public class PlayerFragment extends BaseFragment<FragmentPlayerBinding> {
    private static final String TAG = PlayerFragment.class.getSimpleName();

    private PlaybackStateCompat mPlaybackStateCompat;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mBinding.buttonPlay.setOnClickListener(mOnClickListener);
        mBinding.buttonStop.setOnClickListener(mOnClickListener);
        mBinding.buttonNext.setOnClickListener(mOnClickListener);
        mBinding.buttonPrevious.setOnClickListener(mOnClickListener);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_player;
    }

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: " + state);
        if (state == null) {
            return;
        }

        int duration = state.getExtras() == null ? 0 : state.getExtras().getInt(PlayMusicService.BUNDLE_KEY_SONG_DURATION);
        mPlaybackStateCompat = state;
        PlayerFragment.this.updateState(state);

        Log.d(TAG, "onPlaybackStateChanged: position " + state.getPosition());
        Log.d(TAG, "onPlaybackStateChanged: duration " + duration );

        if (duration != 0) {
            if (mBinding.progressBar.getmMax() != duration) {
                mBinding.progressBar.setmMax(state.getExtras().getInt(PlayMusicService.BUNDLE_KEY_SONG_DURATION));
            }

            mBinding.progressBar.setmProgress((int) state.getPosition());
        }
    }

    // TODO: 2017/5/21 not attached to Activity 滑掉APP時會出現此錯誤
    /**
     * 更新播放的狀態
     * @param state
     */
    private void updateState(PlaybackStateCompat state) {
        Log.d(TAG, "updateState : " + mPlaybackStateCompat);

        if (mPlaybackStateCompat == null) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean enablePlay = false;
        switch (state.getState()) {
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

        Log.d(TAG, "updateState: " + stringBuilder.toString());
        mBinding.buttonPlay.setImageDrawable(enablePlay ? getActivity().getDrawable(android.R.drawable.ic_media_play) : getActivity().getDrawable(android.R.drawable.ic_media_pause));
    }

    @Override
    protected void onSessionDestroyed() {

    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataChanged: ");
        if (metadata == null) {
            return;
        }
        mBinding.textViewSongNameValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mBinding.textViewSongArtistValue.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
    }

    @Override
    protected void pause() {
        super.pause();
        mBinding.buttonPlay.setImageDrawable(getActivity().getDrawable(android.R.drawable.ic_media_play));
    }

    @Override
    protected void play() {
        super.play();
        mBinding.buttonPlay.setImageDrawable(getActivity().getDrawable(android.R.drawable.ic_media_pause));
    }
}
