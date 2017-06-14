package com.tonynowater.smallplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityPlayListBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.util.DialogUtil;

import java.util.List;

/**
 * 顯示Youtube搜尋專輯裡的歌曲清單Activity
 */
public class PlayListActivity extends BaseActivity<ActivityPlayListBinding> {

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
                    if (i == 0) {
                        RealmUtils realmUtils = new RealmUtils();
                        realmUtils.addSongToPlayList(realmUtils.queryCurrentPlayListID(), u2bPlayListVideoItem.getPlayListSongEntity());
                        sendActionPlayingNow(realmUtils.queryCurrentPlayListID());
                        realmUtils.close();
                    } else {
                        DialogUtil.showSelectPlaylistDialog(PlayListActivity.this, u2bPlayListVideoItem, mTransportControls);
                    }
                }
            });
        }
    }
}
