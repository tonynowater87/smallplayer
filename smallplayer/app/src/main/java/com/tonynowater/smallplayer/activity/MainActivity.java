package com.tonynowater.smallplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.MainFunctionViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BUserListViewPagerFragment;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.PermissionGrantedUtil;
import com.tonynowater.smallplayer.view.SearchViewComponent;

import java.util.List;

// TODO: 2017/5/23 目前無法查YoutubleChannel
public class MainActivity extends BaseMediaControlActivity<ActivityMainBinding> {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int FLAG_PAGE_MAIN_FUNCTION = 0;
    public static final int FLAG_PAGE_SETTING = 1;
    //===== Fields =====
    private int m_iFlag = FLAG_PAGE_MAIN_FUNCTION;
    private BaseViewPagerFragment[] mBaseViewPagerFragments;
    private int mCurrentViewPagerPosition = 0;
    private PermissionGrantedUtil mPermissionUtil;
    //===== Fields =====

    //===== Views =====
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SearchViewComponent mSearchViewComponent;
    //===== Views =====

    private SearchViewComponent.OnSearchViewComponentCallback mOnSearchViewComponentCallback = new SearchViewComponent.OnSearchViewComponentCallback() {
        @Override
        public int getCurrentPagerPosition() {
            return mCurrentViewPagerPosition;
        }
    };

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: " + state);
    }

    @Override
    protected void onSessionDestroyed() {
        Log.d(TAG, "onSessionDestroyed: ");
    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataChanged: ");
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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mBinding.drawerlayout, mBinding.toolbar.toolbarMainActivity, R.string.app_name, R.string.app_name);
        mActionBarDrawerToggle.syncState();
        mPermissionUtil = new PermissionGrantedUtil(this, new PermissionGrantedUtil.CallBack() {

            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted: ");
                mBaseViewPagerFragments = new BaseViewPagerFragment[]{SongListViewPagerFragment.newInstance()
                        , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_video), EnumU2BSearchType.VIDEO)
                        , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_playlist), EnumU2BSearchType.PLAYLIST)
                        , U2BUserListViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_user_playlist))};
            }

            @Override
            public void onPermissionNotGranted() {
                Log.d(TAG, "onPermissionNotGranted: ");
                showToast(getString(R.string.no_permission_warning_msg));
                mBaseViewPagerFragments = new BaseViewPagerFragment[]{U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_video), EnumU2BSearchType.VIDEO)
                        , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_playlist), EnumU2BSearchType.PLAYLIST)};
            }
        });
        mPermissionUtil.checkPermissiion(PermissionGrantedUtil.REQUEST_PERMISSIONS);//獲取權限
        initialFab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeFragment(m_iFlag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /** 點擊飄浮按鈕 */
    private void initialFab() {
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Playable> playableList = mBaseViewPagerFragments[mCurrentViewPagerPosition].getPlayableList();
                if (MiscellaneousUtil.isListOK(playableList)) {
                    DialogUtil.showAddPlayableListDialog(MainActivity.this, playableList, mBaseViewPagerFragments[mCurrentViewPagerPosition].getPlayableListName());
                } else {
                    showToast(getString(R.string.add_playablelist_song_failed_toast_msg));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mSearchViewComponent = new SearchViewComponent(this, menu, mBaseViewPagerFragments, getComponentName(), mOnSearchViewComponentCallback);
        mSearchViewComponent.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
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
        Log.d(TAG, "onNewIntent:");
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
            DialogUtil.showActionDialog(this, song.getmTitle(), R.array.local_action_list, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    switch (i) {
                        case 0:
                            sendActionPlayingNow(currentPlayListId, mRealmUtils.addSongToPlayList(currentPlayListId, song.getPlayListSongEntity()));
                            break;
                        case 1:
                            DialogUtil.showSelectPlaylistDialog(MainActivity.this, song, mTransportControls);
                            break;
                    }
                }
            });
        } else if (object instanceof U2BVideoDTO.ItemsBean) {
            final U2BVideoDTO.ItemsBean u2bVideoItem = ((U2BVideoDTO.ItemsBean) object);
            DialogUtil.showActionDialog(this, u2bVideoItem.getPlayListSongEntity().getTitle(), R.array.action_list, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, final int i, CharSequence charSequence) {
                    switch (i) {
                        case 0:
                            sendActionPlayingNow(currentPlayListId, mRealmUtils.addSongToPlayList(currentPlayListId, u2bVideoItem.getPlayListSongEntity()));
                            break;
                        case 1:
                            DialogUtil.showSelectPlaylistDialog(MainActivity.this, u2bVideoItem, mTransportControls);
                            break;
                        case 2:
                            if (!PermissionGrantedUtil.isPermissionGranted(getApplicationContext(), PermissionGrantedUtil.REQUEST_PERMISSIONS)) {
                                showToast(getString(R.string.no_permission_warning_msg));
                                return;
                            }
                            showToast(String.format(getString(R.string.downloadMP3_start_msg), u2bVideoItem.getPlayListSongEntity().getTitle()));
                            U2BApi.newInstance().downloadMP3FromU2B(u2bVideoItem, new U2BApi.OnU2BApiCallback() {
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
        } else if (object instanceof U2BPlayListDTO.ItemsBean) {
            invalidateOptionsMenu();//為了關閉SearchView
            U2BPlayListDTO.ItemsBean u2bVideoItem = ((U2BPlayListDTO.ItemsBean) object);
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID, u2bVideoItem.getId().getPlaylistId());
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLIST_TITLE, u2bVideoItem.getSnippet().getTitle());
            startActivity(intent);
        } else if (object instanceof U2BUserPlayListDTO.ItemsBean) {
            invalidateOptionsMenu();//為了關閉SearchView
            U2BUserPlayListDTO.ItemsBean u2bVideoItem = ((U2BUserPlayListDTO.ItemsBean) object);
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID, u2bVideoItem.getId());
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLIST_TITLE, u2bVideoItem.getSnippet().getTitle());
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
            mainFunctionViewPagerFragment.setCallback(new MainFunctionViewPagerFragment.OnMainFunctionViewPagerFragmentCallback() {
                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "onPageSelected: " + position);
                    mCurrentViewPagerPosition = position;
                    mBinding.toolbar.appbarLayoutMainActivity.setExpanded(true, true);
                    if (position == MainFunctionViewPagerFragment.U2B_LIST_POSITION || position == MainFunctionViewPagerFragment.U2B_USERLIST_POSITION) {
                        mBinding.fab.setVisibility(View.GONE);
                    } else {
                        mBinding.fab.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public BaseViewPagerFragment[] getViewPagerItems() {
                    return mBaseViewPagerFragments;
                }
            });
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_content, mainFunctionViewPagerFragment, MainFunctionViewPagerFragment.class.getSimpleName())
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
            mBinding.toolbar.tabLayoutMainActivity.setupWithViewPager(mainFunctionViewPagerFragment.getViewPager());
        }
        mBinding.toolbar.tabLayoutMainActivity.setVisibility(View.VISIBLE);
    }
}
