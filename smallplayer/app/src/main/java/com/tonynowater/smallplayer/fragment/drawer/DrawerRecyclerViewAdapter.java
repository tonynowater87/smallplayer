package com.tonynowater.smallplayer.fragment.drawer;

import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutDrawerAdapterListItemBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

/**
 * Created by tonynowater on 2017/8/23.
 */

public class DrawerRecyclerViewAdapter extends BasePlayableFragmentAdapter<DrawerItem, LayoutDrawerAdapterListItemBinding> {

    public DrawerRecyclerViewAdapter(List<DrawerItem> mDataList, OnClickSomething<DrawerItem> mOnClickSongListener) {
        super(mDataList, mOnClickSongListener);
    }

    @Override
    protected boolean isFootViewVisible() {
        return false;
    }

    @Override
    protected int getItemResourceId() {
        return R.layout.layout_drawer_adapter_list_item;
    }

    @Override
    protected void onBindItem(BaseViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.drawer_item, mDataList.get(position));
        holder.getBinding().executePendingBindings();
    }
}
