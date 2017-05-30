package com.tonynowater.smallplayer.fragment.locallist;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutEditPlayListItemBinding;
import com.tonynowater.smallplayer.module.dto.realm.PlayListSongDTO;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.util.OnClickSomething;

import io.realm.Realm;

/**
 * Created by tonynowater on 2017/5/29.
 */

public class EditPlayListAdapter extends BasePlayableFragmentAdapter<PlayListSongDTO, LayoutEditPlayListItemBinding> {

    protected EditPlayListAdapter(OnClickSomething<PlayListSongDTO> mOnClickSongListener) {
        super(mOnClickSongListener);
        Realm.getDefaultInstance().where(PlayListSongDTO.class).findAll();
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_edit_play_list_item;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {

    }
}
