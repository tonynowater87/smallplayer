package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.TimeUtil;

/**
 * U2B可播放的音樂列表
 * Created by tonyliao on 2017/5/1.
 */
public class U2BSearchPlaylistSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutSonglistadapterListitemBinding> {
    private static final String TAG = U2BSearchPlaylistSongAdapter.class.getSimpleName();

    public U2BSearchPlaylistSongAdapter(OnClickSomething<PlayListSongEntity> mOnClickSongListener, boolean mFootviewIsVisible) {
        super(mOnClickSongListener, mFootviewIsVisible);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_songlistadapter_listitem;
    }

    @Override
    protected void onBindItem(LayoutSonglistadapterListitemBinding binding, PlayListSongEntity item, int position) {
        binding.tvSongArtistSonglistadapter.setText(item.getTitle());
        binding.tvSongTitleSonglistadapter.setText(item.getArtist());
        binding.tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration(item.getDuration()));
        binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        String sUrl = item.getAlbumArtUri();
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

