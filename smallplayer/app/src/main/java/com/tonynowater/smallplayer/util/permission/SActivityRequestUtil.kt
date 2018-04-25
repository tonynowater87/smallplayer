package com.tonynowater.util.kotlin.activityRequest

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

/**
 *
 * Convenience to use startActivityResult by no-ui fragment
 *
 * Convenience to request permissions by no-ui fragment
 *
 * Created by tonyliao on 2018/4/23.
 */
class SActivityRequestUtil {
    interface OnActivityResultCallback {
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

    interface OnAuthRequestCallback {
        fun onPermissionGranted()
        fun onPermissionNotGranted()
    }

    private var mFragment: SActivityRequestFragment? = null
    private var mFragmentV4: SActivityRequestFragmentV4? = null
    private var mContext: Context

    constructor(activity: Activity) {
        mFragment = getActivityResultFragment(activity)
        mContext = activity.applicationContext
    }

    //support
    constructor(activity: AppCompatActivity) {
        mFragmentV4 = getActivityResultFragment(activity)
        mContext = activity.applicationContext
    }

    fun requestPermission(callback: OnAuthRequestCallback, vararg permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0以上才需要權限處理
            if (!checkPermissionGranted(mContext, permission)) {
                mFragment?.requestPermissions(permission, callback)
                mFragmentV4?.requestPermissions(permission, callback)
            } else {
                callback.onPermissionGranted()
            }
        } else {
            //版本低於6.0，直接獲取權限
            callback.onPermissionGranted()
        }
    }

    companion object {
        @JvmStatic
        fun checkPermissionGranted(context: Context, permissions: Array<out String>): Boolean {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }

    fun startActivityResult(intent: Intent, callback: OnActivityResultCallback) {
        mFragment?.startActivityResult(intent, callback)
        mFragmentV4?.startActivityResult(intent, callback)
    }

    private fun getActivityResultFragment(activity: Activity): SActivityRequestFragment {
        val fragmentManager = activity.fragmentManager
        var fragment = findActivityResultFragment(fragmentManager)
        if (fragment == null) {
            fragment = SActivityRequestFragment()
            fragmentManager.beginTransaction()
                    .add(fragment, SActivityRequestFragment.TAG)
                    .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()//request commit now
        }
        return fragment
    }

    private fun getActivityResultFragment(activity: AppCompatActivity): SActivityRequestFragmentV4 {
        val fragmentManager = activity.supportFragmentManager
        var fragment = findActivityResultFragment(fragmentManager)
        if (fragment == null) {
            fragment = SActivityRequestFragmentV4()
            fragmentManager.beginTransaction()
                    .add(fragment, SActivityRequestFragmentV4.TAG)
                    .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()//request commit noew
        }
        return fragment
    }

    private fun findActivityResultFragment(fragmentManager: FragmentManager) =
            fragmentManager.findFragmentByTag(SActivityRequestFragment.TAG) as SActivityRequestFragment?

    private fun findActivityResultFragment(fragmentManager: android.support.v4.app.FragmentManager) =
            fragmentManager.findFragmentByTag(SActivityRequestFragmentV4.TAG) as SActivityRequestFragmentV4?


}