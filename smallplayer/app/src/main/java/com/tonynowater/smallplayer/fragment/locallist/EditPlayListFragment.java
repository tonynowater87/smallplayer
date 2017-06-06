package com.tonynowater.smallplayer.fragment.locallist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.base.CustomItemTouchHelperCallback;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListFragmentBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.util.DialogUtil;
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

    public EditPlayListFragment() {
        this.setHasOptionsMenu(true);
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
                getActivity().setTitle(getString(R.string.title_edit_play_list));
                initialPlayListAdapter();
                break;
            case PlayListSongs:
                getActivity().setTitle(String.format(getString(R.string.title_edit_play_song, mRealmUtils.queryPlayListById(getArguments().getInt(BUNDLE_KEY_POSITION)).get(0).getPlayListName())));
                initialPlayListSongAdapter();
                break;
        }
    }

    /**
     * 播放清單裡歌曲的Adapter
     */
    private void initialPlayListSongAdapter() {
        ShowPlayListSongAdapter adapter = new ShowPlayListSongAdapter(getArguments().getInt(BUNDLE_KEY_POSITION), (OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mBinding.recyclerview.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerview.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerview.setAdapter(adapter);
        CustomItemTouchHelperCallback callback = new CustomItemTouchHelperCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.recyclerview);
    }

    /**
     * 播放清單的Adapter
     */
    private void initialPlayListAdapter() {
        ShowPlayListAdapter adapter = new ShowPlayListAdapter((OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mBinding.recyclerview.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerview.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerview.setAdapter(adapter);
        CustomItemTouchHelperCallback callback = new CustomItemTouchHelperCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.recyclerview);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mEnumType == EnumEditListType.PlayList) {
            inflater.inflate(R.menu.menu_edit_playlist, menu);
        } else {
            menu.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add:
                DialogUtil.showAddPlayListDialog(getContext(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        RealmUtils realmUtils = new RealmUtils();
                        realmUtils.addNewPlayList(charSequence.toString());
                        realmUtils.close();
                        initialPlayListAdapter();
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
