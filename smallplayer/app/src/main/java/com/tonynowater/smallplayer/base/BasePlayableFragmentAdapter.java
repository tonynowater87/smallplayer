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
import com.tonynowater.smallplayer.module.u2b.U2BApiDefine;
import com.tonynowater.smallplayer.util.OnClickSomething;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewAdapter的基底類別
 * Created by tonynowater on 2017/5/20.
 */
public abstract class BasePlayableFragmentAdapter<K, T extends ViewDataBinding> extends RecyclerView.Adapter<BasePlayableFragmentAdapter.BaseViewHolder>{

    private static final int NORMAL_VIEW_TYPE = 1;
    private static final int FOOTER_VIEW_TYPE = 2;

    protected List<K> mDataList;
    protected RealmUtils realmUtils;
    protected Context mContext;

    private OnClickSomething<K> mOnClickSongListener;
    private boolean mFootViewIsVisible = false;

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

    protected BasePlayableFragmentAdapter(OnClickSomething<K> mOnClickSongListener, boolean mFootViewIsVisible) {
        realmUtils = new RealmUtils();
        this.mDataList = new ArrayList<>();
        this.mOnClickSongListener = mOnClickSongListener;
        this.mFootViewIsVisible = mFootViewIsVisible;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ViewDataBinding viewDataBinding;
        final BaseViewHolder baseViewHolder;
        switch (viewType) {
            case NORMAL_VIEW_TYPE:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getNormalLayoutId(), parent, false);
                baseViewHolder = new BaseViewHolder(viewDataBinding);
                viewDataBinding.getRoot().setOnClickListener(v -> {
                    int position = baseViewHolder.getAdapterPosition();
                    if (position != -1) {
                        mOnClickSongListener.onClick(mDataList.get(position));
                    }
                });
                return baseViewHolder;
            case FOOTER_VIEW_TYPE:
                viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getFooterLayoutId(), parent, false);
                if (!mFootViewIsVisible) {
                    viewDataBinding.getRoot().setVisibility(View.GONE);
                }
                baseViewHolder = new BaseViewHolder(viewDataBinding);
                return baseViewHolder;
            default:
                throw new RuntimeException("wrong viewDataBinding type");
        }
    }

    /**
     * @return xml layout's variable name for data binding
     * ex:BR.song
     */
    protected abstract int getBindingVariableName();

    /** 設置ListItem的Normal Layout */
    protected abstract int getNormalLayoutId();

    /** 設置List的Footer Layout，要客制化Footer需Override*/
    protected int getFooterLayoutId() {
        return R.layout.list_footer;
    }

    @Override
    public void onBindViewHolder(BasePlayableFragmentAdapter.BaseViewHolder holder, int position) {
        if (getItemViewType(position) == NORMAL_VIEW_TYPE) {
            holder.bind(mDataList.get(position));
            T binding = DataBindingUtil.getBinding(holder.itemView);
            onBindItem(binding, mDataList.get(position), position);
        }
    }

    /** 設定ListItem的UI */
    protected abstract void onBindItem(T binding, K item, int position);

    @Override
    public int getItemCount() {
        if (supportFooter() && hasData() && mFootViewIsVisible) {
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
        if (supportFooter() && hasData() && mFootViewIsVisible && position + 1 == getItemCount()) {
            return FOOTER_VIEW_TYPE;
        }

        return NORMAL_VIEW_TYPE;
    }

    protected abstract boolean supportFooter();

    /** 設置要顯示的List資料 */
    public void setDataSource (List<K> dataSource) {
        if (dataSource == null) {
            return;
        }
        mDataList = new ArrayList<>(dataSource);
        if (mDataList.size() < U2BApiDefine.DEFAULT_QUERY_RESULT) {
            //資料小於查詢的最小筆數25筆，不要顯示FootView
            setFootviewVisible(false);
        }
    }

    /** @return List資料 */
    public List<K> getDataList() {
        return mDataList;
    }

    /**
     * @param footviewVisible 設置FootView是否可見，還要NotifydatasetChanged才會改變
     */
    public void setFootviewVisible (boolean footviewVisible) {
        mFootViewIsVisible = footviewVisible;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding mViewDataBinding;

        private BaseViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.mViewDataBinding = viewDataBinding;
        }

        public void bind(Object data) {
            mViewDataBinding.setVariable(getBindingVariableName(), data);
            mViewDataBinding.executePendingBindings();
        }
    }
}
