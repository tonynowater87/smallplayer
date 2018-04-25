package com.tonynowater.smallplayer.util.permission;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

/**
 * Created by tonyliao on 2018/4/25.
 */
public class SActivityRequestUtil {
    public interface OnActivityResultCallback {
        void onActivityResult(int requestCode , int resultCode, Intent data);
    }

    public interface OnAuthRequestCallback {
        void onPermissionGranted();
        void onPermissionNotGranted();
    }

    private SActivityRequestFragment mFragment;
    private SActivityRequestFragmentV4 mFragmentV4;
    private Context mContext;

    public SActivityRequestUtil(Activity activity) {
        mFragment = getActivityResultFragment(activity);
        mContext = activity.getApplicationContext();
    }

    public SActivityRequestUtil(AppCompatActivity activity) {
        mFragmentV4 = getActivityResultFragment(activity);
        mContext = activity.getApplicationContext();
    }

    public final void requestPermission(@NotNull SActivityRequestUtil.OnAuthRequestCallback callback, @NotNull String... permission) {
        Intrinsics.checkParameterIsNotNull(callback, "callback");
        Intrinsics.checkParameterIsNotNull(permission, "permission");
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissionGranted(mContext, permission)) {
                if (mFragment != null) {
                    mFragment.requestPermissions(permission, callback);
                }

                if (mFragmentV4 != null) {
                    mFragmentV4.requestPermissions(permission, callback);
                }
            } else {
                callback.onPermissionGranted();
            }
        } else {
            callback.onPermissionGranted();
        }
    }

    /**
     * 檢查是否有權限
     *
     * @param context
     * @param requestPermissions 要檢查的權限
     * @return true: Granted
     * false: Not Granted
     */
    public static boolean checkPermissionGranted(Context context, String[] requestPermissions) {
        for (String permission : requestPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public final void startActivityResult(@NotNull Intent intent, @NotNull SActivityRequestUtil.OnActivityResultCallback callback) {
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        Intrinsics.checkParameterIsNotNull(callback, "callback");
        if (this.mFragment != null) {
            this.mFragment.startActivityResult(intent, callback);
        }

        if (this.mFragmentV4 != null) {
            this.mFragmentV4.startActivityResult(intent, callback);
        }
    }

    private SActivityRequestFragment getActivityResultFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        Intrinsics.checkExpressionValueIsNotNull(fragmentManager, "fragmentManager");
        SActivityRequestFragment fragment = this.findActivityResultFragment(fragmentManager);
        if (fragment == null) {
            fragment = new SActivityRequestFragment();
            fragmentManager.beginTransaction().add(fragment, SActivityRequestFragment.TAG).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }

        return fragment;
    }

    private SActivityRequestFragmentV4 getActivityResultFragment(AppCompatActivity activity) {
        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Intrinsics.checkExpressionValueIsNotNull(fragmentManager, "fragmentManager");
        SActivityRequestFragmentV4 fragment = this.findActivityResultFragment(fragmentManager);
        if (fragment == null) {
            fragment = new SActivityRequestFragmentV4();
            fragmentManager.beginTransaction().add(fragment, SActivityRequestFragmentV4.TAG).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }

        return fragment;
    }

    private SActivityRequestFragment findActivityResultFragment(FragmentManager fragmentManager) {
        return (SActivityRequestFragment)fragmentManager.findFragmentByTag(SActivityRequestFragment.TAG);
    }

    private SActivityRequestFragmentV4 findActivityResultFragment(android.support.v4.app.FragmentManager fragmentManager) {
        return (SActivityRequestFragmentV4)fragmentManager.findFragmentByTag(SActivityRequestFragmentV4.TAG);
    }
}

