package com.forjrking.permission.annotation

import com.forjrking.permission.consts.Restriction

/**
 * DES: 权限申请切片 需要关注部分拒绝需要 [PermissionDenied]
 * TIME: 2019/2/14 0014 上午 11:29
 * @author 岛主
 * @Manifest.permission.xxx
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class Permission(
    /**
     * @return 需要申请权限的集合
     */
    val permissions: Array<String>
)