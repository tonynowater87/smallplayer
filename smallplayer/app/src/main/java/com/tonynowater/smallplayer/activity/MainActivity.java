package com.tonynowater.smallplayer.activity;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.GoogleSearchSuggestionProvider;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.module.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.view.CustomSearchAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseMediaControlActivity<ActivityMainBinding> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCAL_POSITION = 0;
    private static final int U2B_VIDEO_POSITION = 1;
    private static final int U2B_LIST_POSITION = 2;
    private static final int DEFAULT_SHOW_RECENT_SEARCH_RECORD_COUNT = 15;//最近搜尋記錄要顯示的筆數
    private BaseViewPagerFragment[] mBaseViewPagerFragments;
    private int mCurrentViewPagerPosition = 0;
    private CustomSearchAdapter simpleCursorAdapter;
    private List<String> suggestions;
    private SearchRecentSuggestions searchRecentSuggestions;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SearchView searchView;

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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mBinding.drawerlayout, mBinding.toolbar.toolbarMainActivity, R.string.app_name, R.string.app_name);
        mActionBarDrawerToggle.syncState();
        initialFab();
        initialViewPager();
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
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchRecentSuggestions = new SearchRecentSuggestions(getApplicationContext(), GoogleSearchSuggestionProvider.AUTHORITY, GoogleSearchSuggestionProvider.MODE);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return false;//return false才能讓search執行預設搜尋 => 從onNewIntent處理Query的字串
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);

                if (mBaseViewPagerFragments[mCurrentViewPagerPosition] instanceof U2BSearchViewPagerFragment) {
                    if (!TextUtils.isEmpty(newText.trim())) {
                        U2BApi.newInstance().queryU2BSUGGESTION(newText, new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }

                            @Override
                            public void onResponse(final Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    final String sResponse = new String(response.body().bytes());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d(TAG, "suggesion response: " + sResponse);
                                            suggestions = U2BApiUtil.getSuggestionStringList(sResponse);
                                            if (MiscellaneousUtil.isListOK(suggestions)) {
                                                initialSearchViewSuggestAdapter(suggestions, false);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        suggestions = getRecentSearchList(DEFAULT_SHOW_RECENT_SEARCH_RECORD_COUNT);
                        initialSearchViewSuggestAdapter(suggestions, true);//改顯示歷史搜尋紀錄
                    }
                } else if (mBaseViewPagerFragments[mCurrentViewPagerPosition] instanceof SongListViewPagerFragment) {
                    mBaseViewPagerFragments[mCurrentViewPagerPosition].queryBySearchView(newText);
                }

                return false;
            }

            /** 取得SearchView的最近搜尋紀錄 */
            private List<String> getRecentSearchList(int limit) {
                Uri.Builder uriBuilder = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_CONTENT)
                        .authority(GoogleSearchSuggestionProvider.AUTHORITY);

                uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

                String[] selArgs = new String[] { null };

                if (limit > 0) {
                    uriBuilder.appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT, String.valueOf(limit));
                }

                Uri uri = uriBuilder.build();

                Cursor cursor = getContentResolver().query(uri, null, null, selArgs, null);

                List<String> listRecentSearch = new ArrayList<>();
                while (cursor.moveToNext()) {
                    listRecentSearch.add(cursor.getString(SearchRecentSuggestions.QUERIES_PROJECTION_DISPLAY1_INDEX));
                }

                return listRecentSearch;
            }

            private void initialSearchViewSuggestAdapter(List<String> suggestionStringList, boolean bAddFirstRow) {
                String [] columns = new String[] {BaseColumns._ID, CustomSearchAdapter.COLUMN_SUGGESTION};
                MatrixCursor matrixCursor = new MatrixCursor(columns);

                if (suggestionStringList.size() > 0 && bAddFirstRow) {
                    suggestionStringList.add(0,getString(R.string.search_bar_hint));
                }

                for (int i = 0; i < suggestionStringList.size(); i++) {
                    matrixCursor.addRow(new Object[] {i, suggestionStringList.get(i)});
                }

                simpleCursorAdapter = new CustomSearchAdapter(getApplicationContext(), matrixCursor, true);

                searchView.setSuggestionsAdapter(simpleCursorAdapter);
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                if (MiscellaneousUtil.isListOK(suggestions)) {
                    if (TextUtils.equals(getString(R.string.search_bar_hint), suggestions.get(position))) {
                        //若是點擊最近搜尋列，清空搜尋記錄
                        DialogUtil.showYesNoDialog(MainActivity.this, getString(R.string.search_bar_clear_history_confirm_dialog_title),new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                switch (dialogAction) {
                                    case POSITIVE:
                                        searchRecentSuggestions.clearHistory();
                                        searchView.setSuggestionsAdapter(null);
                                        break;
                                    case NEGATIVE:
                                        break;
                                }
                            }
                        });
                    } else {
                        searchRecentSuggestions.saveRecentQuery(suggestions.get(position), null);//儲存最近搜尋紀錄
                        searchView.setQuery(suggestions.get(position), true);
                    }
                }
                return true;
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
        setIntent(intent);//一定要先setIntent才能收到
        if (TextUtils.equals(getIntent().getAction(),Intent.ACTION_SEARCH)) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "onNewIntent: " + query);
            searchRecentSuggestions.saveRecentQuery(query, null);//儲存最近搜尋紀錄
            mBaseViewPagerFragments[mCurrentViewPagerPosition].queryBySearchView(query);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void initialViewPager() {
        mBaseViewPagerFragments = new BaseViewPagerFragment[]{SongListViewPagerFragment.newInstance()
                , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_video), EnumU2BSearchType.VIDEO)
                , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_playlist), EnumU2BSearchType.PLAYLIST)};
        // TODO: 2017/5/23 查頻道暫時先封住
        //, U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_channel), EnumU2BSearchType.CHANNEL)};
        mBinding.viewpager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                mCurrentViewPagerPosition = position;
                mBinding.toolbar.appbarLayoutMainActivity.setExpanded(true, true);
                if (position == U2B_LIST_POSITION) {
                    mBinding.fab.setVisibility(View.GONE);
                } else {
                    mBinding.fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBinding.toolbar.tabLayoutMainActivity.setVisibility(View.VISIBLE);
        mBinding.toolbar.tabLayoutMainActivity.setupWithViewPager(mBinding.viewpager);
    }

    /**
     * 點擊畫面ListItem的事件
     * @param object 畫面List項目
     */
    @Override
    public void onClick(final Object object) {
        // TODO: 2017/5/30 這邊要在簡化
        if (object instanceof Song) {
            final Song song = ((Song) object);
            DialogUtil.showActionDialog(this, song.getmTitle(), R.array.local_action_list, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    switch (i) {
                        case 0:
                            mRealmUtils.addSongToPlayList(mRealmUtils.queryCurrentPlayListID(), song.getPlayListSongEntity());
                            sendActionPlayingNow(mRealmUtils.queryCurrentPlayListID());
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
                            mRealmUtils.addSongToPlayList(mRealmUtils.queryCurrentPlayListID(), u2bVideoItem.getPlayListSongEntity());
                            sendActionPlayingNow(mRealmUtils.queryCurrentPlayListID());
                            break;
                        case 1:
                            DialogUtil.showSelectPlaylistDialog(MainActivity.this, u2bVideoItem, mTransportControls);
                            break;
                        case 2:
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
        }
    }

    private class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mBaseViewPagerFragments[position];
        }

        @Override
        public int getCount() {
            return mBaseViewPagerFragments.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseViewPagerFragment fragment = (BaseViewPagerFragment)super.instantiateItem(container, position);
            mBaseViewPagerFragments[position] = fragment;
            return fragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mBaseViewPagerFragments[position].getPageTitle();
        }
    }
}
