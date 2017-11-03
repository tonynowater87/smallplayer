package com.tonynowater.smallplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.ActivityPlayListBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.PermissionGrantedUtil;

import java.util.List;

/**
 * 顯示Youtube搜尋專輯裡的歌曲清單Activity
 */
public class PlayListActivity extends BaseMediaControlActivity<ActivityPlayListBinding> {
    private static final String TAG = PlayListActivity.class.getSimpleName();
    private BaseViewPagerFragment mU2BSearchViewPagerFragment;

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
    protected int getLayoutResourceId() {
        return R.layout.activity_play_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        setTitle(getIntent().getStringExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLIST_TITLE));
        mU2BSearchViewPagerFragment = U2BSearchViewPagerFragment.newInstance(EnumU2BSearchType.PLAYLISTVIDEO
                , getIntent().getStringExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID)
                , getIntent().getStringExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLIST_TITLE)
                , getIntent().getBooleanExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_IS_NEED_AUTH_TOKEN, false));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_playlist_video_activity, mU2BSearchViewPagerFragment).commit();
        initialFab();
    }

    /** 點擊飄浮按鈕 */
    private void initialFab() {
        mBinding.fab.setOnClickListener(view -> {
            List<Playable> playableList = mU2BSearchViewPagerFragment.getPlayableList();
            if (MiscellaneousUtil.isListOK(playableList)) {
                DialogUtil.showAddPlayableListDialog(PlayListActivity.this, playableList, mU2BSearchViewPagerFragment.getPlayableListName());
            } else {
                showToast(getString(R.string.add_playablelist_song_failed_toast_msg));
            }
        });
    }

    @Override
    public void onClick(final Object object) {
        if (object instanceof PlayListSongEntity) {
            final PlayListSongEntity playListSongEntity = ((PlayListSongEntity) object);

            if (playListSongEntity.isDeletedOrPrivatedVideo()) {
                return;
            }

            DialogUtil.showActionDialog(this, playListSongEntity.getTitle(), R.array.action_list, (materialDialog, view, i, charSequence) -> {
                switch (i) {
                    case 0:
                        int currentPlayListId = mRealmUtils.queryCurrentPlayListID();
                        sendActionPlayingNow(currentPlayListId, mRealmUtils.addSongToPlayList(currentPlayListId, playListSongEntity));
                        break;
                    case 1:
                        DialogUtil.showSelectPlaylistDialog(PlayListActivity.this, playListSongEntity, mTransportControls);
                        break;
                    case 2:
                        if (!PermissionGrantedUtil.isPermissionGranted(getApplicationContext(), PermissionGrantedUtil.REQUEST_PERMISSIONS)) {
                            showToast(getString(R.string.no_permission_warning_msg));
                            return;
                        }
                        showToast(String.format(getString(R.string.downloadMP3_start_msg), playListSongEntity.getTitle()));
                        U2BApi.newInstance().downloadMP3FromU2B(playListSongEntity, new U2BApi.OnMsgRequestCallback() {
                            @Override
                            public void onSuccess(String response) {
                                Log.d(TAG, "onSuccess: " + response);
                                showToast(response);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                Log.d(TAG, "onFailure: " + errorMsg);
                                showToast(errorMsg);
                            }
                        });
                        break;
                }
            });
        }
    }
}
