package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.support.annotation.NonNull;

import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutU2bSearchPlaylistSongAdapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * U2B可播放的音樂列表
 * Created by tonyliao on 2017/5/1.
 */
public class U2BSearchPlaylistSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutU2bSearchPlaylistSongAdapterListitemBinding> {

    public U2BSearchPlaylistSongAdapter(OnClickSomething<PlayListSongEntity> mOnClickSongListener, boolean mFootviewIsVisible) {
        super(mOnClickSongListener, mFootviewIsVisible);
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.song;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_u2b_search_playlist_song_adapter_listitem;
    }

    @Override
    protected void onBindItem(LayoutU2bSearchPlaylistSongAdapterListitemBinding binding, PlayListSongEntity item, int position) {}

    @Override
    protected boolean supportFooter() {
        return true;
    }
}

