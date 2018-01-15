package com.tonynowater.smallplayer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.tonynowater.smallplayer.module.dto.realm.Migration;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by tonyliao on 2017/4/30.
 */

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static String getMyString(int id) {
        return mContext.getString(id);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG, "onCreate: " + BuildConfig.DEBUG);
        //測試版不做錯誤報告
        FirebaseCrash.setCrashCollectionEnabled(BuildConfig.DEBUG ? false : true);
        initalRealm();
    }

    private void initalRealm() {
        //初始化Ream設定
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().name("smallplayer.ream")
                .schemaVersion(1)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        RealmUtils realmUtils = new RealmUtils();
        realmUtils.close();
    }
}
