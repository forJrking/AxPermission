package com.forjrking.permission.permission

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.forjrking.permission.util.XLogger
import java.util.*

/**
 * @Description: XML中是否申请相关权限
 * @Author: 岛主
 * @CreateDate: 2019/6/3 0003 下午 7:26
 * @Version: 1.0.0
 */
object PermissionXmlCheck {
    /**
     * 检查请求的权限是否都在清单文件中注册
     */
    fun checkManifestPermission(context: Context, vararg perms: String?): Boolean {
        val notRegPermissions = getNotRegPermissions(context, *perms)
        if (notRegPermissions.isEmpty()) {
            return true
        }
        val sb = StringBuilder()
        for (notRegPermission in notRegPermissions) {
            sb.append(" [")
                .append(notRegPermission)
                .append("] ")
        }
        sb.append("权限未在清单文件注册!")
        val permissionList = sb.toString()
        val s = permissionList.replace("(\\s\\[.*\\]\\s)\\1+".toRegex(), "$1")
        XLogger.e("MagicPermission Error: $s")
        try {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
        }
        return false
    }

    /**
     * 获取动态申请却没有在清单文件注册的权限
     */
    private fun getNotRegPermissions(context: Context, vararg perms: String?): List<String> {
        val requiredPermissions = getRequiredPermissions(context)
        val notReg: MutableList<String> = ArrayList()
        for (permission in perms) {
            if (!requiredPermissions.contains(permission)) {
                if (permission != null) {
                    notReg.add(permission)
                }
            }
        }
        return notReg
    }

    /**
     * 获取清单文件中注册的权限
     */
    private fun getRequiredPermissions(context: Context): Array<String?> {
        return try {
            val info = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.isNotEmpty()) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            arrayOfNulls(0)
        }
    }
}