package com.tonynowater.smallplayer.fragment.songlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.LayoutSonglistfragmentBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.util.MediaUtils;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonyliao on 2017/4/27.
 */
public class SongListViewPagerFragment extends BaseViewPagerFragment<LayoutSonglistfragmentBinding> {
    private static final String TAG = SongListViewPagerFragment.class.getSimpleName();
    private static final int MENU_ID_REFRESH = 111;
    private static final int MENU_ORDER_NUMBER = 300;//和menu_main裡的orderInCategory相比，越大越靠右邊。
    private SongListAdapter mSongListAdapter;

    public static SongListViewPagerFragment newInstance() {
        return new SongListViewPagerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSongListAdapter = new SongListAdapter((OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mBinding.recyclerviewSonglistfragment.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewSonglistfragment.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerviewSonglistfragment.setAdapter(mSongListAdapter);
    }

    @Override
    protected int getResourceId() {
        return R.layout.layout_songlistfragment;
    }

    @Override
    public CharSequence getPageTitle(Context context) {
        return context.getString(R.string.viewpager_title_local_music);
    }

    @Override
    public void queryBySearchView(String query) {
        mSongListAdapter.getFilter().filter(query);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_ID_REFRESH, MENU_ORDER_NUMBER, R.string.refresh)
                .setIcon(android.R.drawable.ic_menu_rotate)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_REFRESH:
                mSongListAdapter.setDataSource(MediaUtils.getSongList(MyApplication.getContext()));
                mSongListAdapter.notifyDataSetChanged();
                return true;
        }

        return false;
    }
}
