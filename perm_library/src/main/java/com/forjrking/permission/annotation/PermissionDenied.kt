package com.forjrking.permission.annotation

/**
 * DES: 权限申请切片
 * TIME: 2019/2/14 0014 上午 11:29
 * @author 岛主
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class PermissionDenied(
    /**
     * @return 需要申请权限的集合
     */
    val intercepts: Array<String> = []
)