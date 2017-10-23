package com.tonynowater.smallplayer.fragment.locallist;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.ItemTouchHelperAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListSongAdapterBinding;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.MiscellaneousUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowater.smallplayer.util.TimeUtil;

import java.util.Collections;
/**
 * Created by tonynowater on 2017/5/30.
 */
public class ShowPlayListSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutShowPlayListSongAdapterBinding> implements ItemTouchHelperAdapter{
    private static final String TAG = ShowPlayListSongAdapter.class.getSimpleName();
    private int mPlayListId;
    private MediaControllerCompat.TransportControls mTransportControls;

    public ShowPlayListSongAdapter(int mPlayListId, OnClickSomething<PlayListSongEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        this.mPlayListId = mPlayListId;
        mDataList = new RealmUtils().queryPlayListSongByListIdSortByPosition(mPlayListId);
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_show_play_list_song_adapter;
    }

    @Override
    protected void onBindItem(LayoutShowPlayListSongAdapterBinding binding, PlayListSongEntity item, int position) {
        binding.tvSongTitleSonglistadapter.setText(item.getTitle());
        binding.tvSongArtistSonglistadapter.setText(item.getArtist());
        binding.tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration(item.getDuration()));
        if (!TextUtils.isEmpty(item.getAlbumArtUri())) {
            Glide.with(mContext).load(item.getAlbumArtUri()).into(binding.ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }
        if (MetaDataCustomKeyDefine.isLocal(item.getIsLocal())) {
            binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.local_music_icon));
        } else {
            binding.ivIconTypeSonglistadapter.setImageDrawable(mContext.getDrawable(R.drawable.youtube_logo_icon));
        }
    }

    @Override
    protected boolean supportFooter() {
        return false;
    }

    @Override
    public void onDismiss(final int position) {
        DialogUtil.showYesNoDialog(mContext, String.format(mContext.getString(R.string.delete_hint), mDataList.get(position).getTitle()), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                switch (dialogAction) {
                    case POSITIVE:
                        PlayListSongEntity playListSongEntity = mDataList.get(position);
                        realmUtils.deleteSongFromPlayList(playListSongEntity);
                        mDataList = realmUtils.queryPlayListSongByListIdSortByPosition(mPlayListId);
                        MiscellaneousUtil.sendRemoveSongFromListAction(mPlayListId, playListSongEntity.getId(), mTransportControls);
                        break;
                }

                notifyDataSetChanged();
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onMove(int from, int to) {
        realmUtils.updatePlayListSongPosition(mDataList.get(from), mDataList.get(to));
        Collections.swap(mDataList, from, to);
        notifyItemMoved(from, to);
    }

    public void setTransportControls(MediaControllerCompat.TransportControls mTransportControls) {
        this.mTransportControls = mTransportControls;
    }
}
