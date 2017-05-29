package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseU2BFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.u2b.U2BApiUtil;
import com.tonynowater.smallplayer.module.dto.U2BVideoDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * Created by tonyliao on 2017/5/1.
 */
public class U2BSearchFragmentAdapter extends BaseU2BFragmentAdapter<U2BVideoDTO.ItemsBean, LayoutSonglistadapterListitemBinding> {
    private static final String TAG = U2BSearchFragmentAdapter.class.getSimpleName();

    public U2BSearchFragmentAdapter(OnClickSomething<Playable> mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_songlistadapter_listitem;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().tvSongArtistSonglistadapter.setText(mDataList.get(position).getSnippet().getTitle());
        holder.getBinding().tvSongTitleSonglistadapter.setText(mDataList.get(position).getSnippet().getDescription());
        holder.getBinding().tvDurationSonglistadapter.setText(U2BApiUtil.formateU2BDurationToString(mDataList.get(position).getVideoDuration()));
        U2BVideoDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnailsBean = mDataList.get(position).getSnippet().getThumbnails();
        String sUrl = null;
        if (thumbnailsBean != null) {
            sUrl = thumbnailsBean.getDefaultX().getUrl();
        }
        if (!TextUtils.isEmpty(sUrl)) {
            Glide.with(holder.getBinding().ivSonglistadapter.getContext()).load(sUrl).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(holder.getBinding().ivSonglistadapter.getContext()).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }
    }
}

