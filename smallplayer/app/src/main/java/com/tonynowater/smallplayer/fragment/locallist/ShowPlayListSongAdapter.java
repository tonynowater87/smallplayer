package com.tonynowater.smallplayer.fragment.locallist;

import android.util.Log;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListSongAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.PlayListDTO;
import com.tonynowater.smallplayer.module.dto.realm.PlayListSongDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;

import io.realm.Realm;

/**
 * Created by tonynowater on 2017/5/30.
 */

public class ShowPlayListSongAdapter extends BasePlayableFragmentAdapter<PlayListSongDTO, LayoutShowPlayListSongAdapterBinding> {
    private static final String TAG = ShowPlayListSongAdapter.class.getSimpleName();
    public ShowPlayListSongAdapter(int id, OnClickSomething<PlayListSongDTO> mOnClickSongListener) {
        super(mOnClickSongListener);
        PlayListDTO playListDTO = Realm.getDefaultInstance().where(PlayListDTO.class).equalTo("id", id).findFirst();
        if (playListDTO != null) {
            mDataList = playListDTO.getPlayListSong();
        }
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
