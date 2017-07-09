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
    protected boolean isFootViewVisible() {
        return true;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_u2b_search_playlist_adapter_listitem;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().tvSongArtistSonglistadapter.setText(mDataList.get(position).getSnippet().getTitle());
        holder.getBinding().tvSongTitleSonglistadapter.setText(mDataList.get(position).getSnippet().getDescriptionForList());
        holder.getBinding().ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        U2BPlayListDTO.ItemsBean.SnippetBean.ThumbnailsBean thumbnailsBean = mDataList.get(position).getSnippet().getThumbnails();
        String sUrl = null;
        if (thumbnailsBean != null) {
            //防呆，因為thumbnailsBean有可能為空
            sUrl = thumbnailsBean.getDefaultX().getUrl();
        }
        if (!TextUtils.isEmpty(sUrl)) {
            Glide.with(mContext).load(sUrl).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(holder.getBinding().ivSonglistadapter);
        }
    }
}
