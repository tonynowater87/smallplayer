package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutU2bSearchPlaylistAdapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.U2BPlayListDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * 搜尋清單Adapter
 * Created by tonynowater on 2017/5/20.
 */
public class U2BSearchPlayListFragmentAdapter extends BasePlayableFragmentAdapter<U2BPlayListDTO.ItemsBean, LayoutU2bSearchPlaylistAdapterListitemBinding> {
    private static final String TAG = U2BSearchPlayListFragmentAdapter.class.getSimpleName();

    public U2BSearchPlayListFragmentAdapter(OnClickSomething<U2BPlayListDTO.ItemsBean> mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_u2b_search_playlist_adapter_listitem;
    }

    @Override
    protected void onBindItem(LayoutU2bSearchPlaylistAdapterListitemBinding binding, U2BPlayListDTO.ItemsBean item, int position) {
        binding.tvSongArtistSonglistadapter.setText(item.snippet.title);
        binding.tvSongTitleSonglistadapter.setText(item.snippet.description);
        binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        U2BPlayListDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnailsBean = item.snippet.thumbnails;
        String sUrl = null;
        if (thumbnailsBean != null) {
            //防呆，因為thumbnailsBean有可能為空
            sUrl = thumbnailsBean.defaultX.url;
        }
        if (!TextUtils.isEmpty(sUrl)) {
            Glide.with(mContext).load(sUrl).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }
    }

    @Override
    protected boolean supportFooter() {
        return true;
    }
}
