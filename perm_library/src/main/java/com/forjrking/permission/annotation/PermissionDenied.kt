package com.forjrking.permission.annotation

import com.forjrking.permission.consts.Constant
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * DES: 权限申请切片
 * TIME: 2019/2/14 0014 上午 11:29
 * @author 岛主
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
annotation class PermissionDenied(
    /*** @return 需要申请权限的集合 */
    @Constant.Restriction
    val value: Array<String> = []
)