package com.tonynowater.smallplayer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.databinding.ActivityFullScreenPlayerBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.service.EnumPlayMode;
import com.tonynowater.smallplayer.service.EqualizerType;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.TimeUtil;
import com.tonynowater.smallplayer.view.CurrentPlayListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FullScreenPlayerActivity extends BaseMediaControlActivity<ActivityFullScreenPlayerBinding> implements View.OnClickListener{
    private static final String TAG = FullScreenPlayerActivity.class.getSimpleName();
    private static final long INITIAL_DELAY = 100;
    private static final long UPDATE_PERIOD = 1000;
    private static final int DEFAULT_PROGRESS = 0;
    private Handler mHandler = new Handler();
    private PlaybackStateCompat mLastPlaybackState;
    private List<MediaBrowserCompat.MediaItem> mCurrentPlayList = new ArrayList<>();
    private CurrentPlayListAdapter mCurrentPlayListAdapter;
    private MediaMetadataCompat mMediaMetaData;
    private EnumPlayMode mEnumPlayMode;
    private EqualizerType mLastEqualizerType;

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: ");
        if (state == null) {
            Log.d(TAG, "onPlaybackStateChanged: state == null");
            return;
        }
        updatePlaybackState(state);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        Log.d(TAG, "updatePlaybackState: ");
        mLastPlaybackState = state;
        mBinding.ivPlayPauseActivityFullScreenPlayer.setVisibility(View.VISIBLE);
        mBinding.progressBar.setVisibility(View.GONE);
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                scheduledSeekUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                mBinding.ivPlayPauseActivityFullScreenPlayer.setVisibility(View.GONE);
                mBinding.progressBar.setVisibility(View.VISIBLE);
                stopSeekbarUpdate();
                break;
            default:
                Log.d(TAG, "updatePlaybackState: unhandle state " + state.getState());
        }

        mCurrentPlayListAdapter.onPlaybackStateChanged(mLastPlaybackState);
        setShuffleButtonEnable(mLastPlaybackState.getExtras());
        setRepeatButtonEnable(mLastPlaybackState.getExtras());
        setLastEqualizerType();
    }

    private void setRepeatButtonEnable(Bundle bundle) {
        if (bundle == null) {
            Log.d(TAG, "setRepeatButtonEnable: bundle null");
            return;
        }

        boolean isRepeat = bundle.getBoolean(PlayMusicService.BUNDLE_KEY_IS_REPEAT);
        if (isRepeat) {
            mBinding.ivRepeatPlaylistActivityFullScreenPlayer.setColorFilter(Color.rgb(255, 56, 127));
            mBinding.ivRepeatPlaylistActivityFullScreenPlayer.setImageDrawable(getDrawable(R.drawable.icons8_repeat_select));
        } else {
            mBinding.ivRepeatPlaylistActivityFullScreenPlayer.setColorFilter(Color.WHITE);
            mBinding.ivRepeatPlaylistActivityFullScreenPlayer.setImageDrawable(getDrawable(R.drawable.icons8_repeat_unselect));
        }
    }

    /**
     * 設定播放模式：一般NORMAL、隨機RANDOM
     * @param bundle
     */
    private void setShuffleButtonEnable(Bundle bundle) {

        if (bundle == null) {
            Log.d(TAG, "setShuffleButtonEnable: bundle null");
            return;
        }

        mEnumPlayMode = MiscellaneousUtil.getPlayModeFromBundle(bundle);
        switch (mEnumPlayMode) {
            case NORMAL:
                mBinding.ivShuffleActivityFullScreenPlayer.setColorFilter(Color.WHITE);
                mBinding.ivShuffleActivityFullScreenPlayer.setImageDrawable(getDrawable(R.drawable.icons_shuffle_unselect));
                break;
            case RANDOM_NO_SAME:
                mBinding.ivShuffleActivityFullScreenPlayer.setColorFilter(Color.rgb(255, 56, 127));
                mBinding.ivShuffleActivityFullScreenPlayer.setImageDrawable(getDrawable(R.drawable.icons_shuffle_select));
                break;
        }

        subscribe();
    }

    private void setLastEqualizerType() {
        if (mLastPlaybackState.getExtras() != null) {
            mLastEqualizerType = (EqualizerType) mLastPlaybackState.getExtras().getSerializable(PlayMusicService.BUNDLE_KEY_EQUALIZER_TYPE);
        } else {
            mLastEqualizerType = (EqualizerType) mMediaBrowserCompat.getExtras().getSerializable(PlayMusicService.BUNDLE_KEY_EQUALIZER_TYPE);
        }
    }

    @Override
    protected void onSessionDestroyed() {
        Log.d(TAG, "onSessionDestroyed: ");
    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        Log.d(TAG, "onMetadataChanged: ");
        updateUI(metadata);
    }

    @Override
    protected void onMediaServiceConnected() {
        Log.d(TAG, "onMediaServiceConnected: ");
        setShuffleButtonEnable(mMediaBrowserCompat.getExtras());
    }

    @Override
    protected String getSubscribeID() {
        return PlayMusicService.GET_CURRENT_PLAY_LIST_ID;
    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children) {
        Log.d(TAG, "onChildrenLoaded: ");
        setPlaylistNameToToolbarTitle();
        mCurrentPlayList.clear();
        mCurrentPlayList = new ArrayList<>(children);
        mCurrentPlayListAdapter.setDataSource(mCurrentPlayList);
        mCurrentPlayListAdapter.notifyDataSetChanged();
        if (mMediaMetaData != null) {
            updateUI(mMediaMetaData);
        }
    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children, Bundle options) {

    }

    private void updateUI(MediaMetadataCompat metadata) {
        Log.d(TAG, "updateUI : " + metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mMediaMetaData = metadata;
        mBinding.tvCurrentSongActivityFullScreenPlayer.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mBinding.tvEndTextActivityFullScreenPlayer.setText(TimeUtil.formatSongDuration((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        mBinding.seekbarActivityFullScreenPlayer.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        mCurrentPlayListAdapter.onMetadataChanged(metadata);
        updateProgress();
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mBinding.tvStartTextActivityFullScreenPlayer.setText(DateUtils.formatElapsedTime(progress/1000));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            stopSeekbarUpdate();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mTransportControls.seekTo(seekBar.getProgress());
            scheduledSeekUpdate();
        }
    };

    private ScheduledFuture<?> mScheduledFuture;
    private ScheduledExecutorService mSceduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_full_screen_player;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //設置不要顯示SystemStatusBar
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        //mBinding.toolbar.toolbarMainActivity.setVisibility(View.GONE);//隱藏ToolBar
        MiscellaneousUtil.setToolBarMarquee(mBinding.toolbar.toolbarMainActivity);
        mBinding.seekbarActivityFullScreenPlayer.setOnSeekBarChangeListener(mOnSeekChangedListener);
        mBinding.ivPreviousActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivPlayPauseActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivNextActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivEqActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivChangePlaylistActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivShuffleActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivRepeatPlaylistActivityFullScreenPlayer.setOnClickListener(this);
        mCurrentPlayListAdapter = new CurrentPlayListAdapter(this);
        initialCurrentPlayList();
    }

    /** 設定Title為目前歌單名稱 */
    private void setPlaylistNameToToolbarTitle() {
        RealmUtils realmUtils = new RealmUtils();
        setTitle(realmUtils.getCurrentPlayListName());
        realmUtils.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopSeekbarUpdate();
        mSceduledExecutorService.shutdown();
        super.onDestroy();
    }

    private void scheduledSeekUpdate() {
        stopSeekbarUpdate();
        if (!mSceduledExecutorService.isShutdown()) {
            mScheduledFuture = mSceduledExecutorService.scheduleAtFixedRate(() -> mHandler.post(mRunnable), INITIAL_DELAY, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduledFuture != null) {
            mScheduledFuture.cancel(false);// TODO: 2017/5/22
        }
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            long timeDelta = SystemClock.elapsedRealtime() - mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        else {
            Log.d(TAG, "updateProgress: 不更新SeekBar的狀態 " + mLastPlaybackState.getState());
        }

        mBinding.seekbarActivityFullScreenPlayer.setProgress((int) currentPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_previous_activity_full_screen_player:
                mTransportControls.skipToPrevious();
                break;
            case R.id.iv_play_pause__activity_full_screen_player:
                onPressPlayButton();
                break;
            case R.id.iv_next_activity_full_screen_player:
                mTransportControls.skipToNext();
                break;
            case R.id.iv_eq_activity_full_screen_player:
                showEqList();
                break;
            case R.id.iv_change_playlist_activity_full_screen_player:
                DialogUtil.showChangePlayListDialog(this, new DialogUtil.CallBack() {
                    @Override
                    public void onSubmit() {
                        subscribe();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.iv_shuffle_activity_full_screen_player:
                sendChangePlayModeAction();
                break;
            case R.id.iv_repeat_playlist_activity_full_screen_player:
                sendChangeRepeatAction();
                break;
        }
    }

    private void sendChangeRepeatAction() {
        mTransportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_REPEAT, null);
    }

    private void sendChangePlayModeAction() {
        switch (mEnumPlayMode) {
            case NORMAL:
                mEnumPlayMode = EnumPlayMode.RANDOM_NO_SAME;
                break;
            case RANDOM_NO_SAME:
                mEnumPlayMode = EnumPlayMode.NORMAL;
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(PlayMusicService.BUNDLE_KEY_PLAYMODE, mEnumPlayMode);
        mTransportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_PLAYMODE, bundle);
    }

    @Override
    public void onClick(Object o) {
        if (o instanceof MediaBrowserCompat.MediaItem) {
            MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem) o;
            Bundle bundle = new Bundle();
            bundle.putInt(PlayMusicService.BUNDLE_KEY_EXPLICIT_PLAYLIST_POSITION, Integer.parseInt(mediaItem.getMediaId()));
            mTransportControls.sendCustomAction(PlayMusicService.ACTION_PLAY_EXPLICIT_POSITION_IN_PLAYLIST, bundle);
            mBinding.seekbarActivityFullScreenPlayer.setProgress(DEFAULT_PROGRESS);
        }
    }

    /** 設定目前播放清單RecyclerViewAdpater */
    private void initialCurrentPlayList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerViewDivideLineDecorator recyclerViewDivideLineDecorator = new RecyclerViewDivideLineDecorator(this);
        mBinding.recyclerviewFullplayer.setAdapter(mCurrentPlayListAdapter);
        mBinding.recyclerviewFullplayer.setLayoutManager(linearLayoutManager);
        mBinding.recyclerviewFullplayer.addItemDecoration(recyclerViewDivideLineDecorator);
    }

    /** Show等化器風格設定Dialog */
    private void showEqList() {
        DialogUtil.showChangeEqualizerDialog(this, mLastEqualizerType, mTransportControls);
    }

    private void onPressPlayButton() {
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mTransportControls.pause();
        } else {
            mTransportControls.play();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_full_screen_player, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_edit:
//                EditPlayListActivity.startActivity(this);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_top_to_bottom);
    }
}
