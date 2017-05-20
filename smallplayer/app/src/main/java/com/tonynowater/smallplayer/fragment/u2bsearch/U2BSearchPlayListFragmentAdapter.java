package com.tonynowater.smallplayer.fragment.u2bsearch;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.base.BaseU2BFragmentAdapter;
import com.tonynowater.smallplayer.databinding.LayoutSonglistadapterListitemBinding;
import com.tonynowater.smallplayer.dto.Song;
import com.tonynowater.smallplayer.u2b.U2BPlayListDTO;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

/**
 * Created by tony10532 on 2017/5/20.
 */

public class U2BSearchPlayListFragmentAdapter extends BaseU2BFragmentAdapter<U2BPlayListDTO.ItemsBean, U2BSearchPlayListFragmentAdapter.U2BSearchFragmentAdapterViewHolder> {
    private static final String TAG = U2BSearchPlayListFragmentAdapter.class.getSimpleName();

    public U2BSearchPlayListFragmentAdapter(OnClickSomething<Song> mOnClickSongListener) {
        super(mOnClickSongListener);
    }

    public U2BSearchPlayListFragmentAdapter(List<U2BPlayListDTO.ItemsBean> mU2BVideoList, OnClickSomething<Song> mOnClickSongListener) {
        super(mU2BVideoList, mOnClickSongListener);
    }

    @Override
    public U2BSearchPlayListFragmentAdapter.U2BSearchFragmentAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new U2BSearchPlayListFragmentAdapter.U2BSearchFragmentAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_songlistadapter_listitem,null), mOnClickSongListener, mDataList);
    }

    @Override
    public void onBindViewHolder(U2BSearchPlayListFragmentAdapter.U2BSearchFragmentAdapterViewHolder holder, int position) {
        holder.getBinding().tvSongArtistSonglistadapter.setText(mDataList.get(position).getSnippet().getTitle());
        holder.getBinding().tvSongTitleSonglistadapter.setText(mDataList.get(position).getSnippet().getDescription());
        String sUrl = mDataList.get(position).getSnippet().getThumbnails().getDefaultX().getUrl();
        if (!TextUtils.isEmpty(sUrl)) {
            Glide.with(holder.getBinding().ivSonglistadapter.getContext()).load(sUrl).into(holder.getBinding().ivSonglistadapter);
        } else {
            Glide.with(holder.getBinding().ivSonglistadapter.getContext()).load(R.mipmap.ic_launcher).into(holder.getBinding().ivSonglistadapter);
        }
    }

    public static class U2BSearchFragmentAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private LayoutSonglistadapterListitemBinding mBinding;
        private OnClickSomething mOnClickSongListener;
        private List<U2BPlayListDTO.ItemsBean> mU2BVideoList;

        public U2BSearchFragmentAdapterViewHolder(View itemView, OnClickSomething mOnClickSongListener, List<U2BPlayListDTO.ItemsBean> mU2BVideoList) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mOnClickSongListener = mOnClickSongListener;
            this.mU2BVideoList = mU2BVideoList;
            mBinding.rlRootSonglistadapter.setOnClickListener(this);
        }

        public LayoutSonglistadapterListitemBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + mU2BVideoList.get(getAdapterPosition()).getSnippet().getTitle());
            mOnClickSongListener.onClick(mU2BVideoList.get(getAdapterPosition()));
        }
    }
}
