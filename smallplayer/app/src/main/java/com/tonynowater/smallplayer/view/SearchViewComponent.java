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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.module.GoogleSearchSuggestionProvider;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.Logger;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/10/20.
 */
public class SearchViewComponent {

    public interface OnSearchViewComponentCallback {
        BaseViewPagerFragment getCurrentBaseViewPagerFragment();
        BaseViewPagerFragment[] getCurrentBaseViewPagerFragments();
    }

    public static final String TAG = SearchViewComponent.class.getSimpleName();
    private static final int DEFAULT_SHOW_RECENT_SEARCH_RECORD_COUNT = 15;//最近搜尋記錄要顯示的筆數

    private SearchRecentSuggestions mSearchRecentSuggestions;
    private SearchView mSearchView;
    private Menu mMenu;
    private CustomSearchAdapter mSimpleCursorAdapter;
    private Activity mActivity;
    private OnSearchViewComponentCallback mOnSearchViewComponentCallback;
    private List<String> mSuggestions;
    private String mCurrentQueryText = "";

    public SearchViewComponent(Activity activity, Menu menu, ComponentName componentName) {
        mActivity = activity;
        mMenu = menu;

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
        Logger.getInstance().d(TAG, "onQueryTextChange: " + newText);
        BaseViewPagerFragment baseViewPagerFragment = mOnSearchViewComponentCallback.getCurrentBaseViewPagerFragment();
        if (baseViewPagerFragment instanceof U2BSearchViewPagerFragment) {
            mCurrentQueryText = newText;
            if (!TextUtils.isEmpty(newText.trim())) {
                U2BApi.newInstance().queryU2BSuggestion(newText, new U2BApi.OnListResponseCallback<String>() {
                    @Override
                    public void onSuccess(List<String> response) {
                        mActivity.runOnUiThread(() -> {
                            if (TextUtils.equals(mCurrentQueryText, newText)) {
                                //check query text response is same with the newest query text
                                mSuggestions = response;
                                initialSearchViewSuggestAdapter(response, false);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMsg) {}
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
            mSimpleCursorAdapter.setImageIcon(bAddFirstRow ? R.drawable.ic_restore_white_36dp : R.drawable.ic_search_white_36dp);
            mSearchView.setSuggestionsAdapter(mSimpleCursorAdapter);
        } else {
            ((CustomSearchAdapter) mSearchView.getSuggestionsAdapter()).setImageIcon(bAddFirstRow ? R.drawable.ic_restore_white_36dp : R.drawable.ic_search_white_36dp);
            mSearchView.getSuggestionsAdapter().changeCursor(matrixCursor);
        }
    }

    public boolean onSuggestionClick(int position) {
        if (MiscellaneousUtil.isListOK(mSuggestions)) {
            if (TextUtils.equals(mActivity.getString(R.string.search_bar_hint), mSuggestions.get(position))) {
                //若是點擊最近搜尋列，清空搜尋記錄
                DialogUtil.showYesNoDialog(mActivity, mActivity.getString(R.string.search_bar_clear_history_confirm_dialog_title), (materialDialog, dialogAction) -> {
                    switch (dialogAction) {
                        case POSITIVE:
                            mSearchRecentSuggestions.clearHistory();
                            mSearchView.setSuggestionsAdapter(null);
                            break;
                        case NEGATIVE:
                            break;
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

            BaseViewPagerFragment[] baseViewPagerFragments = mOnSearchViewComponentCallback.getCurrentBaseViewPagerFragments();

            for (int i = 0; i < baseViewPagerFragments.length; i++) {
                if (baseViewPagerFragments[i] instanceof SongListViewPagerFragment)
                    continue;
                String query = intent.getStringExtra(SearchManager.QUERY);
                Logger.getInstance().d(TAG, "onNewIntent query: " + query);
                baseViewPagerFragments[i].queryBySearchView(query);
                mSearchRecentSuggestions.saveRecentQuery(query, null);
                mSearchView.clearFocus();//防搜尋完鍵盤會彈起來
            }
        }
    }

    /**
     * 展開SearchView
     */
    public void expandSearchView() {
        mMenu.findItem(R.id.menu_search).expandActionView();
        mMenu.findItem(R.id.menu_search).getActionView().requestFocus();
    }

    public void setSearchViewVisible(boolean visible) {
        mMenu.findItem(R.id.menu_search).setVisible(visible);
    }
}


