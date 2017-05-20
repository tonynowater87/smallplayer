package com.tonynowater.smallplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseActivity;
import com.tonynowater.smallplayer.base.BaseViewPagerFragment;
import com.tonynowater.smallplayer.databinding.ActivityMainBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.fragment.songlist.SongListViewPagerFragment;
import com.tonynowater.smallplayer.fragment.u2bsearch.EnumU2BSearchType;
import com.tonynowater.smallplayer.fragment.u2bsearch.U2BSearchViewPagerFragment;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.u2b.U2BPlayListDTO;
import com.tonynowater.smallplayer.u2b.U2BVideoDTO;
import com.tonynowater.smallplayer.util.YoutubeExtratorUtil;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BaseViewPagerFragment[] mBaseViewPagerFragments;

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged: " + state);
    }

    @Override
    protected void onSessionDestroyed() {
        Log.d(TAG, "onSessionDestroyed: ");
    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataChanged: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar((Toolbar) mBinding.coordinatorLayoutMainActivity.findViewById(R.id.toolbar_main_activity));
        initialViewPager();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private void initialViewPager() {
        mBaseViewPagerFragments = new BaseViewPagerFragment[]{SongListViewPagerFragment.newInstance()
                , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_video), EnumU2BSearchType.VIDEO)
                , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_playlist), EnumU2BSearchType.PLAYLIST)
                , U2BSearchViewPagerFragment.newInstance(getString(R.string.viewpager_title_u2b_search_channel), EnumU2BSearchType.CHANNEL)};
        mBinding.viewpager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        mBinding.coordinatorLayoutMainActivity.findViewById(R.id.tab_layout_main_activity).setVisibility(View.VISIBLE);
        ((TabLayout)mBinding.coordinatorLayoutMainActivity.findViewById(R.id.tab_layout_main_activity)).setupWithViewPager(mBinding.viewpager);
    }

    @Override
    public void onClick(Playable playable) {

        if (playable instanceof Song) {
            sendMetaDataToService(((Song) playable).getMediaMetadata());
        } else if (playable instanceof U2BVideoDTO.ItemsBean) {
            final U2BVideoDTO.ItemsBean u2bVideoItem = ((U2BVideoDTO.ItemsBean) playable);
            YoutubeExtratorUtil.extratYoutube(getApplicationContext(), u2bVideoItem.getId().getVideoId(), new YoutubeExtratorUtil.CallBack() {
                @Override
                public void getU2BUrl(String url) {
                    u2bVideoItem.setDataSource(url);
                    sendMetaDataToService(u2bVideoItem.getMediaMetadata());
                }
            });
        } else if (playable instanceof U2BPlayListDTO.ItemsBean) {
            U2BPlayListDTO.ItemsBean u2bVideoItem = ((U2BPlayListDTO.ItemsBean) playable);
            Intent intent = new Intent(MainActivity.this, PlayListActivity.class);
            intent.putExtra(U2BSearchViewPagerFragment.BUNDLE_KEY_PLAYLISTID, u2bVideoItem.getId().getPlaylistId());
            startActivity(intent);
        }
    }

    private class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mBaseViewPagerFragments[position];
        }

        @Override
        public int getCount() {
            return mBaseViewPagerFragments.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseViewPagerFragment fragment = (BaseViewPagerFragment)super.instantiateItem(container, position);
            mBaseViewPagerFragments[position] = fragment;
            return fragment;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mBaseViewPagerFragments[position].getPageTitle(MainActivity.this);
        }
    }
}
