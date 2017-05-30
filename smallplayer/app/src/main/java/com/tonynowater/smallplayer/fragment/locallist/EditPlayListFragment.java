package com.tonynowater.smallplayer.fragment.locallist;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListFragmentBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonynowater on 2017/5/30.
 */

public class EditPlayListFragment extends BaseFragment<LayoutShowPlayListFragmentBinding> {
    public static final String BUNDLE_KEY_ENUM_EDITLISTTYPE = "BUNDLE_KEY_ENUM_EDITLISTTYPE";
    private static final String BUNDLE_KEY_POSITION = "BUNDLE_KEY_POSITION";
    private EnumEditListType mEnumType;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_show_play_list_fragment;
    }

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {

    }

    @Override
    protected void onSessionDestroyed() {

    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {

    }

    public static EditPlayListFragment newInstance(EnumEditListType enumEditListType) {
        EditPlayListFragment editPlayListFragment = new EditPlayListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_ENUM_EDITLISTTYPE, enumEditListType);
        editPlayListFragment.setArguments(bundle);
        return editPlayListFragment;
    }

    public static EditPlayListFragment newInstance(int position, EnumEditListType enumEditListType) {
        EditPlayListFragment editPlayListFragment = new EditPlayListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_POSITION, position);
        bundle.putSerializable(BUNDLE_KEY_ENUM_EDITLISTTYPE, enumEditListType);
        editPlayListFragment.setArguments(bundle);
        return editPlayListFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEnumType = (EnumEditListType) getArguments().getSerializable(BUNDLE_KEY_ENUM_EDITLISTTYPE);
        switch (mEnumType) {
            case PlayList:
                initialPlayListAdapter();
                break;
            case PlayListSongs:
                initialPlayListSongAdapter();
                break;
        }
    }

    /**
     * 播放清單裡歌曲的Adapter
     */
    private void initialPlayListSongAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mBinding.recyclerview.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerview.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerview.setAdapter(new ShowPlayListSongAdapter(getArguments().getInt(BUNDLE_KEY_POSITION), (OnClickSomething) getActivity()));
    }

    /**
     * 播放清單的Adapter
     */
    private void initialPlayListAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mBinding.recyclerview.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerview.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerview.setAdapter(new ShowPlayListAdapter((OnClickSomething) getActivity()));
    }
}
