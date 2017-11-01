package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutU2bsearchfragmentBinding;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.util.BaseQueryArrayList;
import com.tonynowater.smallplayer.module.u2b.util.U2BPlayListQueryArray;
import com.tonynowater.smallplayer.module.u2b.util.U2BPlayListVideoQueryArray;
import com.tonynowater.smallplayer.module.u2b.util.U2BQueryParamsItem;
import com.tonynowater.smallplayer.module.u2b.util.U2BVideoQUeryArray;
import com.tonynowater.smallplayer.util.DateUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/1.
 */
// TODO: 2017/10/23 搜尋完不會自動捲到最上面
public class U2BSearchViewPagerFragment extends BaseViewPagerFragment<LayoutU2bsearchfragmentBinding> implements BaseQueryArrayList.IOnU2BQuery {
    private static final String TAG = U2BSearchViewPagerFragment.class.getSimpleName();
    private static final String BUNDLE_KEY_QUERY = "BUNDLE_KEY_QUERY";

    private U2BPlayListQueryArray<U2BUserPlayListEntity> u2BPlayListQueryArray;
    private U2BVideoQUeryArray<PlayListSongEntity> u2BVideoQUeryArray;
    private U2BPlayListVideoQueryArray<PlayListSongEntity> u2BPlayListVideoQueryArray;

    private BasePlayableFragmentAdapter mSongListAdapter;
    private EnumU2BSearchType mEnumU2BSearchType;
    private int lastCompletelyVisibleItemPosition;
    private boolean mIsNeedAuthToken = false;
    private boolean isLoad = false;
    private String mQuery;

    public static U2BSearchViewPagerFragment newInstance(String sTitle, EnumU2BSearchType u2BSearchType) {
        U2BSearchViewPagerFragment fragment = new U2BSearchViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TITLE, sTitle);
        bundle.putSerializable(BUNDLE_KEY_SEARCH_TYPE, u2BSearchType);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * for 搜尋歌單用的建構子
     * @param u2BSearchType
     * @param playListVideoId
     * @param playListTitle
     * @return
     */
    public static U2BSearchViewPagerFragment newInstance(EnumU2BSearchType u2BSearchType
            , String playListVideoId
            , String playListTitle
            , Boolean isNeedAuthToken) {
        U2BSearchViewPagerFragment fragment = new U2BSearchViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_SEARCH_TYPE, u2BSearchType);
        bundle.putString(BUNDLE_KEY_PLAYLISTID, playListVideoId);
        bundle.putString(BUNDLE_KEY_PLAYLIST_TITLE, playListTitle);
        bundle.putBoolean(BUNDLE_KEY_IS_NEED_AUTH_TOKEN, isNeedAuthToken);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_u2bsearchfragment;
    }

    @Override
    public CharSequence getPageTitle() {
        return getArguments().getString(BUNDLE_KEY_TITLE);
    }

    @Override
    public void queryBySearchView(String query) {
        if (!TextUtils.isEmpty(query)) {
            mQuery = query;
            U2BQueryParamsItem queryParamsItem = new U2BQueryParamsItem(mEnumU2BSearchType, query, mIsNeedAuthToken);
            switch (mEnumU2BSearchType) {
                case VIDEO:
                    Log.d(TAG, "queryBySearchView: search video" );
                    if (u2BVideoQUeryArray == null) {
                        u2BVideoQUeryArray = new U2BVideoQUeryArray<>(queryParamsItem, this);
                    } else {
                        u2BVideoQUeryArray.setKeyword(query);
                        u2BVideoQUeryArray.clear();
                    }
                    u2BVideoQUeryArray.query();
                    break;
                case PLAYLIST:
                    Log.d(TAG, "queryBySearchView: search playlist" );
                    if (u2BPlayListQueryArray == null) {
                        u2BPlayListQueryArray = new U2BPlayListQueryArray<>(queryParamsItem, this);
                    } else {
                        u2BPlayListQueryArray.setKeyword(query);
                        u2BPlayListQueryArray.clear();
                    }
                    u2BPlayListQueryArray.query();
                    break;
                case PLAYLISTVIDEO:
                    Log.d(TAG, "queryBySearchView: search playlistvideo" );
                    if (u2BPlayListVideoQueryArray == null) {
                        u2BPlayListVideoQueryArray = new U2BPlayListVideoQueryArray<>(queryParamsItem, this);
                    } else {
                        u2BPlayListVideoQueryArray.setKeyword(query);
                        u2BPlayListVideoQueryArray.clear();
                    }
                    u2BPlayListVideoQueryArray.query();
                    break;
            }

            mBinding.lottieAnimationView.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public List<? extends Playable> getPlayableList() {

        if (u2BPlayListQueryArray != null) {
            //搜尋清單物件不可轉換成Playable
            Log.w(TAG, "搜尋清單物件不可轉換成Playable");
            return null;
        }

        return mSongListAdapter.getDataList();
    }

    @Override
    public String getPlayableListName() {
        switch (mEnumU2BSearchType) {
            case VIDEO:
                return mQuery.concat(DateUtil.getCurrentDateFullFormate());
            case PLAYLISTVIDEO:
                return getArguments().getString(BUNDLE_KEY_PLAYLIST_TITLE).concat(DateUtil.getCurrentDateFullFormate());
        }

        return "錯誤的情況，不應該出現！";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEnumU2BSearchType = (EnumU2BSearchType) getArguments().getSerializable(BUNDLE_KEY_SEARCH_TYPE);
        initialU2BSearchAdapter();
        mIsNeedAuthToken = getArguments().getBoolean(BUNDLE_KEY_IS_NEED_AUTH_TOKEN);
        if (savedInstanceState != null) {
            queryBySearchView(savedInstanceState.getString(BUNDLE_KEY_QUERY));
        } else {
            if (mEnumU2BSearchType == EnumU2BSearchType.PLAYLISTVIDEO) {
                queryBySearchView(getArguments().getString(BUNDLE_KEY_PLAYLISTID));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(BUNDLE_KEY_QUERY, mQuery);
    }

    private void initialU2BSearchAdapter() {
        switch (mEnumU2BSearchType) {
            case VIDEO:
            case PLAYLISTVIDEO:
                mSongListAdapter = new U2BSearchFragmentAdapter((OnClickSomething) getActivity());
                break;
            case PLAYLIST:
                mSongListAdapter = new U2BSearchPlayListFragmentAdapter((OnClickSomething) getActivity());
                break;
        }
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
                        switch (mEnumU2BSearchType) {
                            case VIDEO:
                                if (TextUtils.isEmpty(u2BVideoQUeryArray.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    u2BVideoQUeryArray.query();
                                    isLoad = true;
                                }
                                break;
                            case PLAYLIST:
                                if (TextUtils.isEmpty(u2BPlayListQueryArray.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    u2BPlayListQueryArray.query();
                                    isLoad = true;
                                }
                                break;
                            case PLAYLISTVIDEO:
                                if (TextUtils.isEmpty(u2BPlayListVideoQueryArray.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    u2BPlayListVideoQueryArray.query();
                                    isLoad = true;
                                }
                                break;
                            default:
                                Log.d(TAG, "onScrollStateChanged : DoNothing " + mEnumU2BSearchType.name());
                        }
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
    public void onQuerySuccess() {

        BaseQueryArrayList baseQueryArrayList = null;
        switch (mEnumU2BSearchType) {
            case VIDEO:
                baseQueryArrayList = u2BVideoQUeryArray;
                break;
            case PLAYLISTVIDEO:
                baseQueryArrayList = u2BPlayListVideoQueryArray;
                break;
            case PLAYLIST:
                baseQueryArrayList = u2BPlayListQueryArray;
                break;
        }

        mSongListAdapter.setDataSource(baseQueryArrayList);
        if (isAdded()) {
            getActivity().runOnUiThread(() -> {
                mSongListAdapter.notifyDataSetChanged();
                isLoad = false;
                mBinding.lottieAnimationView.setVisibility(View.GONE);
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
        getActivity().runOnUiThread(() -> {
            showToast(getString(R.string.u2b_query_failure));
            mBinding.lottieAnimationView.setVisibility(View.GONE);
        });
    }
}
