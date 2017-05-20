package com.tonynowater.smallplayer.fragment.songlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutSonglistfragmentBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonyliao on 2017/4/27.
 */
public class SongListViewPagerFragment extends BaseViewPagerFragment<LayoutSonglistfragmentBinding> {
    private static final String TAG = SongListViewPagerFragment.class.getSimpleName();

    public static SongListViewPagerFragment newInstance() {
        return new SongListViewPagerFragment();
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
