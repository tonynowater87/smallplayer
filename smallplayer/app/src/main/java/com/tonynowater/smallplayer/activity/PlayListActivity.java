package com.tonynowater.smallplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityPlayListBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.ToastUtil;

import java.util.List;

/**
 * 顯示Youtube搜尋專輯裡的歌曲清單Activity
 */
public class PlayListActivity extends BaseActivity<ActivityPlayListBinding> {
    private static final String TAG = PlayListActivity.class.getSimpleName();

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {

    }

    @Override
    protected void onSessionDestroyed() {

    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {

    }

    @Override
    protected void onMediaServiceConnected() {

    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children) {

    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children, Bundle options) {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_play_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        U2BSearchViewPagerFragment u2BSearchViewPagerFragment = U2BSearchViewPagerFragment.newInstance(EnumU2BSearchType.PLAYLISTVIDEO, getIntent().getStringExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_playlist_video_activity, u2BSearchViewPagerFragment).commit();
    }

    @Override
    public void onClick(final Object object) {
        if (object instanceof U2bPlayListVideoDTO.ItemsBean) {
            final U2bPlayListVideoDTO.ItemsBean u2bPlayListVideoItem = ((U2bPlayListVideoDTO.ItemsBean) object);
            DialogUtil.showActionDialog(this, u2bPlayListVideoItem.getPlayListSongEntity().getTitle(), new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, final int i, CharSequence charSequence) {
                    switch (i) {
                        case 0:
                            mRealmUtils.addSongToPlayList(mRealmUtils.queryCurrentPlayListID(), u2bPlayListVideoItem.getPlayListSongEntity());
                            sendActionPlayingNow(mRealmUtils.queryCurrentPlayListID());
                            break;
                        case 1:
                            DialogUtil.showSelectPlaylistDialog(PlayListActivity.this, u2bPlayListVideoItem, mTransportControls);
                            break;
                        case 2:
                            ToastUtil.showToast(PlayListActivity.this, String.format(getString(R.string.downloadMP3_start_msg), u2bPlayListVideoItem.getPlayListSongEntity().getTitle()));
                            U2BApi.newInstance().downloadMP3FromU2B(u2bPlayListVideoItem, new U2BApi.OnU2BApiCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    Log.d(TAG, "onSuccess: " + response);
                                    ToastUtil.showToast(PlayListActivity.this, response);
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    Log.d(TAG, "onFailure: " + errorMsg);
                                    ToastUtil.showToast(PlayListActivity.this, errorMsg);
                                }
                            });
                            break;
                    }
                }
            });
        }
    }
}
