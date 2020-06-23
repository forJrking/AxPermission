package com.forjrking.permission.annotation

import com.forjrking.permission.consts.Constant
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * DES: 权限申请切片 需要关注部分拒绝需要 [PermissionDenied]
 * @author 岛主
 * TIME: 2019/2/14 0014 上午 11:29
 * @Manifest.permission.xxx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
annotation class Permission(
    /*** @return 需要申请权限的集合 */
    @Constant.Restriction
    val permissions : Array<String>
)