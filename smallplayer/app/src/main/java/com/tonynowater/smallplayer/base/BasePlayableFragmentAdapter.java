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
import com.tonynowater.smallplayer.module.u2b.U2BApi;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewAdapter的基底類別
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BasePlayableFragmentAdapter<K, T extends ViewDataBinding> extends RecyclerView.Adapter<BasePlayableFragmentAdapter.BaseViewHolder>{

    protected static final int NORMAL_VIEWTYPE = 1;
    protected static final int FOOTER_VIEWTYPE = 2;

    protected List<K> mDataList;
    protected OnClickSomething<K> mOnClickSongListener;
    protected RealmUtils realmUtils;
    protected Context mContext;
    protected boolean mFootviewIsVisible = true;

    public BasePlayableFragmentAdapter(List<K> mDataList, OnClickSomething<K> mOnClickSongListener) {
        realmUtils = new RealmUtils();
        this.mDataList = mDataList;
        this.mOnClickSongListener = mOnClickSongListener;
    }

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
                view = LayoutInflater.from(parent.getContext()).inflate(getNormalLayoutId(), parent, false);
                baseViewHolder = new BaseViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickSongListener.onClick(mDataList.get(baseViewHolder.getAdapterPosition()));
                    }
                });
                return baseViewHolder;
            case FOOTER_VIEWTYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(getFooterLayoutId(), parent, false);
                if (!mFootviewIsVisible) {
                    view.setVisibility(View.GONE);
                }
                baseViewHolder = new BaseViewHolder(view);
                return baseViewHolder;
            default:
                throw new RuntimeException("wrong view type");
        }
    }

    /** 設置ListItem的Normal Layout */
    protected abstract int getNormalLayoutId();

    /** 設置List的Footer Layout，要客制化Footer需Override*/
    protected int getFooterLayoutId() {
        return R.layout.list_footer;
    };

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

        if (supportFooter() && hasData() && mFootviewIsVisible) {
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
        if (supportFooter() && hasData() && mFootviewIsVisible && position + 1 == getItemCount()) {
            return FOOTER_VIEWTYPE;
        }

        return NORMAL_VIEWTYPE;
    }

    protected abstract boolean supportFooter();

    /** 設置要顯示的List資料 */
    public void setDataSource (List<K> dataSource) {
        mDataList = new ArrayList<>(dataSource);
        if (mDataList.size() < U2BApi.QUERY_MAX_RESULT) {
            //資料小於查詢的最小筆數25筆，不要顯示FootView
            setFootviewVisible(false);
        }
    }

    /** @return List資料 */
    public List<K> getDataList() {
        return mDataList;
    }

    /**
     * @param footviewVisible 設置FootView是否可見
     */
    public void setFootviewVisible (boolean footviewVisible) {
        mFootviewIsVisible = footviewVisible;
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
