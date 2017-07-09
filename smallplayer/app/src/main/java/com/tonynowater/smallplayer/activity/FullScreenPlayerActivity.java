package com.tonynowater.smallplayer.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityFullScreenPlayerBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.module.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.service.EnumPlayMode;
import com.tonynowater.smallplayer.service.EqualizerType;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.view.CurrentPlayListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FullScreenPlayerActivity extends BaseActivity<ActivityFullScreenPlayerBinding> implements View.OnClickListener{
    private static final String TAG = FullScreenPlayerActivity.class.getSimpleName();
    private static final long INITIAL_DELAY = 100;
    private static final long UPDATE_PERIOD = 1000;
    private static final int DEFAULT_PROGRESS = 0;
    private Handler mHandler = new Handler();
    private PlaybackStateCompat mLastPlaybackState;
    private List<MediaBrowserCompat.MediaItem> mCurrentPlayList = new ArrayList<>();
    private BottomSheetDialog mBottomSheetDialog;
    private EnumPlayMode mEnumPlayMode;
    private CurrentPlayListAdapter mCurrentPlayListAdapter;
    private MediaMetadataCompat mMediaMetaData;

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
    }

    /**
     * @param bundle
     */
    private void setShuffleButtonEnable(Bundle bundle) {

        if (bundle == null) {
            Log.d(TAG, "setShuffleButtonEnable: bundle null");
            return;
        }

        mEnumPlayMode = getPlayMode(bundle);
        switch (mEnumPlayMode) {
            case NORMAL:
                mBinding.ivShuffleActivityFullScreenPlayer.setColorFilter(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                break;
            case RANDOM:
                mBinding.ivShuffleActivityFullScreenPlayer.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                break;
        }
    }

    private EnumPlayMode getPlayMode(Bundle bundle) {
        return (EnumPlayMode) bundle.getSerializable(PlayMusicService.BUNDLE_KEY_PLAYMODE);
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
        mCurrentPlayListAdapter = new CurrentPlayListAdapter(this);
        setShuffleButtonEnable(mMediaBrowserCompat.getExtras());
    }

    @Override
    protected String getSubscribeID() {
        return PlayMusicService.GET_CURRENT_PLAY_LIST_ID;
    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children) {
        Log.d(TAG, "onChildrenLoaded: ");
        mCurrentPlayList.clear();
        mCurrentPlayList = new ArrayList<>(children);
        mCurrentPlayListAdapter.setDataSource(mCurrentPlayList);
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
        mBinding.tvEndTextActivityFullScreenPlayer.setText(U2BApiUtil.formateU2BDurationToString(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        mBinding.seekbarActivityFullScreenPlayer.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        mBinding.tvTitleActivityFullScreenPlayer.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mBinding.tvArtistActivityFullScreenPlayer.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        String ArtUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
        if (!TextUtils.isEmpty(ArtUrl)) {
            Glide.with(getApplicationContext()).load(ArtUrl).into(mBinding.imageviewBackgroundActivityFullScreenPlayer);
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.ic_default_art).into(mBinding.imageviewBackgroundActivityFullScreenPlayer);
        }
        mCurrentPlayListAdapter.onMetadataChanged(metadata);
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
    protected int getLayoutResource() {
        return R.layout.activity_full_screen_player;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //設置不要顯示SystemStatusBar
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
//        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        mBinding.toolbar.toolbarMainActivity.setVisibility(View.GONE);//隱藏ToolBar
        mBinding.seekbarActivityFullScreenPlayer.setOnSeekBarChangeListener(mOnSeekChangedListener);
        mBinding.ivPreviousActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivPlayPauseActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivNextActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivEqActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivModeActivityFullScreenPlayer.setOnClickListener(this);
        mBinding.ivShuffleActivityFullScreenPlayer.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2017/6/8 讀取、預設圖還要在換
        Glide.with(getApplicationContext()).load(R.drawable.ic_default_art).into(mBinding.imageviewBackgroundActivityFullScreenPlayer);
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
            mScheduledFuture = mSceduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(mRunnable);
                }
            }, INITIAL_DELAY, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
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
            case R.id.iv_mode_activity_full_screen_player:
                showCurrentPlayList();
                break;
            case R.id.iv_shuffle_activity_full_screen_player:
                sendChangePlayModeAction();
                break;
        }
    }

    private void sendChangePlayModeAction() {
        switch (mEnumPlayMode) {
            case NORMAL:
                mEnumPlayMode = EnumPlayMode.RANDOM;
                break;
            case RANDOM:
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
            mBottomSheetDialog.dismiss();
        }
    }

    /** 顯示目前播放清單 */
    private void showCurrentPlayList() {
        mBottomSheetDialog = new BottomSheetDialog(this);
        RecyclerView recyclerView = new RecyclerView(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerViewDivideLineDecorator recyclerViewDivideLineDecorator = new RecyclerViewDivideLineDecorator(this);
        recyclerView.setAdapter(mCurrentPlayListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(recyclerViewDivideLineDecorator);
        mBottomSheetDialog.setContentView(recyclerView);
        mBottomSheetDialog.show();
    }

    /** 顯示等化器設定 */
    private void showEqList() {
        mBottomSheetDialog = new BottomSheetDialog(this);
        ListView listView = new ListView(this);
        final EqualizerType[] types = EqualizerType.values();
        final String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getValue();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MyApplication.getContext(), android.R.layout.simple_list_item_1, names);
        listView.setBackgroundColor(Color.BLACK);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(PlayMusicService.BUNDLE_KEY_EQUALIZER_TYPE, types[position]);
                mTransportControls.sendCustomAction(PlayMusicService.ACTION_CHANGE_EQUALIZER_TYPE,bundle);
                mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Snackbar.make(mBinding.rlRootActivityFullScreenPlayer, String.format(getString(R.string.equlizer_set_finish_toast), names[position]),Snackbar.LENGTH_SHORT).show();
                    }
                });
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog.setContentView(listView);
        mBottomSheetDialog.show();
    }

    private void onPressPlayButton() {
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
            mTransportControls.pause();
        } else {
            mBinding.ivPlayPauseActivityFullScreenPlayer.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
            mTransportControls.play();
        }
    }
}
