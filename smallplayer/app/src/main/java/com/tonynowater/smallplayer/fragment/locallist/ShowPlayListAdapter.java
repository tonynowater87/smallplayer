package com.tonynowater.smallplayer.fragment.locallist;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.base.ItemTouchHelperAdapter;
import com.tonynowater.smallplayer.databinding.LayoutShowPlayListAdapterBinding;
import com.tonynowater.smallplayer.module.dto.realm.dao.BaseDAO;
import com.tonynowater.smallplayer.module.dto.realm.entity.PlayListEntity;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

/**
 * 播放清單Adapter
 * Created by tonynowater on 2017/5/29.
 */
public class ShowPlayListAdapter extends BasePlayableFragmentAdapter<PlayListEntity, LayoutShowPlayListAdapterBinding> implements ItemTouchHelperAdapter{
    private static final String TAG = ShowPlayListAdapter.class.getSimpleName();
    int[] mSongCountArray;
    public ShowPlayListAdapter(OnClickSomething<PlayListEntity> mOnClickSongListener) {
        super(mOnClickSongListener);
        mDataList = realmUtils.queryAllPlayListSortByPosition();
        mSongCountArray = new int[mDataList.size()];
        for (int i = 0; i < mDataList.size(); i++) {
            mSongCountArray[i] = realmUtils.queryPlayListSongByListId(mDataList.get(i).getId()).size();
        }
    }

    @Override
    protected boolean isFootviewVisible() {
        return false;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_show_play_list_adapter;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().tvPlaylistName.setText(mDataList.get(position).getPlayListName());
        holder.getBinding().tvPlaylistCreateDate.setText(String.format(mContext.getString(R.string.playlist_create_date), mDataList.get(position).getCreateDate()));
        holder.getBinding().tvPlaylistSongCount.setText(String.valueOf(mSongCountArray[position]));
    }

    @Override
    public void onDismiss(int position) {
        final PlayListEntity playListEntity = mDataList.get(position);
        if (playListEntity.isDeletable()) {
            DialogUtil.showYesNoDialog(mContext, String.format(mContext.getString(R.string.delete_hint), playListEntity.getPlayListName()),new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                    switch (dialogAction) {
                        case POSITIVE:
                            realmUtils.deletePlayList(playListEntity);
                            realmUtils.setCurrentPlayListID(realmUtils.queryAllPlayList().size() > 1 ? (realmUtils.queryCurrentPlayListPosition() - 1) : BaseDAO.DEFAULT_ID);
                            mDataList = realmUtils.queryAllPlayListSortByPosition();
                            break;
                    }
                    notifyDataSetChanged();
                }
            });
        } else {
            DialogUtil.showMessageDialog(mContext, mContext.getString(R.string.normal_dialog_title),mContext.getString(R.string.default_list_can_not_delete));
            notifyDataSetChanged();
        }
    }

    @Override
    public void onMove(int from, int to) {
        realmUtils.updatePlayListPosition(mDataList.get(from), mDataList.get(to));
        mDataList = realmUtils.queryAllPlayListSortByPosition();
        notifyDataSetChanged();
    }
}
