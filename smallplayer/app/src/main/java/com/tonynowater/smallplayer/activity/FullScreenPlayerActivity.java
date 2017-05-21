package com.tonynowater.smallplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityFullScreenPlayerBinding;
import com.tonynowater.smallplayer.u2b.U2BApiUtil;

public class FullScreenPlayerActivity extends BaseActivity<ActivityFullScreenPlayerBinding> {
    private static final String TAG = FullScreenPlayerActivity.class.getSimpleName();

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: ");
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
        mBinding.tvEndTextActivityFullScreenPlayer.setText(U2BApiUtil.formateU2BDurationToString(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_full_screen_player;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar((Toolbar) mBinding.rlRootActivityFullScreenPlayer.findViewById(R.id.toolbar_main_activity));
    }
}
