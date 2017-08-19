package com.tonynowater.smallplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.databinding.ActivityEditPlayListBinding;
import com.tonynowater.smallplayer.fragment.locallist.EditPlayListFragment;
import com.tonynowater.smallplayer.fragment.locallist.EnumEditListType;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;

import java.util.List;

public class EditPlayListActivity extends BaseMediaControlActivity<ActivityEditPlayListBinding> {
    private static final String TAG = EditPlayListActivity.class.getSimpleName();

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, EditPlayListActivity.class);
        activity.startActivity(intent);
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        replaceShowPlayListFragment();
    }

    private void replaceShowPlayListFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, EditPlayListFragment.newInstance(EnumEditListType.PlayList))
                .commit();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_edit_play_list;
    }

    @Override
    public void onClick(final Object object) {
        if (object instanceof PlayListEntity) {
            PlayListEntity playListEntity = (PlayListEntity) object;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, EditPlayListFragment.newInstance(playListEntity.getId(),EnumEditListType.PlayListSongs))
                    .addToBackStack(null)
                    .commit();
            mBinding.toolbar.appbarLayoutMainActivity.setExpanded(true, true);//展開toolbar

        } else if (object instanceof PlayListSongEntity){
            final PlayListSongEntity playListSongEntity = ((PlayListSongEntity) object);
            DialogUtil.showActionDialog(this, playListSongEntity.getTitle(), R.array.action_list, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    switch (i) {
                        case 0:
                            mRealmUtils.addSongToPlayList(mRealmUtils.queryCurrentPlayListID(), playListSongEntity);
                            sendActionPlayingNow(mRealmUtils.queryCurrentPlayListID(), playListSongEntity);
                            break;
                        case 1:
                            DialogUtil.showSelectPlaylistDialog(EditPlayListActivity.this, playListSongEntity, mTransportControls);
                            break;
                        case 2:
                            showToast(String.format(getString(R.string.downloadMP3_start_msg), playListSongEntity.getTitle()));
                            U2BApi.newInstance().downloadMP3FromU2B(playListSongEntity, new U2BApi.OnU2BApiCallback() {
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
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            fm.popBackStack();
            mBinding.toolbar.appbarLayoutMainActivity.setExpanded(true, true);//展開toolbar
        }
    }
}
