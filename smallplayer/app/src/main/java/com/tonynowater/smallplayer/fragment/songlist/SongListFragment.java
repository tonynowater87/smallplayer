package com.tonynowater.smallplayer.fragment.songlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.LayoutSonglistfragmentBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonyliao on 2017/4/27.
 */
public class SongListFragment extends BaseFragment <LayoutSonglistfragmentBinding> {
    private static final String TAG = SongListFragment.class.getSimpleName();

    public static SongListFragment newInstance() {
        return new SongListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SongListAdapter songListAdapter = new SongListAdapter(getActivity().getApplicationContext(), (OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mBinding.recyclerviewSonglistfragment.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewSonglistfragment.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewSonglistfragment.setAdapter(songListAdapter);
    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_songlistfragment;
    }

    @Override
    public CharSequence getPageTitle(Context context) {
        return context.getString(R.string.viewpager_title_local_music);
    }
}
