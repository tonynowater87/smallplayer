package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutU2bUserPlaylistFragmentBinding;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.util.BaseQueryArrayList;
import com.tonynowater.smallplayer.module.u2b.util.U2BQueryParamsItem;
import com.tonynowater.smallplayer.module.u2b.util.U2BUserListQueryArrayList;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.SPManager;
import com.tonynowater.smallplayer.util.google.GoogleLoginUtil;

import java.util.List;

/**
 * 使用者Youtube播放清單畫面
 * Created by tonynowater on 2017/10/3.
 */
// TODO: 2017/10/24 下滑更新
public class U2BUserListViewPagerFragment extends BaseViewPagerFragment<LayoutU2bUserPlaylistFragmentBinding> implements BaseQueryArrayList.IOnU2BQuery {

    private static final String TAG = U2BUserListViewPagerFragment.class.getSimpleName();

    private GoogleLoginUtil mGoogleLoginUtil;
    private int lastCompletelyVisibleItemPosition;
    private boolean isLoad = false;
    private U2BUserListQueryArrayList<U2BUserPlayListEntity> mAlU2BQuery;
    private BasePlayableFragmentAdapter mSongListAdapter;

    @Override
    public CharSequence getPageTitle() {
        return getArguments().getString(BUNDLE_KEY_TITLE);
    }

    @Override
    public void queryBySearchView(String query) {

    }

    @Override
    public List<? extends Playable> getPlayableList() {
        return null;
    }

    @Override
    public String getPlayableListName() {
        return null;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_u2b_user_playlist_fragment;
    }

    public static U2BUserListViewPagerFragment newInstance(String sTitle) {
        U2BUserListViewPagerFragment fragment = new U2BUserListViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_TITLE, sTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleLoginUtil.googleSignIn(U2BUserListViewPagerFragment.this);
            }
        });
        if (!SPManager.getInstance(MyApplication.getContext()).getIsGoogleLogin()) {
            mGoogleLoginUtil = new GoogleLoginUtil(getActivity(), this, new GoogleLoginUtil.OnGoogleLoginCallBack() {
                @Override
                public void onGoogleLoginSuccess(String authToken) {
                    U2BQueryParamsItem u2BQueryParamsItem = new U2BQueryParamsItem(EnumU2BSearchType.USER_LIST, "", true);
                    mAlU2BQuery = new U2BUserListQueryArrayList(u2BQueryParamsItem, U2BUserListViewPagerFragment.this);
                    SPManager.getInstance(getContext()).setAccessToken(authToken);
                    queryYoutubeUserPlaylist();
                }

                @Override
                public void onGoogleLoginFailure() {
                    showToast("Google 登入失敗");
                }
            });
            mBinding.googleSignInButton.setVisibility(View.VISIBLE);
        } else {
            queryYoutubeUserPlaylist();
        }
        initialU2BSearchAdapter();
    }

    private void queryYoutubeUserPlaylist() {
        mAlU2BQuery.query();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        mGoogleLoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    private void initialU2BSearchAdapter() {
        mSongListAdapter = new U2BUserListViewAdapter((OnClickSomething) getActivity());
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerviewU2bsearchfragment.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewU2bsearchfragment.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewU2bsearchfragment.setAdapter(mSongListAdapter);
        mBinding.recyclerviewU2bsearchfragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.d(TAG, "onScrollStateChanged: " + newState);
                super.onScrollStateChanged(recyclerView, newState);
                //滑到底部的時候做加載
                if (lastCompletelyVisibleItemPosition + 5 >= mSongListAdapter.getItemCount() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isLoad) {
                        // TODO: 2017/10/3 私人歌單加載再處理
//                        if (TextUtils.isEmpty(mU2BUserPlayListDTO.getNextPageToken())) {
//                            Log.d(TAG, "onScrollStateChanged: token null");
//                            mSongListAdapter.setFootviewVisible(false);
//                            mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
//                        } else {
//                            U2BApi.newInstance().queryU2BPlayList(mQuery, mU2BUserPlayListDTO.getNextPageToken(), mViedoSearchCallback);
//                            isLoad = true;
//                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                Log.d(TAG, "onScrolled: " + lastCompletelyVisibleItemPosition);
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleLoginUtil.onStop();
    }

    @Override
    public void onQuerySuccess() {
        mSongListAdapter.setDataSource(mAlU2BQuery);
        if (isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSongListAdapter.notifyDataSetChanged();
                    isLoad = false;
                    mBinding.lottieAnimationView.setVisibility(View.GONE);
                    mBinding.googleSignInButton.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onQueryFail(String errMsg) {
        if (!isDetached()) {
            showFailToast();
        }
    }

    private void showFailToast() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(getString(R.string.u2b_query_failure));
                mBinding.lottieAnimationView.setVisibility(View.GONE);
            }
        });
    }
}
