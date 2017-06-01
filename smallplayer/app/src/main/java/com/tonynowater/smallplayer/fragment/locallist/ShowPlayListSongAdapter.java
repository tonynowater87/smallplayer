package com.tonynowater.smallplayer.fragment.locallist;

import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.ItemTouchHelperAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListSongAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonynowater on 2017/5/30.
 */

public class ShowPlayListSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutShowPlayListSongAdapterBinding> implements ItemTouchHelperAdapter{
    private static final String TAG = ShowPlayListSongAdapter.class.getSimpleName();
    private int playListId;
    public ShowPlayListSongAdapter(int id, OnClickSomething<PlayListSongEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        playListId = id;
        mDataList = new RealmUtils().queryPlayListSongByListIdSortByPosition(id);
    }

    @Override
    protected boolean isFootviewVisible() {
        return false;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_show_play_list_song_adapter;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        String name = mDataList.get(position).getTitle();
        Log.d(TAG, "onBindItem: " + name);
        holder.getBinding().tvLayoutU2bsuggestionAdapterListItem.setText(name);
    }

    @Override
    public void onDismiss(int position) {
        realmUtils.deleteSongFromPlayList(mDataList.get(position));
        mDataList = new RealmUtils().queryPlayListSongByListIdSortByPosition(playListId);
        notifyDataSetChanged();
    }

    @Override
    public void onMove(int from, int to) {
        realmUtils.updatePlayListSongPosition(mDataList.get(from), mDataList.get(to));
        mDataList = realmUtils.queryPlayListSongByListIdSortByPosition(playListId);
        notifyDataSetChanged();
    }

}
