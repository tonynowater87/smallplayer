package com.tonynowater.smallplayer.fragment.locallist;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.ItemTouchHelperAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.Collections;

/**
 * 播放清單Adapter
 * Created by tonynowater on 2017/5/29.
 */
public class ShowPlayListAdapter extends BasePlayableFragmentAdapter<PlayListEntity, LayoutShowPlayListAdapterBinding> implements ItemTouchHelperAdapter{

    private int[] mSongCountArray;
    private BaseMediaControlActivity mActivity;

    public ShowPlayListAdapter(BaseMediaControlActivity activity, OnClickSomething<PlayListEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        this.mActivity = activity;
        refreshData();
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.album;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_show_play_list_adapter;
    }

    @Override
    protected void onBindItem(LayoutShowPlayListAdapterBinding binding, PlayListEntity item, int position) {
        binding.tvPlaylistSongCount.setText(String.valueOf(mSongCountArray[position]));
    }

    @Override
    protected boolean supportFooter() {
        return false;
    }

    @Override
    public void onDismiss(int position) {
        final PlayListEntity playListEntity = mDataList.get(position);
        if (playListEntity.isDeletable()) {
            DialogUtil.showYesNoDialog(mContext, String.format(MyApplication.getContext().getString(R.string.delete_hint), playListEntity.getPlayListName()), (materialDialog, dialogAction) -> {
                switch (dialogAction) {
                    case POSITIVE:
                        if (playListEntity.getId() == realmUtils.queryCurrentPlayListID()) {
                            int defaultPlaylistId = realmUtils.queryAllPlayListSortByPosition().get(0).getId();
                            realmUtils.setCurrentPlayListID(defaultPlaylistId);
                            sendChangePlayListAction(defaultPlaylistId);
                        }
                        realmUtils.deletePlayList(playListEntity);
                        mDataList = realmUtils.queryAllPlayListSortByPosition();
                        notifyDataSetChanged();
                        break;
                }
            }, dialog -> notifyDataSetChanged());
        } else {
            DialogUtil.showMessageDialog(mContext, mContext.getString(R.string.normal_dialog_title),mContext.getString(R.string.default_list_can_not_delete));
            notifyDataSetChanged();
        }
    }

    private void sendChangePlayListAction(int defaultPlaylistId) {
        Bundle bundle = new Bundle();
        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, defaultPlaylistId);
        mActivity.sendActionToService(PlayMusicService.ACTION_CHANGE_PLAYLIST, bundle);
    }

    @Override
    public void onMove(int from, int to) {
        realmUtils.updatePlayListPosition(mDataList.get(from), mDataList.get(to));
        Collections.swap(mDataList, from, to);
        notifyItemMoved(from, to);
    }

    public void refreshData() {
        mDataList = realmUtils.queryAllPlayListSortByPosition();
        mSongCountArray = new int[mDataList.size()];
        for (int i = 0; i < mDataList.size(); i++) {
            mSongCountArray[i] = realmUtils.queryPlayListSongByListId(mDataList.get(i).getId()).size();
        }
        notifyDataSetChanged();
    }
}
