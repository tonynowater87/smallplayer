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
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.base.BaseU2BFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutU2bsearchfragmentBinding;
import com.tonynowater.smallplayer.u2b.U2BApi;
import com.tonynowater.smallplayer.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.u2b.U2BPlayListDTO;
import com.tonynowater.smallplayer.u2b.U2BVideoDTO;
import com.tonynowater.smallplayer.u2b.U2BVideoDuration;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType.VIDEO;

/**
 * Created by tonynowater on 2017/5/1.
 */
public class U2BSearchViewPagerFragment extends BaseViewPagerFragment<LayoutU2bsearchfragmentBinding> implements View.OnClickListener, OnClickSomething<String> {
    private static final String TAG = U2BSearchViewPagerFragment.class.getSimpleName();
    private static final String BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE";
    private static final String BUNDLE_KEY_SEARCH_TYPE = "BUNDLE_KEY_SEARCH_TYPE";

    private U2BVideoDTO mU2BVideoDTO;

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
                        // TODO: 2017/5/20
                        U2BPlayListDTO u2BPlayListDTO = new Gson().fromJson(sResponse, U2BPlayListDTO.class);
                        mSongListAdapter.setDataSource(u2BPlayListDTO.getItems());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSongListAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                    case CHANNEL:
                        break;
                }
            }
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
                U2BVideoDuration u2BVideoDuration = new Gson().fromJson(sResponse, U2BVideoDuration.class);

                HashMap<String, U2BVideoDTO.ItemsBean> hashMap = new HashMap<>();
                for (U2BVideoDTO.ItemsBean item : mU2BVideoDTO.getItems()) {
                    hashMap.put(item.getId().getVideoId(), item);
                }

                U2BVideoDuration.ItemsBean itemDuration;
                U2BVideoDTO.ItemsBean itemVideo;
                for (int i = 0; i < u2BVideoDuration.getItems().size(); i++) {
                    itemDuration = u2BVideoDuration.getItems().get(i);
                    itemVideo = hashMap.get(itemDuration.getId());
                    if (itemVideo != null) {
                        itemVideo.addVideoDuration(U2BApiUtil.formateU2BDurationToMilionSecond(itemDuration.getContentDetails().getDuration()));
                    }
                }

                mSongListAdapter.setDataSource(mU2BVideoDTO.getItems());
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
            if (getArguments().getSerializable(BUNDLE_KEY_SEARCH_TYPE) == VIDEO) {
                Log.d(TAG, "onClick: search video" );
                U2BApi.newInstance().queryU2BVideo(sInput, mViedoSearchCallback);
            } else if (getArguments().getSerializable(BUNDLE_KEY_SEARCH_TYPE) == EnumU2BSearchType.PLAYLIST) {
                Log.d(TAG, "onClick: search playlist" );
                U2BApi.newInstance().queryU2BPlayList(sInput, mViedoSearchCallback);
            } else if (getArguments().getSerializable(BUNDLE_KEY_SEARCH_TYPE) == EnumU2BSearchType.CHANNEL) {
                Log.d(TAG, "onClick: search channel" );
                U2BApi.newInstance().queryU2BChannel(sInput, mViedoSearchCallback);
            }

            mBinding.etQueryU2b.setText("");
        }
    }
}
