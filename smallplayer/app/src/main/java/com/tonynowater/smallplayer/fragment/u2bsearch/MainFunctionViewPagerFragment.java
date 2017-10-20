package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.activity.MainActivity;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutMainFunctionViewPagerFragmentBinding;

/**
 * Created by tonynowater on 2017/10/14.
 */

public class MainFunctionViewPagerFragment extends BaseFragment<LayoutMainFunctionViewPagerFragmentBinding> {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_main_function_view_pager_fragment;
    }

    public ViewPager getViewPager() {
        return mBinding.viewpager;
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mBinding.viewpager.addOnPageChangeListener(onPageChangeListener);
    }

    public void setAdapter(MyViewPagerAdapter myViewPagerAdapter) {
        mBinding.viewpager.setAdapter(myViewPagerAdapter);
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
