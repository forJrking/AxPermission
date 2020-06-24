package com.forjrking.permission.permission

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import com.forjrking.permission.util.XLogger
import java.util.*

/**
 * DES: 权限管理界面栈
 * CHANGED: 岛主
 * TIME: 2019/6/3 0003 下午 7:09
 */
class PermissionActivityLifecycle : ActivityLifecycleCallbacks {

    private val activities by lazy { mutableListOf<Activity>() }

    /**
     * 获取可用Activity
     *
     * @return Activity 优先栈顶
     */
    val activity: Activity?
        get() {
            if (activities.isEmpty()) {
                XLogger.d("not one Activity on stack")
                return null
            }
            XLogger.d("current  stack:$activities")
            for (i in activities.indices.reversed()) {
                val activity = activities[i]
                if (isAvailable(activity)) {
                    XLogger.d("top available activity is :" + activity.javaClass.simpleName)
                    return activity
                }
            }
            XLogger.d("no available Activity on stack")
            return null
        }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activities.add(activity)
        XLogger.d("stack added:" + activity.javaClass.simpleName)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
        XLogger.d("stack removed:" + activity.javaClass.simpleName)
    }

    companion object {
        /**
         * 判断Activity 是否可用
         *
         * @param activity 目标Activity
         * @return true of false
         */
        @JvmStatic
        fun isAvailable(activity: Activity?): Boolean {
            if (null == activity) {
                return false
            }
            if (activity.isFinishing) {
                XLogger.e(" activity is finishing :" + activity.javaClass.simpleName)
                return false
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
                XLogger.e(" activity is destroyed :" + activity.javaClass.simpleName)
                return false
            }
            return true
        }
    }
}