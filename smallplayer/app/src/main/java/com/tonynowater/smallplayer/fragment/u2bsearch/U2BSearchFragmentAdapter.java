package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.TimeUtil;

/**
 * 搜尋音樂的列表
 * Created by tonyliao on 2017/5/1.
 */
public class U2BSearchFragmentAdapter extends BasePlayableFragmentAdapter<U2BVideoDTO.ItemsBean, LayoutSonglistadapterListitemBinding> {
    private static final String TAG = U2BSearchFragmentAdapter.class.getSimpleName();

    public U2BSearchFragmentAdapter(OnClickSomething<U2BVideoDTO.ItemsBean> mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_songlistadapter_listitem;
    }

    @Override
    protected void onBindItem(LayoutSonglistadapterListitemBinding binding, U2BVideoDTO.ItemsBean item, int position) {
        binding.tvSongArtistSonglistadapter.setText(item.getSnippet().getTitle());
        binding.tvSongTitleSonglistadapter.setText(item.getSnippet().getDescription());
        binding.tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration((int) item.getVideoDuration()));
        binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        U2BVideoDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnailsBean = item.getSnippet().getThumbnails();
        String sUrl = null;
        if (thumbnailsBean != null) {
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
        return true;
    }
}

