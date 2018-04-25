package com.tonynowater.smallplayer.util.permission;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;

/**
 * Created by tonyliao on 2018/4/25.
 */
public class SActivityRequestFragmentV4 extends Fragment {
    public static final String TAG = "SActivityRequestFragmentV4";

    private SparseArray<SActivityRequestUtil.OnActivityResultCallback> mOnActivityResultCallbacks = new SparseArray();
    private SparseArray<SActivityRequestUtil.OnAuthRequestCallback> mOnRequestPermissionCallbacks = new SparseArray();

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SActivityRequestUtil.OnActivityResultCallback callback = this.mOnActivityResultCallbacks.get(requestCode);
        this.mOnActivityResultCallbacks.remove(requestCode);
        callback.onActivityResult(requestCode, resultCode, data);
    }

    public final void startActivityResult(@NotNull Intent intent, @NotNull SActivityRequestUtil.OnActivityResultCallback callback) {
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        Intrinsics.checkParameterIsNotNull(callback, "callback");
        int hashCode = callback.hashCode() >> 16;
        this.mOnActivityResultCallbacks.put(hashCode, callback);
        this.startActivityForResult(intent, hashCode);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        Intrinsics.checkParameterIsNotNull(permissions, "permissions");
        Intrinsics.checkParameterIsNotNull(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SActivityRequestUtil.OnAuthRequestCallback callback = this.mOnRequestPermissionCallbacks.get(requestCode);
        this.mOnRequestPermissionCallbacks.remove(requestCode);
        boolean granted = false;
        if (grantResults.length == 0) {
            callback.onPermissionNotGranted();
        } else {
            for (int iPermission : grantResults) {
                granted = iPermission == 0;
            }

            if (granted) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionNotGranted();
            }
        }
    }

    public final void requestPermissions(@NotNull String[] permission, @NotNull SActivityRequestUtil.OnAuthRequestCallback callback) {
        Intrinsics.checkParameterIsNotNull(permission, "permission");
        Intrinsics.checkParameterIsNotNull(callback, "callback");
        int hashCode = callback.hashCode() >> 16;
        this.mOnRequestPermissionCallbacks.put(hashCode, callback);
        this.requestPermissions(permission, hashCode);
    }
}
