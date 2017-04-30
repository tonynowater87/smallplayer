package com.tonynowater.myu2bwidget;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tonynowater.myu2bwidget.databinding.ActivityMainBinding;
import com.tonynowater.myyoutubeapi.U2BApi;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding mBinding;

    private Callback mCallBack = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            Log.e(TAG, "onFailure: " + e.toString());
        }

        @Override
        public void onResponse(Response response) throws IOException {
            Log.d(TAG, "onResponse: " + response.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.btQueryVideoU2bTest.setOnClickListener(this);
        mBinding.btQueryPlaylistU2bTest.setOnClickListener(this);
        mBinding.btQueryChannelU2bTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String input = mBinding.etQueryU2bTest.getText().toString();

        if (!TextUtils.isEmpty(input)) {

            if (v.getId() == mBinding.btQueryVideoU2bTest.getId()) {
                U2BApi.newInstance().queryU2BVideo(input, mCallBack);
            } else if (v.getId() == mBinding.btQueryPlaylistU2bTest.getId()) {
                U2BApi.newInstance().queryU2BPlayList(input, mCallBack);
            } else if (v.getId() == mBinding.btQueryChannelU2bTest.getId()) {
                U2BApi.newInstance().queryU2BChannel(input, mCallBack);
            }
        }
    }
}
