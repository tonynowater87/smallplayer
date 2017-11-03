package com.tonynowater.smallplayer.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

/**
 * 所有Activity的基底類別
 * Created by tonynowater on 2017/7/23.
 */
public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T mBinding;
    private Toast mToast = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutResourceId());
    }

    public void showToast(final String toastMsg) {
        runOnUiThread(() -> {
            if (mToast != null)
            {
                mToast.cancel();
            }
            mToast = Toast.makeText(BaseActivity.this, toastMsg, Toast.LENGTH_SHORT);
            mToast.show();
        });
    }

    //turn off scrolling
    protected void turnOffToolbarScrolling(Toolbar mToolbar, AppBarLayout appBarLayout) {
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(null);
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    //turn on scrolling
    protected void turnOnToolbarScrolling(Toolbar mToolbar, AppBarLayout appBarLayout) {
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    protected abstract int getLayoutResourceId();
}
