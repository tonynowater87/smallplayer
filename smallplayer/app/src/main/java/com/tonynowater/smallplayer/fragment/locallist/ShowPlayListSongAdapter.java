package com.tonynowater.smallplayer.fragment.locallist;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseMediaControlActivity;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.ItemTouchHelperAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListSongAdapterBinding;
import com.tonynowater.smallplayer.module.dto.MetaDataCustomKeyDefine;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListSongEntity;
import com.tonynowater.smallplayer.service.PlayMusicService;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.Collections;

/**
 * Created by tonynowater on 2017/5/30.
 */
public class ShowPlayListSongAdapter extends BasePlayableFragmentAdapter<PlayListSongEntity, LayoutShowPlayListSongAdapterBinding> implements ItemTouchHelperAdapter {

    private int mPlayListId;
    private BaseMediaControlActivity mActivity;

    public ShowPlayListSongAdapter(BaseMediaControlActivity activity, int mPlayListId, OnClickSomething<PlayListSongEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        this.mActivity = activity;
        this.mPlayListId = mPlayListId;
        mDataList = new RealmUtils().queryPlayListSongByListIdSortByPosition(mPlayListId);
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.song;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_show_play_list_song_adapter;
    }

    @Override
    protected void onBindItem(LayoutShowPlayListSongAdapterBinding binding, PlayListSongEntity item, int position) {
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
                        realmUtils.deleteSong(playListSongEntity.getId());
                        mDataList = realmUtils.queryPlayListSongByListIdSortByPosition(mPlayListId);
                        sendRemoveSongFromListAction(mPlayListId, playListSongEntity.getId());
                        break;
                }

                notifyDataSetChanged();
            }
        }, dialog -> notifyDataSetChanged());
    }

    @Override
    public void onMove(int from, int to) {
        realmUtils.updatePlayListSongPosition(mDataList.get(from), mDataList.get(to));
        Collections.swap(mDataList, from, to);
        notifyItemMoved(from, to);
    }

    /**
     * 送從歌單刪除歌曲的Action
     *
     * @param mPlayListId
     * @param playListSongEntityId
     */
    private void sendRemoveSongFromListAction(int mPlayListId, int playListSongEntityId) {
        Bundle bundle = new Bundle();
        bundle.putInt(PlayMusicService.BUNDLE_KEY_PLAYLIST_ID, mPlayListId);
        bundle.putInt(PlayMusicService.BUNDLE_KEY_SONG_ID, playListSongEntityId);
        mActivity.sendActionToService(PlayMusicService.ACTION_REMOVE_SONG_FROM_PLAYLIST, bundle);
    }
}
