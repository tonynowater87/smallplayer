package com.tonynowater.smallplayer.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 所有Fragment的基底類別
 * Created by tonynowater on 2017/7/23.
 */
public abstract class BaseFragment<T extends ViewDataBinding> extends android.support.v4.app.Fragment {

    private BaseActivity baseActivity;

    protected T mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), null, false);
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof BaseActivity) {
            baseActivity = (BaseMediaControlActivity) getActivity();
        }
    }

    protected void showToast(String toastMsg) {
        if (baseActivity != null) {
            baseActivity.showToast(toastMsg);
        }
    }

    protected abstract int getLayoutResourceId();
}
