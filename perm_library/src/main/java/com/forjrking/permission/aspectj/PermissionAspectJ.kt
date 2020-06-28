package com.forjrking.permission.aspectj

import android.annotation.SuppressLint
import com.forjrking.permission.AxPermission
import com.forjrking.permission.annotation.Permission
import com.forjrking.permission.annotation.PermissionDenied
import com.forjrking.permission.consts.Constant.getPermissions
import com.forjrking.permission.permission.PermissionUtils
import com.forjrking.permission.permission.RequestPermListener
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import java.lang.reflect.Method

/**
 * DES: 权限申请切片  借用github 开源切片库
 * CHANGED: 岛主
 * TIME: 2019/2/14 0014 上午 11:20
 */
@Aspect
class PermissionAspectJ {

    companion object {
        const val PACKAGE = "com.forjrking.permission.annotation.Permission"
    }

    @Pointcut("within(@$PACKAGE *)")
    fun withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    fun methodInsideAnnotatedType() {
    }

    @Pointcut("execution(@$PACKAGE * *(..)) || methodInsideAnnotatedType()")
    fun method() {
    } //方法切入点

    @SuppressLint("WrongConstant")
    @Around("method() && @annotation(permission)")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint, permission: Permission) {
        // DES: 原始注入需要处理一次
        val perms: Array<String> = permission.permissions
        // DES: 真实的请求单个权限数组
        val permissions: Array<String> = getPermissions(*perms)
        val target = joinPoint.target

        PermissionUtils.instance
            .request(*permissions)
            .execute(object : RequestPermListener {
                override fun onAllPermissionsGranted() {
                    //执行原有方法
                    joinPoint.proceed()
                    AxPermission.listener?.onAllPermissionsGranted()
                }

                override fun onPermissionsGranted(perms: Array<String>?) {
                    AxPermission.listener?.onAllPermissionsGranted()
                }

                override fun onPermissionsDenied(deniedPerms: Array<String>) {
                    try {
                        val deniedMethod = deniedCallback(target)
                            ?: throw IllegalArgumentException(target.javaClass.simpleName + " 中无 @PermDenied 注解方法 ")
                        // DES: 需要监听的回调权限
                        val annotation = deniedMethod.getAnnotation(PermissionDenied::class.java)
                        val permission = getPermissions(*annotation.intercepts)
                        val types = deniedMethod.parameterTypes
                        if (permission.isEmpty() || hasPerm(deniedPerms, *permission)) {
                            if (types.size == 1 && types[0].isArray
                                && types[0].componentType == String::class.java
                            ) {
                                deniedMethod.invoke(target, deniedPerms)
                            } else if (types.isEmpty()) {
                                deniedMethod.invoke(target)
                            } else {
                                throw IllegalArgumentException("@PermissionDenied 注解方法只允许一个(String[])参数或者无参")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    AxPermission.listener?.onPermissionsDenied(deniedPerms)
                }
            })
    }

    /**
     * DES: 权限拒绝后的回调 注解
     * TIME: 2019/6/3 0003 下午 9:21
     */
    fun deniedCallback(`object`: Any): Method? {
        val aClass: Class<*> = `object`.javaClass
        for (method in aClass.declaredMethods) {
            val isCallback = method.isAnnotationPresent(PermissionDenied::class.java)
            if (!isCallback) continue
            method.isAccessible = true
            return method
        }
        return null
    }

    fun hasPerm(deniedPerms: Array<String>, vararg perm: String): Boolean {
        for (p in perm) {
            if (deniedPerms.contains(p)) {
                return true
            }
        }
        return false
    }
}