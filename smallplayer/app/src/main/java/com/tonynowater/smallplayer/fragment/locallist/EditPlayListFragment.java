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
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BaseMediaControlFragment;
import com.tonynowater.smallplayer.base.CustomItemTouchHelperCallback;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListFragmentBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonynowater on 2017/5/30.
 */
// TODO: 2017/10/7 未來可增加可選取的刪除模式
public class EditPlayListFragment extends BaseMediaControlFragment<LayoutShowPlayListFragmentBinding> {
    public static final String BUNDLE_KEY_ENUM_EDITLISTTYPE = "BUNDLE_KEY_ENUM_EDITLISTTYPE";
    private static final String BUNDLE_KEY_POSITION = "BUNDLE_KEY_POSITION";
    private EnumEditListType mEnumType;
    private int mId;
    private ShowPlayListAdapter mShowPlayListAdapter;
    private ShowPlayListSongAdapter mShowPlayListSongAdapter;

    @Override
    protected int getLayoutResourceId() {
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
        initialPlayListSongAdapter();
    }

    @Override
    protected void onFragmentMediaConnected() {

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
        mId = getArguments().getInt(BUNDLE_KEY_POSITION);
        switch (mEnumType) {
            case PlayList:
                getActivity().setTitle(getString(R.string.title_edit_play_list));
                initialPlayListAdapter();
                break;
            case PlayListSongs:
                getActivity().setTitle(String.format(getString(R.string.title_edit_play_song), mRealmUtils.queryPlayListById(mId).get(0).getPlayListName()));
                initialPlayListSongAdapter();
                break;
        }
    }

    /**
     * 播放清單裡歌曲的Adapter
     */
    private void initialPlayListSongAdapter() {
        mShowPlayListSongAdapter = new ShowPlayListSongAdapter((BaseMediaControlActivity) getActivity(), getArguments().getInt(BUNDLE_KEY_POSITION), (OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mBinding.recyclerview.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerview.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerview.setAdapter(mShowPlayListSongAdapter);
        CustomItemTouchHelperCallback callback = new CustomItemTouchHelperCallback(mShowPlayListSongAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.recyclerview);
    }

    /**
     * 播放清單的Adapter
     */
    private void initialPlayListAdapter() {
        mShowPlayListAdapter = new ShowPlayListAdapter((BaseMediaControlActivity) getActivity(), (OnClickSomething) getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mBinding.recyclerview.setLayoutManager(layoutManager);
        RecyclerViewDivideLineDecorator dividerItemDecoration = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerview.addItemDecoration(dividerItemDecoration);
        mBinding.recyclerview.setAdapter(mShowPlayListAdapter);
        CustomItemTouchHelperCallback callback = new CustomItemTouchHelperCallback(mShowPlayListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mBinding.recyclerview);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mEnumType == EnumEditListType.PlayList) {
            inflater.inflate(R.menu.menu_edit_playlist, menu);
        } else {
            inflater.inflate(R.menu.menu_edit_playlistsong, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final PlayListEntity playListEntity;
        switch (item.getItemId()) {
            case R.id.menu_add:
                DialogUtil.showInputDialog(getContext(), getString(R.string.add_play_list_dialog_title), getString(R.string.input_play_list_name_dialog_hint), (materialDialog, charSequence) -> {
                    mRealmUtils.addNewPlayList(charSequence.toString());
                    mShowPlayListAdapter.refreshData();
                });
                return true;
            case R.id.menu_edit_playlist_name:
                playListEntity = mRealmUtils.queryPlayListById(mId).get(0);
                DialogUtil.showInputDialog(getContext(), getString(R.string.edit_play_list_name_dialog_title), getString(R.string.input_play_list_name_dialog_hint), playListEntity.getPlayListName(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {
                        PlayListEntity playListEntity = mRealmUtils.queryPlayListById(mId).get(0);
                        playListEntity.setPlayListName(charSequence.toString());
                        mRealmUtils.updatePlayList(playListEntity);
                        getActivity().setTitle(String.format(getString(R.string.title_edit_play_song), charSequence.toString()));
                    }
                });
                return true;
            case R.id.menu_edit_playlist_delete_all:
                playListEntity = mRealmUtils.queryPlayListById(mId).get(0);
                DialogUtil.showYesNoDialog(getContext(), getString(R.string.delete_all_dialog_check_title), (materialDialog, dialogAction) -> {
                    switch (dialogAction) {
                        case POSITIVE:
                            Bundle bundle = new Bundle();
                            bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, playListEntity.getId());
                            mTransportControls.sendCustomAction(PlayMusicService.ACTION_DEL_ALL_SONGS_IN_PLAYLIST, bundle);
                            break;
                    }
                }, dialog -> {});
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
