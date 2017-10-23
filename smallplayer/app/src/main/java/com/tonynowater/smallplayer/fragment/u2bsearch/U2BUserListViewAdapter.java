package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutU2bUserPlaylistAdapterBinding;
import com.tonynowater.smallplayer.module.dto.U2BUserPlayListDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * 使用者Youtube播放清單畫面的Adapter
 * Created by tonynowater on 2017/10/3.
 */
public class U2BUserListViewAdapter extends BasePlayableFragmentAdapter<U2BUserPlayListDTO.ItemsBean, LayoutU2bUserPlaylistAdapterBinding> {

    public U2BUserListViewAdapter(OnClickSomething<U2BUserPlayListDTO.ItemsBean> mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_u2b_user_playlist_adapter;
    }

    @Override
    protected void onBindItem(LayoutU2bUserPlaylistAdapterBinding binding, U2BUserPlayListDTO.ItemsBean item, int position) {
        binding.tvSongArtistSonglistadapter.setText(item.getSnippet().getTitle());
        binding.tvSongTitleSonglistadapter.setText(item.getSnippet().getDescription());
        binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        U2BUserPlayListDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnailsBean = item.getSnippet().getThumbnails();
        String sUrl = null;
        if (thumbnailsBean != null) {
            //防呆，因為thumbnailsBean有可能為空
            sUrl = thumbnailsBean.getDefaultX().getUrl();
        }
        if (!TextUtils.isEmpty(sUrl)) {
            Glide.with(mContext).load(sUrl).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }
    }

    @Override
    protected boolean supportFooter() {
        return false;
    }
}
