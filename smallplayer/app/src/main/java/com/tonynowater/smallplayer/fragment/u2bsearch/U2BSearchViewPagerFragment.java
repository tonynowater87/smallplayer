package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseU2BFragmentAdapter;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutU2bsearchfragmentBinding;
import com.tonynowater.smallplayer.u2b.U2BApi;
import com.tonynowater.smallplayer.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.u2b.U2BPlayListDTO;
import com.tonynowater.smallplayer.u2b.U2BVideoDTO;
import com.tonynowater.smallplayer.u2b.U2BVideoDurationDTO;
import com.tonynowater.smallplayer.u2b.U2bPlayListVideoDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/1.
 */
public class U2BSearchViewPagerFragment extends BaseViewPagerFragment<LayoutU2bsearchfragmentBinding> implements View.OnClickListener, OnClickSomething<String> {
    private static final String TAG = U2BSearchViewPagerFragment.class.getSimpleName();
    private static final String BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE";
    private static final String BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE";
    public static final String BUNDLE_KEY_PLAYLISTID = "BUNDLE_KEY_PLAYLISTID";

    private U2BVideoDTO mU2BVideoDTO;
    private U2bPlayListVideoDTO mU2bPlayListVideoDTO;

    private Callback mViedoSearchCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            mToastUtil.showToast(getString(R.string.u2b_query_failure));
        }

        @Override
        public void onResponse(Response response) throws IOException {
            mToastUtil.cancelToast();
            if (response.isSuccessful()) {
                String sResponse = response.body().string();
                Log.d(TAG, "onResponse body: " + sResponse);

                switch (mEnumU2BSearchType) {
                    case VIDEO:
                        initialVideoList(sResponse);
                        break;
                    case PLAYLIST:
                        U2BPlayListDTO u2BPlayListDTO = new Gson().fromJson(sResponse, U2BPlayListDTO.class);
                        mSongListAdapter.setDataSource(u2BPlayListDTO.getItems());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSongListAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    case PLAYLISTVIDEO:
                        initialPlayListVideoList(sResponse);
                        break;
                    case CHANNEL:
                        break;
                }
            }
        }

        private void initialPlayListVideoList(String sResponse) {
            mU2bPlayListVideoDTO = new Gson().fromJson(sResponse, U2bPlayListVideoDTO.class);
            StringBuilder sVideoIds = new StringBuilder();
            for (int i = 0; i < mU2bPlayListVideoDTO.getItems().size(); i++) {
                sVideoIds.append(mU2bPlayListVideoDTO.getItems().get(i).getSnippet().getResourceId().getVideoId());
                if (i < mU2bPlayListVideoDTO.getItems().size() - 1) {
                    sVideoIds.append(",");
                }
            }
            U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), mDurationSearchCallback);
        }

        private void initialVideoList(String sResponse) {
            mU2BVideoDTO = new Gson().fromJson(sResponse, U2BVideoDTO.class);
            StringBuilder sVideoIds = new StringBuilder();
            for (int i = 0; i < mU2BVideoDTO.getItems().size(); i++) {
                sVideoIds.append(mU2BVideoDTO.getItems().get(i).getId().getVideoId());
                if (i < mU2BVideoDTO.getItems().size() - 1) {
                    sVideoIds.append(",");
                }
            }
            U2BApi.newInstance().queryU2BVedioDuration(sVideoIds.toString(), mDurationSearchCallback);
        }
    };


    private Callback mDurationSearchCallback = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {}

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
                        for (U2BVideoDTO.ItemsBean item : mU2BVideoDTO.getItems()) {
                            hashMap.put(item.getId().getVideoId(), item);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSongListAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    case PLAYLISTVIDEO:
                        HashMap<String, U2bPlayListVideoDTO.ItemsBean> hashMap2 = new HashMap<>();
                        for (U2bPlayListVideoDTO.ItemsBean item : mU2bPlayListVideoDTO.getItems()) {
                            hashMap2.put(item.getSnippet().getResourceId().getVideoId(), item);
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSongListAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSongListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!TextUtils.isEmpty(s) && s.length() > 0) {

                if (!mIsRequesting) {
                    mIsRequesting = true;
                    U2BApi.newInstance().queryU2BSUGGESTION(s.toString(), new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            mBinding.recyclerviewSuggestion.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResponse(final Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String sResponse = new String(response.body().bytes());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initialU2BSuggest(U2BApiUtil.getSuggestionStringList(sResponse));
                                    }
                                });
                            }
                        }
                    });
                }
            } else {
                mBinding.recyclerviewSuggestion.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private BaseU2BFragmentAdapter mSongListAdapter;
    private boolean mIsRequesting = false;
    private EnumU2BSearchType mEnumU2BSearchType;

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
    protected int getResourceId() {
        return R.layout.layout_u2bsearchfragment;
    }

    @Override
    public CharSequence getPageTitle(Context context) {
        return getArguments().getString(BUNDLE_KEY_TITLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEnumU2BSearchType = (EnumU2BSearchType) getArguments().getSerializable(BUNDLE_KEY_SEARCH_TYPE);
        mBinding.btnQuerySubmit.setOnClickListener(this);
        mBinding.etQueryU2b.addTextChangedListener(mTextWatcher);
        initialU2BSearchAdapter();
        if (mEnumU2BSearchType == EnumU2BSearchType.PLAYLISTVIDEO) {
            searchU2B(getArguments().getString(BUNDLE_KEY_PLAYLISTID));
        }
    }

    private void initialU2BSuggest(List<String> suggests) {
        U2BSuggestAdapter suggestAdapter = new U2BSuggestAdapter(suggests, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerviewSuggestion.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewSuggestion.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewSuggestion.setAdapter(suggestAdapter);
        mBinding.recyclerviewSuggestion.setVisibility(View.VISIBLE);
        mIsRequesting = false;
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerviewU2bsearchfragment.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewU2bsearchfragment.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewU2bsearchfragment.setAdapter(mSongListAdapter);
    }

    /** 點擊確定 */
    @Override
    public void onClick(View v) {
        searchU2B(mBinding.etQueryU2b.getText().toString());
    }

    /** 點擊關鍵字 */
    @Override
    public void onClick(String s) {
        searchU2B(s);
    }

    private void searchU2B(String sInput) {
        if (!TextUtils.isEmpty(sInput)) {

            switch (mEnumU2BSearchType) {
                case VIDEO:
                    Log.d(TAG, "onClick: search video" );
                    U2BApi.newInstance().queryU2BVideo(sInput, mViedoSearchCallback);
                    break;
                case PLAYLIST:
                    Log.d(TAG, "onClick: search playlist" );
                    U2BApi.newInstance().queryU2BPlayList(sInput, mViedoSearchCallback);
                    break;
                case PLAYLISTVIDEO:
                    Log.d(TAG, "onClick: search playlistvideo" );
                    U2BApi.newInstance().queryU2BPlayListVideo(sInput, mViedoSearchCallback);
                    break;
                case CHANNEL:
                    Log.d(TAG, "onClick: search channel" );
                    U2BApi.newInstance().queryU2BChannel(sInput, mViedoSearchCallback);
                    break;
            }
            mBinding.etQueryU2b.setText("");
        }
    }
}
