package com.tonynowater.smallplayer.fragment.locallist;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.ItemTouchHelperAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListSongAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;
import com.tonynowtaer87.myutil.TimeUtil;

import java.util.Collections;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class ShowPlayListSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutShowPlayListSongAdapterBinding> implements ItemTouchHelperAdapter{
    private static final String TAG = ShowPlayListSongAdapter.class.getSimpleName();
    private int playListId;
    public ShowPlayListSongAdapter(int id, OnClickSomething<PlayListSongEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        playListId = id;
        mDataList = new RealmUtils().queryPlayListSongByListIdSortByPosition(id);
    }

    @Override
    protected boolean isFootviewVisible() {
        return false;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_show_play_list_song_adapter;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().tvSongTitleSonglistadapter.setText(mDataList.get(position).getTitle());
        holder.getBinding().tvSongArtistSonglistadapter.setText(mDataList.get(position).getArtist());
        holder.getBinding().tvDurationSonglistadapter.setText(TimeUtil.formatSongDuration(mDataList.get(position).getDuration()));
        if (!TextUtils.isEmpty(mDataList.get(position).getAlbumArtUri())) {
            Glide.with(mContext).load(mDataList.get(position).getAlbumArtUri()).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }
    }

    @Override
    public void onDismiss(final int position) {
        DialogUtil.showYesNoDialog(mContext, String.format(mContext.getString(R.string.delete_hint), mDataList.get(position).getTitle()), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                switch (dialogAction) {
                    case POSITIVE:
                        realmUtils.deleteSongFromPlayList(mDataList.get(position));
                        mDataList = realmUtils.queryPlayListSongByListIdSortByPosition(playListId);
                        break;
                }

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
}
