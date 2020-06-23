package com.forjrking.permission.permission

/**
 * DES: 权限请求的监听器
 * CHANGED: 岛主
 * TIME: 2019/6/4 0004 上午 9:58
 */
interface RequestPermListener {
    /**
     * 权限部分ok，可做后续部分的事情
     *
     * @param perms 允许的权限
     */
    fun onPermissionsGranted(perms: Array<String>?) {}

    /**
     * 本次申请的权限全部ok，可做后续的事情
     */
    fun onAllPermissionsGranted() {}

    /**
     * 权限不ok，被拒绝或者未授予
     *
     * @param perms 拒绝的权限
     */
    fun onPermissionsDenied(perms: Array<String>)
}