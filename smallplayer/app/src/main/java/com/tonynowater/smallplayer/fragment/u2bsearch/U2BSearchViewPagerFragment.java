package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutU2bsearchfragmentBinding;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.module.dto.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.module.u2b.util.BaseQueryArrayList;
import com.tonynowater.smallplayer.module.u2b.util.IOnU2BQuery;
import com.tonynowater.smallplayer.module.u2b.util.U2BPlayListQueryArray;
import com.tonynowater.smallplayer.module.u2b.util.U2BVideoQUeryArray;
import com.tonynowater.smallplayer.util.DateUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.SPManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/1.
 */
// TODO: 2017/10/23 搜尋完不會自動捲到最上面
public class U2BSearchViewPagerFragment extends BaseViewPagerFragment<LayoutU2bsearchfragmentBinding> implements IOnU2BQuery {
    private static final String TAG = U2BSearchViewPagerFragment.class.getSimpleName();

    private U2bPlayListVideoDTO mU2bPlayListVideoDTO;
    private U2BPlayListQueryArray<U2BUserPlayListEntity> u2BPlayListQueryArray;
    private U2BVideoQUeryArray<PlayListSongEntity> u2BVideoQUeryArray;

    private Callback mViedoSearchCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            if (!isDetached()) {
                showFailToast();
            }
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if (response.isSuccessful()) {
                String sResponse = response.body().string();
                Log.d(TAG, "onResponse body: " + sResponse);
                switch (mEnumU2BSearchType) {
                    case VIDEO:
//                        initialVideoList(sResponse);
                        break;
                    case PLAYLISTVIDEO:
                        initialPlayListVideoList(sResponse);
                        break;
                    case CHANNEL:
                        break;
                }
            } else {
                showFailToast();
            }
        }

        private void initialPlayListVideoList(String sResponse) {
            StringBuilder sVideoIds;
            if (mU2bPlayListVideoDTO == null) {
                //首次加載
                mU2bPlayListVideoDTO = new Gson().fromJson(sResponse, U2bPlayListVideoDTO.class);
                sVideoIds = MiscellaneousUtil.getVideoIdsForQueryDuration(mU2bPlayListVideoDTO.getItems());
            } else {
                //滑到底加載
                U2bPlayListVideoDTO u2bPlayListVideoDTO = new Gson().fromJson(sResponse, U2bPlayListVideoDTO.class);
                mU2bPlayListVideoDTO.setNextPageToken(u2bPlayListVideoDTO.getNextPageToken());
                mU2bPlayListVideoDTO.getItems().addAll(mU2bPlayListVideoDTO.getItems().size(), u2bPlayListVideoDTO.getItems());
                sVideoIds = MiscellaneousUtil.getVideoIdsForQueryDuration(u2bPlayListVideoDTO.getItems());
            }

            U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), mDurationSearchCallback);
        }

//        private void initialVideoList(String sResponse) {
//            StringBuilder sVideoIds;
//            if (mU2BVideoDTO == null) {
//                //首次加載
//                mU2BVideoDTO = new Gson().fromJson(sResponse, U2BVideoDTO.class);
//                sVideoIds = MiscellaneousUtil.getVideoIdsForQueryDuration(mU2BVideoDTO.getItems());
//            } else {
//                //滑到底加載
//                U2BVideoDTO u2BVideoDTO = new Gson().fromJson(sResponse, U2BVideoDTO.class);
//                mU2BVideoDTO.setNextPageToken(u2BVideoDTO.getNextPageToken());
//                mU2BVideoDTO.getItems().addAll(mU2BVideoDTO.getItems().size(), u2BVideoDTO.getItems());
//                sVideoIds = MiscellaneousUtil.getVideoIdsForQueryDuration(u2BVideoDTO.getItems());
//            }
//            U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), mDurationSearchCallback);
//        }
    };

    private void showFailToast() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(getString(R.string.u2b_query_failure));
                mBinding.lottieAnimationView.setVisibility(View.GONE);
            }
        });
    }


    private Callback mDurationSearchCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.lottieAnimationView.setVisibility(View.GONE);
                }
            });
        }

        // FIXME: 2017/6/14 搜尋到一半，在搜尋會 java.lang.NullPointerException: Attempt to invoke virtual method 'java.util.List com.tonynowater.smallplayer.module.dto.U2BVideoDTO.getItems()' on a null object reference
        @Override
        public void onResponse(Response response) throws IOException {
            if (response.isSuccessful()) {
                String sResponse = response.body().string();
                Log.d(TAG, "onResponse body: " + sResponse);
                U2BVideoDurationDTO u2BVideoDurationDTO = new Gson().fromJson(sResponse, U2BVideoDurationDTO.class);
                switch (mEnumU2BSearchType) {
                    case VIDEO:
//                        MiscellaneousUtil.processDuration(u2BVideoDurationDTO, mU2BVideoDTO.getItems());
//                        mSongListAdapter.setDataSource(mU2BVideoDTO.getItems());
                        break;
                    case PLAYLISTVIDEO:
                        MiscellaneousUtil.processDuration(u2BVideoDurationDTO, mU2bPlayListVideoDTO.getItems());
                        mSongListAdapter.setDataSource(mU2bPlayListVideoDTO.getItems());
                        break;
                }

                if (isAdded()) {
                    //防止還沒補完，退出當機
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSongListAdapter.notifyDataSetChanged();
                            isLoad = false;
                            mBinding.lottieAnimationView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    };

    private BasePlayableFragmentAdapter mSongListAdapter;
    private EnumU2BSearchType mEnumU2BSearchType;
    private int lastCompletelyVisibleItemPosition;
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
    public static U2BSearchViewPagerFragment newInstance(EnumU2BSearchType u2BSearchType, String playListVideoId, String playListTitle) {
        U2BSearchViewPagerFragment fragment = new U2BSearchViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_SEARCH_TYPE, u2BSearchType);
        bundle.putString(BUNDLE_KEY_PLAYLISTID, playListVideoId);
        bundle.putString(BUNDLE_KEY_PLAYLIST_TITLE, playListTitle);
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
            switch (mEnumU2BSearchType) {
                case VIDEO:
                    Log.d(TAG, "queryBySearchView: search video" );
                    if (u2BVideoQUeryArray == null) {
                        u2BVideoQUeryArray = new U2BVideoQUeryArray<>(this);
                    } else {
                        u2BVideoQUeryArray.clear();
                    }
                    u2BVideoQUeryArray.query(query);
                    break;
                case PLAYLIST:
                    Log.d(TAG, "queryBySearchView: search playlist" );
                    if (u2BPlayListQueryArray == null) {
                        u2BPlayListQueryArray = new U2BPlayListQueryArray<>(this);
                    } else {
                        u2BPlayListQueryArray.clear();
                    }
                    u2BPlayListQueryArray.query(query);
                    break;
                case PLAYLISTVIDEO:
                    Log.d(TAG, "queryBySearchView: search playlistvideo" );
                    mU2bPlayListVideoDTO = null;
                    U2BApi.newInstance().queryU2BPlayListVideo(query, SPManager.getInstance(getContext()).getAccessToken(), mViedoSearchCallback);
                    break;
                case CHANNEL:
                    Log.d(TAG, "queryBySearchView: search channel" );
                    U2BApi.newInstance().queryU2BChannel(query, mViedoSearchCallback);
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
        if (mEnumU2BSearchType == EnumU2BSearchType.PLAYLISTVIDEO) {
            queryBySearchView(getArguments().getString(BUNDLE_KEY_PLAYLISTID));
        }
    }

    private void initialU2BSearchAdapter() {
        switch (mEnumU2BSearchType) {
            case VIDEO:
                mSongListAdapter = new U2BSearchFragmentAdapter((OnClickSomething) getActivity());
                break;
            case PLAYLIST:
                mSongListAdapter = new U2BSearchPlayListFragmentAdapter((OnClickSomething) getActivity());
                break;
            case PLAYLISTVIDEO:
                mSongListAdapter = new U2BSearchPlayListVideoFragmentAdapter((OnClickSomething) getActivity());
                break;
            case CHANNEL:
                // TODO: 2017/5/20 not finished
                mSongListAdapter = new U2BSearchFragmentAdapter((OnClickSomething) getActivity());
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
                                    u2BVideoQUeryArray.queryNextPage();
                                    isLoad = true;
                                }
                                break;
                            case PLAYLIST:
                                if (TextUtils.isEmpty(u2BPlayListQueryArray.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    u2BPlayListQueryArray.queryNextPage();
                                    isLoad = true;
                                }
                                break;
                            case PLAYLISTVIDEO:
                                if (TextUtils.isEmpty(mU2bPlayListVideoDTO.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    U2BApi.newInstance().queryU2BPlayListVideo(mQuery, SPManager.getInstance(getContext()).getAccessToken(), mU2bPlayListVideoDTO.getNextPageToken(), mViedoSearchCallback);
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
            case PLAYLIST:
                baseQueryArrayList = u2BPlayListQueryArray;
                break;
        }

        mSongListAdapter.setDataSource(baseQueryArrayList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSongListAdapter.notifyDataSetChanged();
                isLoad = false;
                mBinding.lottieAnimationView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onQueryFail(String errMsg) {
        showFailToast();
    }
}
