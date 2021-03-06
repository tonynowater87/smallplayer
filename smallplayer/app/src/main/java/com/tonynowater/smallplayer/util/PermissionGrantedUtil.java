package com.tonynowater.smallplayer.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * 權限處理工具
 * Created by tonynowater on 2017/9/16.
 */
public class PermissionGrantedUtil {
    private static final String TAG = PermissionGrantedUtil.class.getSimpleName();

    public static final String[] REQUEST_PERMISSIONS = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int PERMISTTION_REQUEST_CODE = 100;

    private Context context;
    private Activity activity;
    private Fragment fragment;
    private CallBack callBack;

    public PermissionGrantedUtil(Activity activity, CallBack callBack) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.callBack = callBack;
    }

    public PermissionGrantedUtil(Fragment fragment, CallBack callBack) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.callBack = callBack;
    }

    public void checkPermissiion (String... requestPermission) {
        //6.0以上才需要權限處理
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();

            for (int i = 0; i < requestPermission.length; i++) {
                if (ContextCompat.checkSelfPermission(context, requestPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(requestPermission[i]);
                }
            }

            Logger.getInstance().d(TAG, "checkPermissiion: " + permissions.size());
            if (permissions.size() > 0) {
                if (activity != null) {
                    activity.requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISTTION_REQUEST_CODE);
                } else {
                    fragment.requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISTTION_REQUEST_CODE);
                }
            } else {
                callBack.onPermissionGranted();
            }
        } else {
            //版本低於6.0，直接獲取權限
            callBack.onPermissionGranted();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISTTION_REQUEST_CODE) {
            Logger.getInstance().d(TAG, "onRequestPermissionsResult: " + permissions.length);
            Logger.getInstance().d(TAG, "onRequestPermissionsResult: " + grantResults.length);

            boolean granted = false;
            for (int i = 0; i < grantResults.length; i++) {
                granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }

            if (granted && grantResults.length > 0) {
                callBack.onPermissionGranted();
            } else {
                callBack.onPermissionNotGranted();
            }
        }
    }

    /**
     * 檢查權限
     * @param context
     * @param requestPermission
     * @return
     */
    public static boolean isPermissionGranted(Context context, String... requestPermission) {
        for (int i = 0; i < requestPermission.length; i++) {
            if (ContextCompat.checkSelfPermission(context, requestPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface CallBack {
        void onPermissionGranted();
        void onPermissionNotGranted();
    }
}
