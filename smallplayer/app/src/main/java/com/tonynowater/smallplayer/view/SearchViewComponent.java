package com.tonynowater.smallplayer.view;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.GoogleSearchSuggestionProvider;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.module.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/10/20.
 */
public class SearchViewComponent {

    public interface OnSearchViewComponentCallback {
        BaseViewPagerFragment getCurrentBaseViewPagerFragment();
    }

    public static final String TAG = SearchViewComponent.class.getSimpleName();
    private static final int DEFAULT_SHOW_RECENT_SEARCH_RECORD_COUNT = 15;//最近搜尋記錄要顯示的筆數

    private SearchRecentSuggestions mSearchRecentSuggestions;
    private SearchView mSearchView;
    private CustomSearchAdapter mSimpleCursorAdapter;
    private Activity mActivity;
    private OnSearchViewComponentCallback mOnSearchViewComponentCallback;
    private List<String> mSuggestions;

    public SearchViewComponent(Activity activity, Menu menu, ComponentName componentName) {
        mActivity = activity;

        if (mActivity instanceof OnSearchViewComponentCallback) {
            mOnSearchViewComponentCallback = (OnSearchViewComponentCallback) activity;
        }

        SearchManager searchManager = (SearchManager) mActivity.getSystemService(Context.SEARCH_SERVICE);
        mSearchRecentSuggestions = new SearchRecentSuggestions(mActivity, GoogleSearchSuggestionProvider.AUTHORITY, GoogleSearchSuggestionProvider.MODE);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
    }

    public void setOnQueryTextListener(SearchView.OnQueryTextListener onQueryTextListener) {
        mSearchView.setOnQueryTextListener(onQueryTextListener);
    }

    public void setOnSuggestionListener(SearchView.OnSuggestionListener onSuggestionListener) {
        mSearchView.setOnSuggestionListener(onSuggestionListener);
    }

    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: " + newText);
        BaseViewPagerFragment baseViewPagerFragment = mOnSearchViewComponentCallback.getCurrentBaseViewPagerFragment();
        if (baseViewPagerFragment instanceof U2BSearchViewPagerFragment) {
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "suggesion response: " + sResponse);
                                    mSuggestions = U2BApiUtil.getSuggestionStringList(sResponse);
                                    if (MiscellaneousUtil.isListOK(mSuggestions)) {
                                        initialSearchViewSuggestAdapter(mSuggestions, false);
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                mSuggestions = getRecentSearchList(DEFAULT_SHOW_RECENT_SEARCH_RECORD_COUNT);
                initialSearchViewSuggestAdapter(mSuggestions, true);//改顯示歷史搜尋紀錄
            }
        } else if (baseViewPagerFragment instanceof SongListViewPagerFragment) {
            baseViewPagerFragment.queryBySearchView(newText);
        }

        return false;
    }

    /**
     * 取得SearchView的最近搜尋紀錄
     */
    private List<String> getRecentSearchList(int limit) {
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(GoogleSearchSuggestionProvider.AUTHORITY);

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        String[] selArgs = new String[]{null};

        if (limit > 0) {
            uriBuilder.appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT, String.valueOf(limit));
        }

        Uri uri = uriBuilder.build();

        List<String> listRecentSearch = new ArrayList<>();
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri, null, null, selArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                listRecentSearch.add(cursor.getString(SearchRecentSuggestions.QUERIES_PROJECTION_DISPLAY1_INDEX));
            }
            cursor.close();
        }
        return listRecentSearch;
    }

    private void initialSearchViewSuggestAdapter(List<String> suggestionStringList, boolean bAddFirstRow) {
        String[] columns = new String[]{BaseColumns._ID, CustomSearchAdapter.COLUMN_SUGGESTION};
        MatrixCursor matrixCursor = new MatrixCursor(columns);

        if (suggestionStringList.size() > 0 && bAddFirstRow) {
            suggestionStringList.add(0, mActivity.getString(R.string.search_bar_hint));
        }

        for (int i = 0; i < suggestionStringList.size(); i++) {
            matrixCursor.addRow(new Object[]{i, suggestionStringList.get(i)});
        }

        if (mSearchView.getSuggestionsAdapter() == null) {
            mSimpleCursorAdapter = new CustomSearchAdapter(mActivity, matrixCursor, true);
            mSearchView.setSuggestionsAdapter(mSimpleCursorAdapter);
        } else {
            mSearchView.getSuggestionsAdapter().changeCursor(matrixCursor);
        }
    }

    public boolean onSuggestionClick(int position) {
        if (MiscellaneousUtil.isListOK(mSuggestions)) {
            if (TextUtils.equals(mActivity.getString(R.string.search_bar_hint), mSuggestions.get(position))) {
                //若是點擊最近搜尋列，清空搜尋記錄
                DialogUtil.showYesNoDialog(mActivity, mActivity.getString(R.string.search_bar_clear_history_confirm_dialog_title), new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        switch (dialogAction) {
                            case POSITIVE:
                                mSearchRecentSuggestions.clearHistory();
                                mSearchView.setSuggestionsAdapter(null);
                                break;
                            case NEGATIVE:
                                break;
                        }
                    }
                });
            } else {
                mSearchRecentSuggestions.saveRecentQuery(mSuggestions.get(position), null);//儲存最近搜尋紀錄
                mSearchView.setQuery(mSuggestions.get(position), true);
            }
        }
        return true;
    }

    /**
     * 儲存最近搜尋紀錄
     * 指定Fragment進行查詢
     */
    public void onNewIntent(Intent intent) {
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_SEARCH)) {

            BaseViewPagerFragment baseViewPagerFragment = mOnSearchViewComponentCallback.getCurrentBaseViewPagerFragment();
            if (baseViewPagerFragment instanceof SongListViewPagerFragment)
                return;

            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "onNewIntent: " + query);
            baseViewPagerFragment.queryBySearchView(query);
            mSearchRecentSuggestions.saveRecentQuery(query, null);
            mSearchView.clearFocus();//防搜尋完鍵盤會彈起來
        }
    }
}


