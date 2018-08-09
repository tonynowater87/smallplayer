package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
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
    protected void onBindItem(LayoutU2bSearchPlaylistSongAdapterListitemBinding binding, PlayListSongEntity item, int position) {
        if (!TextUtils.isEmpty(item.getAlbumArtUri())) {
            Glide.with(mContext).load(item.getAlbumArtUri()).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }
    }

    @Override
    protected boolean supportFooter() {
        return true;
    }
}

