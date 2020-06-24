package com.forjrking.permission.permission

import android.R
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

/**
 * Android M (API >= 23).
 */
object EasyPermissions {

    const val SETTINGS_REQ_CODE = 76061
    const val PERM_REQ_CODE = 76051

    fun hasPermissions(context: Context?, vararg perms: String?): Boolean {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (perm in perms) {
            val hasPerm = ContextCompat.checkSelfPermission(
                context!!,
                perm!!
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPerm) {
                return false
            }
        }
        return true
    }


    fun requestPermissions(
        any: Any?, rationale: String,perms: Array<String>,
        @StringRes positiveButton: Int = R.string.ok,
        @StringRes negativeButton: Int = R.string.cancel,
        requestCode: Int = PERM_REQ_CODE
    ) {

        checkCallingObjectSuitability(any)
        var shouldShowRationale = true
        for (perm in perms) {
            shouldShowRationale =
                shouldShowRationale && shouldShowRequestPermissionRationale(any, perm)
        }
        if (shouldShowRationale) {
            // DES: 被第一次请求拒绝过
            val activity = getActivity(any) ?: return
            val dialog = AlertDialog.Builder(activity)
                .setMessage(rationale)
                .setPositiveButton(positiveButton) { _, _ ->
                    executePermissionsRequest(any, perms, requestCode)
                }
                .setNegativeButton(negativeButton) { _, _ -> // act as if the permissions were denied
                    if (any is PermissionCallbacks) {
                        any.onPermissionsDenied(requestCode, listOf(*perms))
                    }
                }.create()
            dialog.show()
        } else {
            executePermissionsRequest(any, perms, requestCode)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray, any: Any?
    ) {
        checkCallingObjectSuitability(any)

        // Make a collection of granted and denied permissions from the request.
        val granted = ArrayList<String>()
        val denied = ArrayList<String>()
        for (i in permissions.indices) {
            val perm = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm)
            } else {
                denied.add(perm)
            }
        }

        // Report granted permissions, if any.
        if (granted.isNotEmpty()) {
            // Notify callbacks
            if (any is PermissionCallbacks) {
                any.onPermissionsGranted(requestCode, granted)
            }
        }

        // Report denied permissions, if any.
        if (denied.isNotEmpty()) {
            if (any is PermissionCallbacks) {
                any.onPermissionsDenied(requestCode, denied)
            }
        }

        // If 100% successful, call annotated methods
        if (granted.isNotEmpty() && denied.isEmpty()) {
            if (any is PermissionCallbacks) any.onPermissionsAllGranted()
        }
    }

    /**
     * DES: 权限
     * TIME: 2019/6/4 0004 下午 2:53
     */
    fun checkDeniedPermissionsNeverAskAgain(
        `object`: Any?,
        rationale: String?,
        @StringRes title: Int,
        @StringRes positiveButton: Int,
        @StringRes negativeButton: Int,
        negativeButtonOnClickListener: DialogInterface.OnClickListener?,
        deniedPerms: List<String>
    ): Boolean {
        var shouldShowRationale: Boolean
        for (perm in deniedPerms) {
            shouldShowRationale =
                shouldShowRequestPermissionRationale(`object`, perm)
            if (!shouldShowRationale) {
                val activity = getActivity(`object`) ?: return true
                val dialog =
                    AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(rationale)
                        .setPositiveButton(positiveButton) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            startAppSettingsScreen(`object`, intent)
                        }
                        .setNegativeButton(negativeButton, negativeButtonOnClickListener)
                        .create()
                dialog.show()
                return true
            }
        }
        return false
    }

    @TargetApi(23)
    private fun shouldShowRequestPermissionRationale(any: Any?, perm: String): Boolean {
        return when (any) {
            is Activity -> {
                ActivityCompat.shouldShowRequestPermissionRationale((any as Activity?)!!, perm)
            }
            is Fragment -> {
                any.shouldShowRequestPermissionRationale(perm)
            }
            is android.app.Fragment -> {
                any.shouldShowRequestPermissionRationale(perm)
            }
            else -> {
                false
            }
        }
    }

    @TargetApi(23)
    private fun executePermissionsRequest(any: Any?, perms: Array<String>, requestCode: Int) {
        checkCallingObjectSuitability(any)
        when (any) {
            is Activity -> {
                ActivityCompat.requestPermissions(any, perms, requestCode)
            }
            is Fragment -> {
                any.requestPermissions(perms, requestCode)
            }
            is android.app.Fragment -> {
                any.requestPermissions(perms, requestCode)
            }
        }
    }

    @TargetApi(11)
    private fun getActivity(any: Any?): Activity? {
        return when (any) {
            is Activity -> {
                any
            }
            is Fragment -> {
                any.activity
            }
            is android.app.Fragment -> {
                any.activity
            }
            else -> {
                null
            }
        }
    }

    @TargetApi(11)
    private fun startAppSettingsScreen(any: Any?, intent: Intent) {
        when (any) {
            is Activity -> {
                any.startActivityForResult(intent, SETTINGS_REQ_CODE)
            }
            is Fragment -> {
                any.startActivityForResult(intent, SETTINGS_REQ_CODE)
            }
            is android.app.Fragment -> {
                any.startActivityForResult(intent, SETTINGS_REQ_CODE)
            }
        }
    }

    private fun checkCallingObjectSuitability(any: Any?) {
        // Make sure Object is an Activity or Fragment
        val isActivity = any is Activity
        val isSupportFragment = any is Fragment
        val isAppFragment = any is android.app.Fragment
        val isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        if (!(isSupportFragment || isActivity || isAppFragment && isMinSdkM)) {
            require(!isAppFragment) { "Target SDK needs to be greater than 23 if caller is android.app.Fragment" }
            throw IllegalArgumentException("Caller must be an Activity or a Fragment.")
        }
    }

    interface PermissionCallbacks : ActivityCompat.OnRequestPermissionsResultCallback {
        /**
         * DES: 回调此方法 一定会回调 onPermissionsDenied
         * TIME: 2019/6/4 0004 下午 5:54
         */
        fun onPermissionsGranted(requestCode: Int, perms: List<String>)

        fun onPermissionsDenied(requestCode: Int, perms: List<String>)

        fun onPermissionsAllGranted()
    }
}