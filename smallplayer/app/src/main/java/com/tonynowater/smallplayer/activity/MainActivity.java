package com.tonynowater.smallplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.MainFunctionViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.Logger;
import com.tonynowater.smallplayer.util.PermissionGrantedUtil;
import com.tonynowater.smallplayer.view.SearchViewComponent;

import java.util.List;

// TODO: 2017/5/23 目前無法查YoutubleChannel
public class MainActivity extends BaseMediaControlActivity<ActivityMainBinding> implements MainFunctionViewPagerFragment.OnMainFunctionViewPagerFragmentInterface
, SearchViewComponent.OnSearchViewComponentCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int FLAG_PAGE_MAIN_FUNCTION = 0;
    public static final int FLAG_PAGE_SETTING = 1;
    //===== Fields =====
    private int m_iFlag = FLAG_PAGE_MAIN_FUNCTION;
    //===== Fields =====

    //===== Views =====
    private SearchViewComponent mSearchViewComponent;
    //===== Views =====

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Logger.getInstance().d(TAG, "onPlaybackStateChanged: " + state);
    }

    @Override
    protected void onSessionDestroyed() {
        Logger.getInstance().d(TAG, "onSessionDestroyed: ");
    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        Logger.getInstance().d(TAG, "onMetadataChanged: ");
    }

    @Override
    protected void onMediaServiceConnected() {}

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
        initialFab();
        changeFragment(m_iFlag);
    }

    /** 點擊飄浮按鈕 */
    private void initialFab() {
        mBinding.fab.setOnClickListener(view -> {
            switch (m_iFlag) {
                case FLAG_PAGE_MAIN_FUNCTION:
                    ((MainFunctionViewPagerFragment) getSupportFragmentManager()
                            .findFragmentByTag(MainFunctionViewPagerFragment.class.getSimpleName()))
                            .onClickFab();
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logger.getInstance().d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchViewComponent = new SearchViewComponent(this, menu, getComponentName());
        mSearchViewComponent.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Logger.getInstance().d(TAG, "onQueryTextSubmit: " + query);
                return false;//return false才能讓search執行預設搜尋 => 從onNewIntent處理Query的字串
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return mSearchViewComponent.onQueryTextChange(newText);
            }
        });

        mSearchViewComponent.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return mSearchViewComponent.onSuggestionClick(position);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                EditPlayListActivity.startActivity(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.getInstance().d(TAG, "onNewIntent:");
        setIntent(intent);//一定要先setIntent才能收到
        if (mSearchViewComponent != null) {
            // FIXME: 2017/10/21 因為OnNewIntent有可能會比OnCreateOptionMenu早執行，先暫時這樣處理防NPE
            mSearchViewComponent.onNewIntent(intent);
        } 
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    /**
     * 點擊畫面ListItem的事件
     * @param object 畫面List項目
     */
    @Override
    public void onClick(final Object object) {
        // TODO: 2017/5/30 這邊要在簡化
        final int currentPlayListId = mRealmUtils.queryCurrentPlayListID();
        if (object instanceof Song) {
            final Song song = ((Song) object);
            DialogUtil.showActionDialog(this, song.getmTitle(), R.array.local_action_list, (materialDialog, view, i, charSequence) -> {
                switch (i) {
                    case 0:
                        sendActionPlayingNow(currentPlayListId, mRealmUtils.addSongToPlayList(currentPlayListId, song.getPlayListSongEntity()));
                        break;
                    case 1:
                        DialogUtil.showSelectPlaylistDialog(MainActivity.this, song, mTransportControls);
                        break;
                }
            });
        } else if (object instanceof PlayListSongEntity) {
            final PlayListSongEntity playListSongEntity = ((PlayListSongEntity) object);
            if (playListSongEntity.isDeletedOrPrivatedVideo()) {
                return;
            }
            DialogUtil.showActionDialog(this, playListSongEntity.getTitle(), R.array.action_list, (materialDialog, view, i, charSequence) -> {
                switch (i) {
                    case 0:
                        sendActionPlayingNow(currentPlayListId, mRealmUtils.addSongToPlayList(currentPlayListId, playListSongEntity));
                        break;
                    case 1:
                        DialogUtil.showSelectPlaylistDialog(MainActivity.this, playListSongEntity, mTransportControls);
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
                                Logger.getInstance().d(TAG, "onSuccess: " + response);
                                showToast(response);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                Logger.getInstance().d(TAG, "onFailure: " + errorMsg);
                                showToast(errorMsg);
                            }
                        });
                        break;
                }
            });
        } else if (object instanceof U2BUserPlayListEntity) {
            invalidateOptionsMenu();//為了關閉SearchView
            U2BUserPlayListEntity playListEntity = ((U2BUserPlayListEntity) object);
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID, playListEntity.getPlaylistId());
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID, playListEntity.getPlaylistId());
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLIST_TITLE, playListEntity.getTitle());
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_IS_NEED_AUTH_TOKEN, playListEntity.isNeedAuthToken());
            startActivity(intent);
        }
    }

    public void changeFragment(int flag) {
        switch (flag) {
            case FLAG_PAGE_MAIN_FUNCTION:
                mBinding.fab.setVisibility(View.VISIBLE);
                initialViewPager();
                break;
            case FLAG_PAGE_SETTING:
                mBinding.fab.setVisibility(View.GONE);
                break;
        }
    }

    private void initialViewPager() {
        if (getSupportFragmentManager().findFragmentByTag(MainFunctionViewPagerFragment.class.getSimpleName()) == null) {
            MainFunctionViewPagerFragment mainFunctionViewPagerFragment = new MainFunctionViewPagerFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_content, mainFunctionViewPagerFragment, MainFunctionViewPagerFragment.class.getSimpleName())
                    .commit();
        }
        mBinding.toolbar.tabLayoutMainActivity.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageSelected(int position) {
        Logger.getInstance().d(TAG, "onPageSelected: " + position);
        mBinding.toolbar.appbarLayoutMainActivity.setExpanded(true, true);
        switch (position) {
            case MainFunctionViewPagerFragment.U2B_LIST_POSITION:
                mBinding.fab.setVisibility(View.GONE);
                break;
            case MainFunctionViewPagerFragment.U2B_USERLIST_POSITION:
                mBinding.fab.setVisibility(View.GONE);
                break;
            default:
                mBinding.fab.setVisibility(View.VISIBLE);
                break;
        }
        if (mSearchViewComponent != null) {
            mSearchViewComponent.setSearchViewVisible(position != MainFunctionViewPagerFragment.U2B_USERLIST_POSITION);
        }
    }

    @Override
    public TabLayout getTabLayout() {
        return mBinding.toolbar.tabLayoutMainActivity;
    }

    @Override
    public BaseViewPagerFragment getCurrentBaseViewPagerFragment() {
        switch (m_iFlag) {
            case FLAG_PAGE_MAIN_FUNCTION:
                return ((MainFunctionViewPagerFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainFunctionViewPagerFragment.class.getSimpleName()))
                        .getBaseViewPagerFragment();
        }
        return null;
    }

    @Override
    public BaseViewPagerFragment[] getCurrentBaseViewPagerFragments() {
        switch (m_iFlag) {
            case FLAG_PAGE_MAIN_FUNCTION:
                return ((MainFunctionViewPagerFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainFunctionViewPagerFragment.class.getSimpleName()))
                        .getBaseViewPagerFragments();
        }
        return null;
    }

    public void expandSearchView() {
        mSearchViewComponent.expandSearchView();
    }
}
