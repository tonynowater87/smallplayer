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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.GoogleSearchSuggestionProvider;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.u2b.U2BApi;
import com.tonynowater.smallplayer.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.YoutubeExtratorUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BaseViewPagerFragment[] mBaseViewPagerFragments;
    private int mCurrentViewPagerPosition = 0;
    private SimpleCursorAdapter simpleCursorAdapter;
    private List<String> suggestions;
    private SearchRecentSuggestions searchRecentSuggestions;

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
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children) {

    }

    @Override
    protected void onChildrenLoaded(List<MediaBrowserCompat.MediaItem> children, Bundle options) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mBinding.toolbar.toolbarMainActivity);
        initialViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchRecentSuggestions = new SearchRecentSuggestions(getApplicationContext(), GoogleSearchSuggestionProvider.AUTHORITY, GoogleSearchSuggestionProvider.MODE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
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
                if (!TextUtils.isEmpty(newText)) {
                    if (mBaseViewPagerFragments[mCurrentViewPagerPosition] instanceof U2BSearchViewPagerFragment) {
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
                                            suggestions =U2BApiUtil.getSuggestionStringList(sResponse);
                                            initialSearchViewSuggestAdapter(suggestions);
                                        }
                                    });
                                }
                            }
                        });
                    }
                } else {
                    suggestions = getRecentSearchList(10);
                   initialSearchViewSuggestAdapter(suggestions);//改顯示歷史搜尋紀錄
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

            private void initialSearchViewSuggestAdapter(List<String> suggestionStringList) {
                String [] columns = new String[] {BaseColumns._ID, "suggestion"};
                MatrixCursor matrixCursor = new MatrixCursor(columns);
                String[] from = new String[] {"suggestion"};
                int[] to = new int[] {R.id.tv_layout_u2bsuggestion_adapter_list_item};

                for (int i = 0; i < suggestionStringList.size(); i++) {
                    matrixCursor.addRow(new Object[] {i, suggestionStringList.get(i)});
                }

                simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext()
                        , R.layout.layout_u2bsuggestion_adapter_list_item
                        , matrixCursor
                        , from
                        , to
                        ,FLAG_REGISTER_CONTENT_OBSERVER);
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
                    searchRecentSuggestions.saveRecentQuery(suggestions.get(position), null);//儲存最近搜尋紀錄
                    searchView.setQuery(suggestions.get(position),true);
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
    protected int getLayoutResource() {
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBinding.toolbar.tabLayoutMainActivity.setVisibility(View.VISIBLE);
        mBinding.toolbar.tabLayoutMainActivity.setupWithViewPager(mBinding.viewpager);
    }

    @Override
    public void onClick(final Object object) {
        // TODO: 2017/5/30 這邊要在簡化
        final RealmUtils realmUtils = new RealmUtils();
        if (object instanceof Song) {
            DialogUtil.showActionDialog(this, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    Song song = ((Song) object);
                    if (i == 0) {
                        realmUtils.addSongToPlayList(realmUtils.queryCurrentPlayListID(), song.getPlayListSongEntity());
                        sendMetaDataToService(realmUtils.queryCurrentPlayListID());
                    } else {
                        DialogUtil.showSelectPlaylistDialog(MainActivity.this, song);
                    }
                    realmUtils.close();
                }
            });
        } else if (object instanceof U2BVideoDTO.ItemsBean) {
            DialogUtil.showActionDialog(this, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, final int i, CharSequence charSequence) {
                    final U2BVideoDTO.ItemsBean u2bVideoItem = ((U2BVideoDTO.ItemsBean) object);
                    new YoutubeExtratorUtil(getApplicationContext(), u2bVideoItem.getId().getVideoId(), new YoutubeExtratorUtil.CallBack() {
                        @Override
                        public void getU2BUrl(String url) {
                            u2bVideoItem.setDataSource(url);
                            if (i == 0) {
                                realmUtils.addSongToPlayList(realmUtils.queryCurrentPlayListID(), u2bVideoItem.getPlayListSongEntity());
                                sendMetaDataToService(realmUtils.queryCurrentPlayListID());
                            } else {
                                DialogUtil.showSelectPlaylistDialog(MainActivity.this, u2bVideoItem);
                            }
                            realmUtils.close();
                        }
                    }).execute();
                }
            });
        } else if (object instanceof U2BPlayListDTO.ItemsBean) {
            U2BPlayListDTO.ItemsBean u2bVideoItem = ((U2BPlayListDTO.ItemsBean) object);
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID, u2bVideoItem.getId().getPlaylistId());
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
            return mBaseViewPagerFragments[position].getPageTitle(MainActivity.this);
        }
    }
}
