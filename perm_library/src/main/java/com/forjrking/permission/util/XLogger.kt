package com.forjrking.permission.util

import android.util.Log
import com.forjrking.permission.AxPermission

/**
 * @Description:
 * @Author: forJrking
 * @CreateDate: 2020/6/23 17:35
 * @Version: 1.0.0
 */
object XLogger {

    private const val TAG = "AxPermission"

    @JvmStatic
    fun d(log: String) {
        if (AxPermission.isdebug) {
            Log.d(TAG, log)
        }
    }

    @JvmStatic
    fun e(log: String) {
        if (AxPermission.isdebug) {
            Log.e(TAG, log)
        }
    }
}