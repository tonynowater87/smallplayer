package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutSonglistfragmentBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonyliao on 2017/4/27.
 */

public class SongListFragment extends android.support.v4.app.Fragment {
    private static final String TAG = SongListFragment.class.getSimpleName();
    private LayoutSonglistfragmentBinding mBinding;

    public static SongListFragment newInstance() {
        return new SongListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_songlistfragment, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SongListAdapter songListAdapter = new SongListAdapter(getActivity().getApplicationContext(), (OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mBinding.recyclerviewSonglistfragment.setLayoutManager(layoutManager);
        mBinding.recyclerviewSonglistfragment.setAdapter(songListAdapter);
    }
}
