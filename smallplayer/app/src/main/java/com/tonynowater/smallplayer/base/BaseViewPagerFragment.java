package com.tonynowater.smallplayer.base;

import android.content.Context;
import android.databinding.ViewDataBinding;

/**
 * 主畫面ViewPager的基底類別
 * Created by tonyliao on 2017/5/1.
 */
public abstract class BaseViewPagerFragment<T extends ViewDataBinding> extends BaseFragment<T> {

    public abstract CharSequence getPageTitle(Context context);
    public abstract void queryBySearchView(String query);
}
