package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.support.annotation.NonNull;

import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutU2bSearchPlaylistAdapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * 搜尋清單Adapter
 * Created by tonynowater on 2017/5/20.
 */
public class U2BSearchPlayListAdapter extends BasePlayableFragmentAdapter<U2BUserPlayListEntity, LayoutU2bSearchPlaylistAdapterListitemBinding> {

    public U2BSearchPlayListAdapter(OnClickSomething<U2BUserPlayListEntity> mOnClickSongListener) {
        super(mOnClickSongListener, true);
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.song;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_u2b_search_playlist_adapter_listitem;
    }

    @Override
    protected void onBindItem(LayoutU2bSearchPlaylistAdapterListitemBinding binding, U2BUserPlayListEntity item, int position) { }

    @Override
    protected boolean supportFooter() {
        return true;
    }
}
