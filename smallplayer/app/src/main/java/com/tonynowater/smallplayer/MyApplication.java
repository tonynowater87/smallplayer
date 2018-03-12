package com.tonynowater.smallplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.leakcanary.LeakCanary;
import com.tonynowater.smallplayer.module.dto.realm.Migration;
import com.tonynowater.smallplayer.module.dto.realm.RealmUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.tonynowater.smallplayer.service.notification.ChannelConstant.DEFAULT_CHANNEL_GROUP_ID;
import static com.tonynowater.smallplayer.service.notification.ChannelConstant.DEFAULT_CHANNEL_GROUP_NAME;
import static com.tonynowater.smallplayer.service.notification.ChannelConstant.DOWNLOAD_CHANNEL_ID;
import static com.tonynowater.smallplayer.service.notification.ChannelConstant.DOWNLOAD_CHANNEL_NAME;
import static com.tonynowater.smallplayer.service.notification.ChannelConstant.PLAYER_CHANNEL_ID;
import static com.tonynowater.smallplayer.service.notification.ChannelConstant.PLAYER_CHANNEL_NAME;

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

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG, "onCreate: " + BuildConfig.DEBUG);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        LeakCanary.install(this);

        FirebaseCrash.setCrashCollectionEnabled(BuildConfig.DEBUG ? false : true);//測試版不做錯誤報告
        initalRealm();
        addNotificationChannel();
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

    private void addNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = getNotificationManager();

            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(DEFAULT_CHANNEL_GROUP_ID, DEFAULT_CHANNEL_GROUP_NAME);
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);

            NotificationChannel notificationChannelDefault = new NotificationChannel(PLAYER_CHANNEL_ID, PLAYER_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannelDefault.setGroup(DEFAULT_CHANNEL_GROUP_ID);
            notificationManager.createNotificationChannel(notificationChannelDefault);

            NotificationChannel notificationChannel = new NotificationChannel(DOWNLOAD_CHANNEL_ID, DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setGroup(DEFAULT_CHANNEL_GROUP_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
