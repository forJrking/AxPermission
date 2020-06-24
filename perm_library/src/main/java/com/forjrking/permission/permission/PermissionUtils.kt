package com.forjrking.permission.permission

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import com.forjrking.permission.permission.PermissionXmlCheck.checkManifestPermission
import com.forjrking.permission.util.XLogger.d
import java.util.*

/**
 * 安卓8.0以下申请一个权限，用户同意后整个权限组的权限都不用申请可以直接使用
 * 8.0后每个权限都要单独申请，不能一次申请通过后,整个权限组都不用申请;
 * 但是用户同意权限组内一个之后，其它申请直接通过(之前是不申请可以直接用，现在是申请直接过(但是必须申请))
 *
 * @author 岛主
 */
class PermissionUtils {

    private lateinit var mPermissions: Array<String>

    /**
     * 需要请求的权限列表
     *
     * @param permissions 权限列表
     * @return 返回自身链式编程
     */
    fun request(vararg permissions: String): PermissionUtils {
        mPermissions = arrayOf(*permissions)
        return this
    }

    /**
     * 筛选出需要申请的权限
     *
     * @param permissions 权限列表
     * @return 需要申请的权限
     */
    private fun getNeedRequestPermissions(act: Activity, permissions: Array<String>): Array<String> {
        val list: MutableList<String> = ArrayList()
        for (p in permissions) {
            if (!EasyPermissions.hasPermissions(act, p)) {
                list.add(p)
            }
        }
        return list.toTypedArray()
    }

    /**
     * 执行权限请求
     *
     * @param listener 请求结果回调
     */
    fun execute(listener: RequestPermListener) {
        // DES: 是不是改拒绝有点纠结
        val activity = mLifecycle?.activity ?: return
        // DES: 清单文件检查一次  AOP编程和常规写代码不一样，使用者更加简单 开发者代码需要多 所以要多帮助校验
        if (!checkManifestPermission(activity, *mPermissions)) {
            listener.onPermissionsDenied(mPermissions)
            return
        }
        if (EasyPermissions.hasPermissions(activity, *mPermissions)) {
            listener.onAllPermissionsGranted()
            return
        }
        val permissions = getNeedRequestPermissions(activity, mPermissions)
        d("需要请求的权限:" + permissions.contentToString())
        if (activity is FragmentActivity) {
            val supportFragmentManager = activity.supportFragmentManager
            var permissionSupportFragment =
                supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as PermissionSupportFragment?
            if (null == permissionSupportFragment) {
                //makes other transactions execute immediately
                val hasOtherTask = supportFragmentManager.executePendingTransactions()
                d(" begin commit permissionSupportFragment \n begin with another transactions: $hasOtherTask")
                permissionSupportFragment = PermissionSupportFragment()
                supportFragmentManager.beginTransaction()
                    .add(permissionSupportFragment, FRAGMENT_TAG)
                    .commitNowAllowingStateLoss()
            }
            permissionSupportFragment.setPermissions(permissions, listener)
        } else {
            val fragmentManager = activity.fragmentManager
            var permissionFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as PermissionFragment?
            if (null == permissionFragment) {
                //makes other transactions execute immediately
                val hasOtherTask = fragmentManager.executePendingTransactions()
                d(" begin commit permissionFragment \n begin with another transactions: $hasOtherTask")
                permissionFragment = PermissionFragment()
                fragmentManager.beginTransaction()
                    .add(permissionFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss()
                //make it commit like commitNow
                fragmentManager.executePendingTransactions()
            }
            permissionFragment.setPermissions(permissions, listener)
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "#AOP_FRAGMENT_REQUEST_PERMISSION"
        private var mLifecycle: PermissionActivityLifecycle? = null
        fun init(application: Application) {
            if (mLifecycle == null) {
                mLifecycle = PermissionActivityLifecycle()
                application.registerActivityLifecycleCallbacks(mLifecycle)
            } else {
                d("不需要重复初始化")
            }
        }

        /**
         * 获取请求权限实例
         *
         * @return 请求权限工具对象
         */
        @JvmStatic
        val instance: PermissionUtils by lazy { PermissionUtils() }

        /**
         * DES: 解绑Fragment
         * TIME: 2019/6/4 0004 上午 9:51
         */
        @JvmStatic
        fun detachFragment(obj: Any) {
            d("权限请求完成销毁:$obj")
            if (obj is androidx.fragment.app.Fragment) {
                if (!obj.isAdded) return
                obj.fragmentManager?.beginTransaction()?.let {
                    it.detach(obj)
                    it.remove(obj)
                    it.commitNowAllowingStateLoss()
                }
            } else if (obj is android.app.Fragment) {
                if (!obj.isAdded) return
                val fragmentManager = obj.fragmentManager
                fragmentManager?.beginTransaction()?.let {
                    it.detach(obj)
                    it.remove(obj)
                    it.commitAllowingStateLoss()
                }
                fragmentManager.executePendingTransactions()
            }
        }

        /**
         * DES: 设置开启提示
         * TIME: 2019/6/4 0004 上午 9:51
         */
        @JvmStatic
        fun getPermissionLabel(context: Context, vararg permissions: String): String {
            val sb = StringBuilder()
            sb.append("[")
            for (permission in permissions) {
                if (!EasyPermissions.hasPermissions(context, permission)) {
                    val permissionName = getPermissionName(context, permission)
                    if (permissionName.isNotEmpty() && sb.indexOf(permissionName) == -1) {
                        sb.append(permissionName).append(",")
                    }
                }
            }
            sb.deleteCharAt(sb.length - 1)
            sb.append("]")
            val permissionList = sb.toString()
            return permissionList.replace("(\\s\\[.*\\]\\s)\\1+".toRegex(), "$1")
        }

        /**
         * 获取权限的名称,自动按设备语言显示
         *
         * @param context    上下文
         * @param permission 权限
         * @return 权限名称
         */
        private fun getPermissionName(context: Context, permission: String): String {
            var permissionName = ""
            val pm = context.packageManager
            try {
                val permissionInfo = pm.getPermissionInfo(permission, 0)
                val groupInfo = pm?.getPermissionGroupInfo(permissionInfo.group!!, 0)
                permissionName = groupInfo?.loadLabel(pm).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return permissionName
        }
    }
}