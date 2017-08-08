package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.module.dto.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.module.dto.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.module.u2b.Playable;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.module.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/1.
 */
public class U2BSearchViewPagerFragment extends BaseViewPagerFragment<LayoutU2bsearchfragmentBinding> {
    private static final String TAG = U2BSearchViewPagerFragment.class.getSimpleName();
    private static final String BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE";
    private static final String BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE";
    public static final String BUNDLE_KEY_PLAYLISTID = "BUNDLE_KEY_PLAYLISTID";

    private U2BVideoDTO mU2BVideoDTO;
    private U2BPlayListDTO mU2BPlayListDTO;
    private U2bPlayListVideoDTO mU2bPlayListVideoDTO;

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
                        initialVideoList(sResponse);
                        break;
                    case PLAYLIST:
                        initialPlayListDTO(sResponse);
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

        private void initialPlayListDTO(String sResponse) {
            if (mU2BPlayListDTO == null) {
                mU2BPlayListDTO = new Gson().fromJson(sResponse, U2BPlayListDTO.class);
            } else {
                U2BPlayListDTO u2BPlayListDTO = new Gson().fromJson(sResponse, U2BPlayListDTO.class);
                mU2BPlayListDTO.setNextPageToken(u2BPlayListDTO.getNextPageToken());
                mU2BPlayListDTO.getItems().addAll(mU2BPlayListDTO.getItems().size(), u2BPlayListDTO.getItems());
            }

            mSongListAdapter.setDataSource(mU2BPlayListDTO.getItems());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSongListAdapter.notifyDataSetChanged();
                    isLoad = false;
                    mBinding.lottieAnimationView.setVisibility(View.GONE);
                }
            });
        }

        private void initialPlayListVideoList(String sResponse) {
            StringBuilder sVideoIds;
            if (mU2bPlayListVideoDTO == null) {
                //首次加載
                mU2bPlayListVideoDTO = new Gson().fromJson(sResponse, U2bPlayListVideoDTO.class);
                sVideoIds = getQueryDurationOfPlayListVideoIds(mU2bPlayListVideoDTO.getItems());
            } else {
                //滑到底加載
                U2bPlayListVideoDTO u2bPlayListVideoDTO = new Gson().fromJson(sResponse, U2bPlayListVideoDTO.class);
                mU2bPlayListVideoDTO.setNextPageToken(u2bPlayListVideoDTO.getNextPageToken());
                mU2bPlayListVideoDTO.getItems().addAll(mU2bPlayListVideoDTO.getItems().size(), u2bPlayListVideoDTO.getItems());
                sVideoIds = getQueryDurationOfPlayListVideoIds(u2bPlayListVideoDTO.getItems());
            }

            U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), mDurationSearchCallback);
        }

        private void initialVideoList(String sResponse) {
            StringBuilder sVideoIds;
            if (mU2BVideoDTO == null) {
                //首次加載
                mU2BVideoDTO = new Gson().fromJson(sResponse, U2BVideoDTO.class);
                sVideoIds = getQueryDurationVideoIds(mU2BVideoDTO.getItems());
            } else {
                //滑到底加載
                U2BVideoDTO u2BVideoDTO = new Gson().fromJson(sResponse, U2BVideoDTO.class);
                mU2BVideoDTO.setNextPageToken(u2BVideoDTO.getNextPageToken());
                mU2BVideoDTO.getItems().addAll(mU2BVideoDTO.getItems().size(), u2BVideoDTO.getItems());
                sVideoIds = getQueryDurationVideoIds(u2BVideoDTO.getItems());
            }
            U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), mDurationSearchCallback);
        }

        private StringBuilder getQueryDurationVideoIds(List<U2BVideoDTO.ItemsBean> items) {
            StringBuilder sVideoIds = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                sVideoIds.append(items.get(i).getId().getVideoId());
                if (i < items.size() - 1) {
                    sVideoIds.append(",");
                }
            }
            return sVideoIds;
        }

        private StringBuilder getQueryDurationOfPlayListVideoIds(List<U2bPlayListVideoDTO.ItemsBean> items) {
            StringBuilder sVideoIds = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                sVideoIds.append(items.get(i).getSnippet().getResourceId().getVideoId());
                if (i < items.size() - 1) {
                    sVideoIds.append(",");
                }
            }
            return sVideoIds;
        }
    };

    private void showFailToast() {
        showToast(getString(R.string.u2b_query_failure));
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

        @Override
        public void onResponse(Response response) throws IOException {
            if (response.isSuccessful()) {
                String sResponse = response.body().string();
                Log.d(TAG, "onResponse body: " + sResponse);
                U2BVideoDurationDTO u2BVideoDurationDTO = new Gson().fromJson(sResponse, U2BVideoDurationDTO.class);
                U2BVideoDurationDTO.ItemsBean itemDuration;
                switch (mEnumU2BSearchType) {
                    case VIDEO:
                        HashMap<String, U2BVideoDTO.ItemsBean> hashMap = new HashMap<>();
                        // FIXME: 2017/6/14 搜尋到一半，在搜尋會 java.lang.NullPointerException: Attempt to invoke virtual method 'java.util.List com.tonynowater.smallplayer.module.dto.U2BVideoDTO.getItems()' on a null object reference
                        for (U2BVideoDTO.ItemsBean item : mU2BVideoDTO.getItems()) {
                            if (item.getVideoDuration() == -1) {
                                //沒Duration的才放
                                hashMap.put(item.getId().getVideoId(), item);
                            }
                        }

                        U2BVideoDTO.ItemsBean itemVideo;
                        for (int i = 0; i < u2BVideoDurationDTO.getItems().size(); i++) {
                            itemDuration = u2BVideoDurationDTO.getItems().get(i);
                            itemVideo = hashMap.get(itemDuration.getId());
                            if (itemVideo != null) {
                                itemVideo.setVideoDuration(U2BApiUtil.formateU2BDurationToMilionSecond(itemDuration.getContentDetails().getDuration()));
                            }
                        }

                        mSongListAdapter.setDataSource(mU2BVideoDTO.getItems());
                        break;
                    case PLAYLISTVIDEO:
                        HashMap<String, U2bPlayListVideoDTO.ItemsBean> hashMap2 = new HashMap<>();
                        for (U2bPlayListVideoDTO.ItemsBean item : mU2bPlayListVideoDTO.getItems()) {
                            if (item.getVideoDuration() == -1) {
                                //沒Duration的才放
                                hashMap2.put(item.getSnippet().getResourceId().getVideoId(), item);
                            }
                        }

                        U2bPlayListVideoDTO.ItemsBean itemVideo2;
                        for (int i = 0; i < u2BVideoDurationDTO.getItems().size(); i++) {
                            itemDuration = u2BVideoDurationDTO.getItems().get(i);
                            itemVideo2 = hashMap2.get(itemDuration.getId());
                            if (itemVideo2 != null) {
                                itemVideo2.setVideoDuration(U2BApiUtil.formateU2BDurationToMilionSecond(itemDuration.getContentDetails().getDuration()));
                            }
                        }

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

    public static U2BSearchViewPagerFragment newInstance(EnumU2BSearchType u2BSearchType, String playListVideoId) {
        U2BSearchViewPagerFragment fragment = new U2BSearchViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_SEARCH_TYPE, u2BSearchType);
        bundle.putString(BUNDLE_KEY_PLAYLISTID, playListVideoId);
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
                    mU2BVideoDTO = null;
                    U2BApi.newInstance().queryU2BVideo(query, mViedoSearchCallback);
                    break;
                case PLAYLIST:
                    Log.d(TAG, "queryBySearchView: search playlist" );
                    mU2BPlayListDTO = null;
                    U2BApi.newInstance().queryU2BPlayList(query, mViedoSearchCallback);
                    break;
                case PLAYLISTVIDEO:
                    Log.d(TAG, "queryBySearchView: search playlistvideo" );
                    mU2bPlayListVideoDTO = null;
                    U2BApi.newInstance().queryU2BPlayListVideo(query, mViedoSearchCallback);
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

        if (mU2BPlayListDTO != null) {
            //搜尋清單物件不可轉換成Playable
            Log.w(TAG, "搜尋清單物件不可轉換成Playable");
            return null;
        }

        return mSongListAdapter.getDataList();
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
                                if (TextUtils.isEmpty(mU2BVideoDTO.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    U2BApi.newInstance().queryU2BVideo(mQuery, mU2BVideoDTO.getNextPageToken(), mViedoSearchCallback);
                                    isLoad = true;
                                }
                                break;
                            case PLAYLIST:
                                if (TextUtils.isEmpty(mU2BPlayListDTO.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    U2BApi.newInstance().queryU2BPlayList(mQuery, mU2BPlayListDTO.getNextPageToken(), mViedoSearchCallback);
                                    isLoad = true;
                                }
                                break;
                            case PLAYLISTVIDEO:
                                if (TextUtils.isEmpty(mU2bPlayListVideoDTO.getNextPageToken())) {
                                    Log.d(TAG, "onScrollStateChanged: token null");
                                    mSongListAdapter.setFootviewVisible(false);
                                    mSongListAdapter.notifyItemChanged(mSongListAdapter.getItemCount());
                                } else {
                                    U2BApi.newInstance().queryU2BPlayListVideo(mQuery, mU2bPlayListVideoDTO.getNextPageToken(), mViedoSearchCallback);
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
}
