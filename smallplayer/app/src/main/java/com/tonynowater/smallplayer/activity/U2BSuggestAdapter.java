package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutU2bsuggestionAdapterListItemBinding;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.List;

/**
 * Google關鍵字搜尋列表
 * Created by tonynowater on 2017/5/7.
 */
public class U2BSuggestAdapter extends RecyclerView.Adapter<U2BSuggestAdapter.U2BSuggestAdapterViewHolder> {

    private List<String> mSuggests;
    private OnClickSomething mOnClickSomething;

    public U2BSuggestAdapter(List<String> mSuggests, OnClickSomething mOnClickSomething) {
        this.mSuggests = mSuggests;
        this.mOnClickSomething = mOnClickSomething;
    }

    @Override
    public U2BSuggestAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new U2BSuggestAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_u2bsuggestion_adapter_list_item, null, false), mSuggests, mOnClickSomething);
    }

    @Override
    public void onBindViewHolder(U2BSuggestAdapterViewHolder holder, int position) {
        holder.getmBinding().tvLayoutU2bsuggestionAdapterListItem.setText(mSuggests.get(position));
    }

    @Override
    public int getItemCount() {
        return mSuggests.size();
    }

    public static class U2BSuggestAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LayoutU2bsuggestionAdapterListItemBinding mBinding;
        private final List<String> mSuggests;
        private final OnClickSomething mOnClickSomething;

        public U2BSuggestAdapterViewHolder(View itemView, List<String> mSuggests, OnClickSomething mOnClickSomething) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            this.mSuggests = mSuggests;
            this.mOnClickSomething = mOnClickSomething;
            mBinding.llLayoutU2bsuggestionAdapterListItem.setOnClickListener(this);
        }

        public LayoutU2bsuggestionAdapterListItemBinding getmBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            mOnClickSomething.onClick(mSuggests.get(getAdapterPosition()));
        }
    }
}
