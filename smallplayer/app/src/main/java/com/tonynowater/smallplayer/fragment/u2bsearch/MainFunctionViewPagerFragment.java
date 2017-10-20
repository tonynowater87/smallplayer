package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutMainFunctionViewPagerFragmentBinding;

/**
 * Created by tonynowater on 2017/10/14.
 */

public class MainFunctionViewPagerFragment extends BaseFragment<LayoutMainFunctionViewPagerFragmentBinding> {

    public interface OnMainFunctionViewPagerFragmentCallback {
        void onPageSelected(int position);

        BaseViewPagerFragment[] getViewPagerItems();
    }

    private OnMainFunctionViewPagerFragmentCallback mOnMainFunctionViewPagerFragmentCallback;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_main_function_view_pager_fragment;
    }

    public ViewPager getViewPager() {
        return mBinding.viewpager;
    }

    public void setCallback(OnMainFunctionViewPagerFragmentCallback mOnMainFunctionViewPagerFragmentCallback) {
        this.mOnMainFunctionViewPagerFragmentCallback = mOnMainFunctionViewPagerFragmentCallback;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mOnMainFunctionViewPagerFragmentCallback != null) {
            mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {}

                @Override
                public void onPageSelected(int i) {
                    mOnMainFunctionViewPagerFragmentCallback.onPageSelected(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {}
            });
            mBinding.viewpager.setAdapter(new MyViewPagerAdapter(getFragmentManager(), mOnMainFunctionViewPagerFragmentCallback.getViewPagerItems()));
        }
    }

    public static class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        private BaseViewPagerFragment[] mBaseViewPagerFragments;

        public MyViewPagerAdapter(FragmentManager fm, BaseViewPagerFragment[] mBaseViewPagerFragments) {
            super(fm);
            this.mBaseViewPagerFragments = mBaseViewPagerFragments;
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
            BaseViewPagerFragment fragment = (BaseViewPagerFragment) super.instantiateItem(container, position);
            mBaseViewPagerFragments[position] = fragment;
            return fragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mBaseViewPagerFragments[position].getPageTitle();
        }
    }
}
