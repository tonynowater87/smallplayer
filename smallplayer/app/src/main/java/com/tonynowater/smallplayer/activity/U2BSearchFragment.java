package com.tonynowater.smallplayer.activity;

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
import com.tonynowater.myyoutubeapi.U2BApi;
import com.tonynowater.myyoutubeapi.U2BApiUtil;
import com.tonynowater.myyoutubeapi.U2BVideoDTO;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.LayoutU2bsearchfragmentBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.io.IOException;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/1.
 */
public class U2BSearchFragment extends BaseFragment<LayoutU2bsearchfragmentBinding> implements View.OnClickListener {
    private static final String TAG = U2BSearchFragment.class.getSimpleName();

    private Callback mCallBack = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            mToastUtil.showToast(getString(R.string.u2b_query_failure));
        }

        @Override
        public void onResponse(Response response) throws IOException {
            mToastUtil.cancelToast();
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                String sResponse = response.body().string();
                Log.d(TAG, "onResponse body: " + sResponse);
                U2BVideoDTO u2BVideoDTO = gson.fromJson(sResponse, U2BVideoDTO.class);
                mSongListAdapter.setDataSource(u2BVideoDTO.getItems());
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

            if (!TextUtils.isEmpty(s) && s.length() > 2) {

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

    private U2BSearchFragmentAdapter mSongListAdapter;
    private boolean mIsRequesting = false;

    public static U2BSearchFragment newInstance() {
        return new U2BSearchFragment();
    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_u2bsearchfragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.btnQuerySubmit.setOnClickListener(this);
        mBinding.etQueryU2b.addTextChangedListener(mTextWatcher);
        initialU2BViewSearch();

    }

    private void initialU2BSuggest(List<String> suggests) {
        U2BSuggestAdapter suggestAdapter = new U2BSuggestAdapter(suggests);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerviewSuggestion.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewSuggestion.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewSuggestion.setAdapter(suggestAdapter);
        mBinding.recyclerviewSuggestion.setVisibility(View.VISIBLE);
        mIsRequesting = false;
    }

    private void initialU2BViewSearch() {
        mSongListAdapter = new U2BSearchFragmentAdapter((OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerviewU2bsearchfragment.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewU2bsearchfragment.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewU2bsearchfragment.setAdapter(mSongListAdapter);
    }

    @Override
    public void onClick(View v) {
        String sInput = mBinding.etQueryU2b.getText().toString();

        if (!TextUtils.isEmpty(sInput)) {
            mToastUtil.showToast(getString(R.string.seaching));
            U2BApi.newInstance().queryU2BVideo(sInput, mCallBack);
        }
    }
}
