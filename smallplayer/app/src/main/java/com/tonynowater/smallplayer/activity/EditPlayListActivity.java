package com.tonynowater.smallplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.databinding.ActivityEditPlayListBinding;
import com.tonynowater.smallplayer.fragment.locallist.ShowPlayListAdapter;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

public class EditPlayListActivity extends BaseActivity<ActivityEditPlayListBinding> {

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerViewDivideLineDecorator recyclerViewDivideLineDecorator = new RecyclerViewDivideLineDecorator(getApplicationContext());
        mBinding.recyclerview.setLayoutManager(linearLayoutManager);
        mBinding.recyclerview.addItemDecoration(recyclerViewDivideLineDecorator);
        mBinding.recyclerview.setAdapter(new ShowPlayListAdapter(new OnClickSomething<Playable>() {
            @Override
            public void onClick(Playable playable) {

            }
        }));
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
}
