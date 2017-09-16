package com.tonynowater.smallplayer.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null)
                {
                    mToast.cancel();
                }
                mToast = Toast.makeText(BaseActivity.this, toastMsg, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    protected abstract int getLayoutResourceId();
}
