package com.tonynowater.smallplayer.base;

import android.databinding.ViewDataBinding;

import com.tonynowater.smallplayer.module.u2b.Playable;

import java.util.List;

/**
 * 主畫面ViewPager的基底類別
 * Created by tonyliao on 2017/5/1.
 */
public abstract class BaseViewPagerFragment<T extends ViewDataBinding> extends BaseFragment<T> {

    public static final String BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE";
    public static final String BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE";
    public static final String BUNDLE_KEY_PLAYLISTID = "BUNDLE_KEY_PLAYLISTID";
    public static final String BUNDLE_KEY_PLAYLIST_TITLE = "BUNDLE_KEY_PLAYLIST_TITLE";

    /** @return ViewPager的Tab標題文字 */
    public abstract CharSequence getPageTitle();

    /**
     * 供MainActivity搜尋ViewPager裡的List
     * @param query ViewPager搜尋
     */
    public abstract void queryBySearchView(String query);

    /**
     * @return ViewPager裡的List
     */
    public abstract List<? extends Playable> getPlayableList();

    /**
     * @return 要自動新增的播放清單名稱
     */
    public abstract String getPlayableListName();
}
