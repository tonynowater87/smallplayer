package com.tonynowater.smallplayer.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowater.smallplayer.R;
import com.tonynowater.smallplayer.u2b.Playable;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BaseU2BFragmentAdapter<K extends Playable, T extends ViewDataBinding> extends RecyclerView.Adapter<BaseU2BFragmentAdapter.BaseViewHolder>{

    protected static final int NORMAL_VIEWTYPE = 1;
    protected static final int FOOTER_VIEWTYPE = 2;

    protected List<K> mDataList;
    protected OnClickSomething<Playable> mOnClickSongListener;

    protected BaseU2BFragmentAdapter(OnClickSomething<Playable> mOnClickSongListener) {
        this.mDataList = new ArrayList<>();
        this.mOnClickSongListener = mOnClickSongListener;
    }

    @Override
    public BaseU2BFragmentAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(BaseU2BFragmentAdapter.BaseViewHolder holder, int position) {
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
