package com.tonynowater.util.kotlin.activityRequest

import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.RequiresApi
import android.util.SparseArray

/**
 * Created by tonyliao on 2018/4/23.
 */
class SActivityRequestFragment: Fragment() {
    companion object {
        const val TAG = "SActivityRequestFragment"
    }

    private var mOnActivityResultCallbacks = SparseArray<SActivityRequestUtil.OnActivityResultCallback>()
    private var mOnRequestPermissionCallbacks = SparseArray<SActivityRequestUtil.OnAuthRequestCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true //true, if configuration change, fragment will not recreate
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val callback = mOnActivityResultCallbacks[requestCode]
        mOnActivityResultCallbacks.remove(requestCode)
        callback.onActivityResult(requestCode, resultCode, data)
    }

    fun startActivityResult(intent: Intent, callback: SActivityRequestUtil.OnActivityResultCallback) {
        val hashCode = callback.hashCode() shr 16
        mOnActivityResultCallbacks.put(hashCode, callback)
        startActivityForResult(intent, hashCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<out String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val callback = mOnRequestPermissionCallbacks[requestCode]
        mOnRequestPermissionCallbacks.remove(requestCode)

        var granted = false

        if (grantResults.isEmpty()) {
            callback.onPermissionNotGranted()
            return
        }

        for (iPermission in grantResults) {
            granted = iPermission == PackageManager.PERMISSION_GRANTED
        }

        if (granted) {
            callback.onPermissionGranted()
        } else {
            callback.onPermissionNotGranted()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermissions(permission: Array<out String>, callback: SActivityRequestUtil.OnAuthRequestCallback) {
        val hashCode = callback.hashCode() shr 16
        mOnRequestPermissionCallbacks.put(hashCode, callback)
        requestPermissions(permission, hashCode)
    }
}