package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tonynowater.myyoutubeapi.U2BVideoDTO;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyliao on 2017/5/1.
 */

public class U2BSearchFragmentAdapter extends RecyclerView.Adapter<U2BSearchFragmentAdapter.U2BSearchFragmentAdapterViewHolder> {

    private List<U2BVideoDTO.ItemsBean> mSongList;
    private OnClickSomething<Song> mOnClickSongListener;

    public U2BSearchFragmentAdapter(OnClickSomething mOnClickSongListener) {
        mSongList = new ArrayList<>();
        this.mOnClickSongListener = mOnClickSongListener;
    }

    @Override
    public U2BSearchFragmentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new U2BSearchFragmentAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_songlistadapter_listitem,null), mOnClickSongListener, mSongList);
    }

    @Override
    public void onBindViewHolder(U2BSearchFragmentAdapterViewHolder holder, int position) {
        holder.getBinding().tvSongArtistSonglistadapter.setText(mSongList.get(position).getSnippet().getTitle());
        holder.getBinding().tvSongTitleSonglistadapter.setText(mSongList.get(position).getSnippet().getDescription());
        String sUrl = mSongList.get(position).getSnippet().getThumbnails().getDefaultX().getUrl();
        if (!TextUtils.isEmpty(sUrl)) {
            Glide.with(holder.getBinding().ivSonglistadapter.getContext()).load(sUrl).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(holder.getBinding().ivSonglistadapter.getContext()).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public static class U2BSearchFragmentAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private LayoutSonglistadapterListitemBinding mBinding;
        private OnClickSomething mOnClickSongListener;
        private List<U2BVideoDTO.ItemsBean> mSongList;

        public U2BSearchFragmentAdapterViewHolder(View itemView, OnClickSomething mOnClickSongListener, List<U2BVideoDTO.ItemsBean> mSongList) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mOnClickSongListener = mOnClickSongListener;
            this.mSongList = mSongList;
            mBinding.rlRootSonglistadapter.setOnClickListener(this);
        }

        public LayoutSonglistadapterListitemBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            mOnClickSongListener.onClick(mSongList.get(getAdapterPosition()));
        }
    }

    public void setDataSource (List<U2BVideoDTO.ItemsBean> dataSource) {
        mSongList = new ArrayList<>(dataSource);
    }
}

