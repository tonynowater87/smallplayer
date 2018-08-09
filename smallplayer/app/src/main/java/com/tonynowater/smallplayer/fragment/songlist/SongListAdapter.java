package com.tonynowater.smallplayer.fragment.songlist;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.BR;
import com.tonynowater.smallplayer.MyApplication;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BasePlayableFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.module.dto.Song;
import com.tonynowater.smallplayer.util.MediaUtils;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
// TODO: 2017/6/18 下載完歌曲，不會自動刷新的問題

/**
 * 本地音樂Adapter
 * Created by tonyliao on 2017/4/27.
 */
public class SongListAdapter extends BasePlayableFragmentAdapter<Song, LayoutSonglistadapterListitemBinding> implements Filterable {

    private ArrayList<Song> mDuplicateList;//所有歌曲的copy

    public SongListAdapter(OnClickSomething mOnClickSongListener) {
        super(mOnClickSongListener);
        mDataList = MediaUtils.getSongList(MyApplication.getContext(), false);
        mDuplicateList = new ArrayList<>(mDataList);
    }

    @NonNull
    @Override
    protected int getBindingVariableName() {
        return BR.song;
    }

    @Override
    protected int getNormalLayoutId() {
        return R.layout.layout_songlistadapter_listitem;
    }

    @Override
    protected void onBindItem(LayoutSonglistadapterListitemBinding binding, Song item, int position) {
        if (!TextUtils.isEmpty(item.getmAlbumObj().getmAlbumArt())) {
            Glide.with(MyApplication.getContext()).load(Uri.fromFile(new File(item.getmAlbumObj().getmAlbumArt()))).into(binding.ivSonglistadapter);
        } else {
            Glide.with(MyApplication.getContext()).load(R.drawable.ic_default_art).into(binding.ivSonglistadapter);
        }
    }

    @Override
    protected boolean supportFooter() {
        return false;
    }

    @Override
    public Filter getFilter() {
        return new SongListAdapterFilter();
    }

    @Override
    public void setDataSource(List<Song> dataSource) {
        super.setDataSource(dataSource);
        mDuplicateList = new ArrayList<>(mDataList);
    }

    private class SongListAdapterFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Song> filterlist = new ArrayList<>();
            for (int i = 0; i < mDuplicateList.size(); i++) {
                Song song = mDuplicateList.get(i);
                if (song.getmTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                        || song.getmArtist().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filterlist.add(song);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.count = filterlist.size();
            filterResults.values = filterlist;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                mDataList = (ArrayList<Song>) results.values;
                notifyDataSetChanged();
            } else {
                mDataList = new ArrayList<>(mDuplicateList);
                notifyDataSetChanged();
            }
        }
    }

}
