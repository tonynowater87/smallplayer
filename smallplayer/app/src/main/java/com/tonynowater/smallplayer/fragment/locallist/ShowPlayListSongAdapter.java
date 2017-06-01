package com.tonynowater.smallplayer.fragment.locallist;

import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListSongAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonynowater on 2017/5/30.
 */

public class ShowPlayListSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutShowPlayListSongAdapterBinding> {
    private static final String TAG = ShowPlayListSongAdapter.class.getSimpleName();
    public ShowPlayListSongAdapter(int id, OnClickSomething<PlayListSongEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        mDataList = new RealmUtils().queryPlayListSongByListId(id);
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
}
