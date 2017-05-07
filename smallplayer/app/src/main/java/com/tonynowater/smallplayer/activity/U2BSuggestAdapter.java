package com.tonynowater.smallplayer.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.databinding.LayoutU2bsuggestionAdapterListItemBinding;

import java.util.List;

/**
 * Created by tonynowater on 2017/5/7.
 */

public class U2BSuggestAdapter extends RecyclerView.Adapter<U2BSuggestAdapter.U2BSuggestAdapterViewHolder> {

    private List<String> mSuggests;

    public U2BSuggestAdapter(List<String> mSuggests) {
        this.mSuggests = mSuggests;
    }

    @Override
    public U2BSuggestAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new U2BSuggestAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_u2bsuggestion_adapter_list_item, null, false));
    }

    @Override
    public void onBindViewHolder(U2BSuggestAdapterViewHolder holder, int position) {
        holder.getmBinding().tvLayoutU2bsuggestionAdapterListItem.setText(mSuggests.get(position));
    }

    @Override
    public int getItemCount() {
        return mSuggests.size();
    }

    public static class U2BSuggestAdapterViewHolder extends RecyclerView.ViewHolder {

        private LayoutU2bsuggestionAdapterListItemBinding mBinding;

        public U2BSuggestAdapterViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public LayoutU2bsuggestionAdapterListItemBinding getmBinding() {
            return mBinding;
        }
    }
}
