package com.tonynowater.smallplayer.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BasePlayableFragmentAdapter<K, T extends ViewDataBinding> extends RecyclerView.Adapter<BasePlayableFragmentAdapter.BaseViewHolder>{

    protected static final int NORMAL_VIEWTYPE = 1;
    protected static final int FOOTER_VIEWTYPE = 2;

    protected List<K> mDataList;
    protected OnClickSomething<K> mOnClickSongListener;
    protected RealmUtils realmUtils;
    protected Context mContext;

    protected BasePlayableFragmentAdapter(OnClickSomething<K> mOnClickSongListener) {
        realmUtils = new RealmUtils();
        this.mDataList = new ArrayList<>();
        this.mOnClickSongListener = mOnClickSongListener;
    }

    @Override
    public BasePlayableFragmentAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        final BaseViewHolder baseViewHolder;
        switch (viewType) {
            case NORMAL_VIEWTYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(getItemResourceId(), parent, false);
                baseViewHolder = new BaseViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickSongListener.onClick(mDataList.get(baseViewHolder.getAdapterPosition()));
                    }
                });
                return baseViewHolder;
            case FOOTER_VIEWTYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
                if (!isFootviewVisible()) {
                    view.setVisibility(View.GONE);
                }
                baseViewHolder = new BaseViewHolder(view);
                return baseViewHolder;
            default:
                throw new RuntimeException("wrong view type");
        }
    }

    /** 設置ListItem的Layout */
    protected abstract int getItemResourceId();

    @Override
    public void onBindViewHolder(BasePlayableFragmentAdapter.BaseViewHolder holder, int position) {
        if (getItemViewType(position) == NORMAL_VIEWTYPE) {
            onBindItem(holder, position);
        }
    }

    /** 設定ListItem的UI */
    protected abstract void onBindItem(BaseViewHolder holder, int position);

    @Override
    public int getItemCount() {

        if (hasData() && isFootviewVisible()) {
            return mDataList.size() + 1;
        } else {
            return mDataList.size();
        }
    }

    /** @return 是否有資料 */
    private boolean hasData() {
        return mDataList.size() > 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasData() && isFootviewVisible() && position + 1 == getItemCount()) {
            return FOOTER_VIEWTYPE;
        }

        return NORMAL_VIEWTYPE;
    }

    /** 設置要顯示的List資料 */
    public void setDataSource (List<K> dataSource) {
        mDataList = new ArrayList<>(dataSource);
    }

    /** @return 是否要顯示FooterView */
    protected boolean isFootviewVisible () {
        return true;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        private T mBinding;
        public BaseViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public T getBinding() {
            return mBinding;
        }
    }
}
