package com.tonynowater.smallplayer.fragment.locallist;

import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonynowater on 2017/5/29.
 */
public class ShowPlayListAdapter extends BasePlayableFragmentAdapter<PlayListEntity, LayoutShowPlayListAdapterBinding>{
    private static final String TAG = ShowPlayListAdapter.class.getSimpleName();

    public ShowPlayListAdapter(OnClickSomething<PlayListEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        mDataList = new RealmUtils().queryAllPlayList();
    }

    @Override
    protected boolean isFootviewVisible() {
        return false;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_show_play_list_adapter;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        String name = mDataList.get(position).getPlayListName();
        Log.d(TAG, "onBindItem: " + name);
        holder.getBinding().tvLayoutU2bsuggestionAdapterListItem.setText(name);
    }
}
