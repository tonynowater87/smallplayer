package com.tonynowater.smallplayer.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonynowtaer87.myutil.ToastUtil;

/**
 * Created by tonyliao on 2017/5/1.
 */
public abstract class BaseViewPagerFragment<T extends ViewDataBinding> extends Fragment {

    protected T mBinding;
    protected ToastUtil mToastUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToastUtil = ToastUtil.newInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getResourceId(), null, false);
        return mBinding.getRoot();
    }

    protected abstract int getResourceId();

    public abstract CharSequence getPageTitle(Context context);
}
