package com.tonynowater.smallplayer.fragment.drawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.LayoutDrawerFragmentLeftBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * 側邊選單Fragment
 * Created by tonynowater on 2017/7/23.
 */
public class DrawerFragment extends BaseFragment<LayoutDrawerFragmentLeftBinding> implements OnClickSomething<DrawerItem> {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_drawer_fragment_left;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialRecyclerView();
    }

    private void initialRecyclerView() {
        DrawerRecyclerViewAdapter drawerRecyclerViewAdapter = new DrawerRecyclerViewAdapter(generateDrawerItems(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerViewDivideLineDecorator recyclerViewDivideLineDecorator = new RecyclerViewDivideLineDecorator(getContext());
        mBinding.recyclerviewDrawerlayoutLeft.setAdapter(drawerRecyclerViewAdapter);
        mBinding.recyclerviewDrawerlayoutLeft.setLayoutManager(linearLayoutManager);
        mBinding.recyclerviewDrawerlayoutLeft.addItemDecoration(recyclerViewDivideLineDecorator);
    }

    private List<DrawerItem> generateDrawerItems() {
        List<DrawerItem> itemList = new ArrayList<>();
//        itemList.add(new DrawerItem(android.R.drawable.ic_menu_month, getString(R.string.drawer_item_popular_music)));
//        itemList.add(new DrawerItem(android.R.drawable.ic_menu_month, getString(R.string.drawer_item_search_music)));
//        itemList.add(new DrawerItem(android.R.drawable.ic_menu_month, getString(R.string.drawer_item_local_music)));
//        itemList.add(new DrawerItem(android.R.drawable.ic_menu_set_as, getString(R.string.drawer_item_setting)));
//        itemList.add(new DrawerItem(android.R.drawable.ic_menu_upload_you_tube, getString(R.string.login_and_import_youtube_playlists)));
        return itemList;
    }

    @Override
    public void onClick(DrawerItem drawerItem) {

    }
}
