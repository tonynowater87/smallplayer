package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutMainFunctionViewPagerFragmentBinding;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.Logger;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.permission.SPermissionDefine;
import com.tonynowater.util.kotlin.activityRequest.SActivityRequestUtil;

import java.util.List;

/**
 * Created by tonynowater on 2017/10/14.
 */

public class MainFunctionViewPagerFragment extends BaseFragment<LayoutMainFunctionViewPagerFragmentBinding> {

    private static final String TAG = MainFunctionViewPagerFragment.class.getSimpleName();

    public interface OnMainFunctionViewPagerFragmentInterface {
        void onPageSelected(int position);
        TabLayout getTabLayout();
    }

    public static final int LOCAL_POSITION = 0;
    public static final int U2B_VIDEO_POSITION = 1;
    public static final int U2B_LIST_POSITION = 2;
    public static final int U2B_USERLIST_POSITION = 3;

    private int mCurrentViewPagerPosition = 0;
    private BaseViewPagerFragment[] mBaseViewPagerFragments;
    private OnMainFunctionViewPagerFragmentInterface mOnMainFunctionViewPagerFragmentInterface;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_main_function_view_pager_fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFunctionViewPagerFragmentInterface) {
            Logger.getInstance().d(TAG, "onAttach: ");
            mOnMainFunctionViewPagerFragmentInterface = (OnMainFunctionViewPagerFragmentInterface) context;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //請求權限
        new SActivityRequestUtil(getActivity()).requestPermission(new SActivityRequestUtil.OnAuthRequestCallback() {
            @Override
            public void onPermissionGranted() {
                Logger.getInstance().d(TAG, "onPermissionGranted: ");
                mBaseViewPagerFragments = new BaseViewPagerFragment[]{SongListViewPagerFragment.newInstance()
                        , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_video), EnumU2BSearchType.VIDEO)
                        , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_playlist), EnumU2BSearchType.PLAYLIST)
                        , U2BUserListViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_user_playlist))};
                initialView();
            }

            @Override
            public void onPermissionNotGranted() {
                Logger.getInstance().d(TAG, "onPermissionNotGranted: ");
                showToast(getString(R.string.no_permission_warning_msg));
                mBaseViewPagerFragments = new BaseViewPagerFragment[]{U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_video), EnumU2BSearchType.VIDEO)
                        , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_playlist), EnumU2BSearchType.PLAYLIST)
                        , U2BUserListViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_user_playlist))};
                initialView();
            }
        }, SPermissionDefine.REQUEST_PERMISSIONS);
    }

    private void initialView() {
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int position) {
                mCurrentViewPagerPosition = position;
                mOnMainFunctionViewPagerFragmentInterface.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
        mBinding.viewpager.setAdapter(new MyViewPagerAdapter(getFragmentManager(), mBaseViewPagerFragments));
        mOnMainFunctionViewPagerFragmentInterface.getTabLayout().setupWithViewPager(mBinding.viewpager);
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

    public void onClickFab() {
        List<Playable> playableList = mBaseViewPagerFragments[mCurrentViewPagerPosition].getPlayableList();
        if (MiscellaneousUtil.isListOK(playableList)) {
            DialogUtil.showAddPlayableListDialog((BaseMediaControlActivity) getActivity(), playableList, mBaseViewPagerFragments[mCurrentViewPagerPosition].getPlayableListName());
        } else {
            showToast(getString(R.string.add_playablelist_song_failed_toast_msg));
        }
    }

    public BaseViewPagerFragment[] getBaseViewPagerFragments() {
        BaseViewPagerFragment[] baseViewPagerFragments;
        if (mCurrentViewPagerPosition == LOCAL_POSITION) {
            baseViewPagerFragments = new BaseViewPagerFragment[2];
            baseViewPagerFragments[0] = mBaseViewPagerFragments[LOCAL_POSITION];
            baseViewPagerFragments[1] = mBaseViewPagerFragments[U2B_VIDEO_POSITION];
        } else if (mCurrentViewPagerPosition == mBaseViewPagerFragments.length - 1){
            baseViewPagerFragments = new BaseViewPagerFragment[0];
        } else {
            baseViewPagerFragments = new BaseViewPagerFragment[3];
            baseViewPagerFragments[0] = mBaseViewPagerFragments[mCurrentViewPagerPosition - 1];
            baseViewPagerFragments[1] = mBaseViewPagerFragments[mCurrentViewPagerPosition];
            baseViewPagerFragments[2] = mBaseViewPagerFragments[mCurrentViewPagerPosition + 1];
        }
        return baseViewPagerFragments;
    }

    public BaseViewPagerFragment getBaseViewPagerFragment() {
        return mBaseViewPagerFragments[mCurrentViewPagerPosition];
    }
}
