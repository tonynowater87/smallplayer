package com.tonynowater.smallplayer.fragment.locallist;

import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.PlayFolder;
import com.tonynowater.smallplayer.module.dto.realm.PlayListDTO;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.util.OnClickSomething;

import io.realm.Realm;

/**
 * Created by tonynowater on 2017/5/29.
 */
public class ShowPlayListAdapter extends BasePlayableFragmentAdapter<PlayListDTO, LayoutShowPlayListAdapterBinding>{
    private static final String TAG = ShowPlayListAdapter.class.getSimpleName();

    public ShowPlayListAdapter(OnClickSomething<Playable> mOnClickSongListener) {
        super(mOnClickSongListener);
        PlayFolder playFolder = Realm.getDefaultInstance().where(PlayFolder.class).equalTo("id", 0).findFirst();
        if (playFolder != null) {
            mDataList = Realm.getDefaultInstance().where(PlayFolder.class).equalTo("id", 0).findFirst().getPlayList();
        }
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
