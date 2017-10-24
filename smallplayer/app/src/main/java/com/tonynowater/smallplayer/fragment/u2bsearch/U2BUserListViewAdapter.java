package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutU2bUserPlaylistAdapterBinding;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * 使用者Youtube播放清單畫面的Adapter
 * Created by tonynowater on 2017/10/3.
 */
public class U2BUserListViewAdapter extends BasePlayableFragmentAdapter<U2BUserPlayListEntity, LayoutU2bUserPlaylistAdapterBinding> {

    public U2BUserListViewAdapter(OnClickSomething<U2BUserPlayListEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_u2b_user_playlist_adapter;
    }

    @Override
    protected void onBindItem(LayoutU2bUserPlaylistAdapterBinding binding, U2BUserPlayListEntity item, int position) {
        binding.tvSongArtistSonglistadapter.setText(item.getTitle());
        binding.tvSongTitleSonglistadapter.setText(item.getChannelTitle());
        binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        if (!TextUtils.isEmpty(item.getArtUrl())) {
            Glide.with(mContext).load(item.getArtUrl()).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }
    }

    @Override
    protected boolean supportFooter() {
        return false;
    }
}
