package com.tonynowater.smallplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityEditPlayListBinding;
import com.tonynowater.smallplayer.fragment.locallist.EditPlayListFragment;
import com.tonynowater.smallplayer.fragment.locallist.EnumEditListType;
import com.tonynowater.smallplayer.module.dto.realm.PlayListDTO;
import com.tonynowater.smallplayer.module.dto.realm.PlayListSongDTO;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.util.DialogUtil;

import java.util.List;

public class EditPlayListActivity extends BaseActivity<ActivityEditPlayListBinding> {
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
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children) {

    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children, Bundle options) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, EditPlayListFragment.newInstance(EnumEditListType.PlayList))
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_play_list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,getString(R.string.add_playlist));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // TODO: 2017/5/29 需要可以自訂新增歌單
            case 0:
                DialogUtil.showAddPlayListDialog(this);
                break;
        }

        return true;
    }

    @Override
    public void onClick(Object object) {
        if (object instanceof PlayListDTO) {
            PlayListDTO playListDTO = (PlayListDTO) object;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, EditPlayListFragment.newInstance(playListDTO.getId(),EnumEditListType.PlayListSongs))
                    .addToBackStack(null)
                    .commit();

        } else if (object instanceof PlayListSongDTO){
            PlayListSongDTO playListSongDTO = (PlayListSongDTO) object;
            RealmUtils.addSongToPlayList(RealmUtils.queryCurrentPlayListID(), playListSongDTO);
            sendMetaDataToService(RealmUtils.queryCurrentPlayListID());
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            fm.popBackStack();
        }
    }
}
