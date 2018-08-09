package com.tonynowater.smallplayer.fragment.drawer;

import android.support.annotation.NonNull;

import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutDrawerAdapterListItemBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

/**
 * 側邊選單Adapter
 * Created by tonynowater on 2017/8/23.
 */
public class DrawerRecyclerViewAdapter extends BasePlayableFragmentAdapter<DrawerItem, LayoutDrawerAdapterListItemBinding> {

    public DrawerRecyclerViewAdapter(List<DrawerItem> mDataList, OnClickSomething<DrawerItem> mOnClickSongListener) {
        super(mDataList, mOnClickSongListener);
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.drawer_item;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_drawer_adapter_list_item;
    }

    @Override
    protected void onBindItem(LayoutDrawerAdapterListItemBinding binding, DrawerItem item, int position) {

    }

    @Override
    protected boolean supportFooter() {
        return false;
    }
}
