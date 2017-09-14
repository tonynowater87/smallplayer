package com.tonynowater.smallplayer.fragment.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseFragment;
import com.tonynowater.smallplayer.databinding.LayoutDrawerFragmentLeftBinding;
import com.tonynowater.smallplayer.fragment.u2bsearch.RecyclerViewDivideLineDecorator;
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.DialogUtil;
import com.tonynowater.smallplayer.util.GoogleLoginUtil;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

// TODO: 2017/8/27 還沒完成匯入Youtube播放清單功能，需要整理查詢歌曲的方式
/**
 * 側邊選單Fragment
 * Created by tonynowater on 2017/7/23.
 */
public class DrawerFragment extends BaseFragment<LayoutDrawerFragmentLeftBinding> implements OnClickSomething<DrawerItem> {

    private GoogleLoginUtil mGoogleLoginUtil;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_drawer_fragment_left;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialRecyclerView();
        mGoogleLoginUtil = new GoogleLoginUtil(getContext(), getActivity(), new GoogleLoginUtil.OnGoogleLoginCallBack() {

            @Override
            public void onGoogleLoginSuccess(String authToken) {
                U2BApi.newInstance().queryUserPlaylist(authToken, new U2BApi.OnU2BApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtil.showImportUserPlayListDialog(getContext());
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMsg) {

                    }
                });
            }

            @Override
            public void onGoogleLoginFailure() {

            }
        });
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
        itemList.add(new DrawerItem(android.R.drawable.ic_menu_upload_you_tube, getString(R.string.login_and_import_youtube_playlists)));
        return itemList;
    }

    @Override
    public void onClick(DrawerItem drawerItem) {
        showToast(drawerItem.getTitle());
        switch (drawerItem.getIcon()) {
            case android.R.drawable.ic_menu_upload_you_tube:
                mGoogleLoginUtil.googleSignIn(this);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleLoginUtil.onActivityResult(requestCode, resultCode, data);
    }
}
