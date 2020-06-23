package com.forjrking.permission.consts

import android.Manifest
import android.Manifest.permission
import androidx.annotation.StringDef
import com.forjrking.permission.consts.Constant.Restriction
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * DES: 封装常见的高危权限
 * [Permission]注解搭配使用 目的为少些权限，便于阅读
 * G_xxx 开头的为权限组
 * Manifest.permission.xxx 为单个权限可以随意添加到 [Restriction] 中
 * CHANGED: 岛主
 * TIME: 2019/6/4 0004 下午 4:22
 */
object Constant {
    /*** DES: 权限组 包含一组多个类型权限 */
    const val G_CALENDAR = Manifest.permission_group.CALENDAR
    const val G_CAMERA = Manifest.permission_group.CAMERA
    const val G_CONTACTS = Manifest.permission_group.CONTACTS
    const val G_LOCATION = Manifest.permission_group.LOCATION
    const val G_MICROPHONE = Manifest.permission_group.MICROPHONE
    const val G_PHONE = Manifest.permission_group.PHONE
    const val G_SENSORS = Manifest.permission_group.SENSORS
    const val G_SMS = Manifest.permission_group.SMS
    const val G_STORAGE = Manifest.permission_group.STORAGE

    private val GROUP_CALENDAR = arrayOf(
        permission.READ_CALENDAR, permission.WRITE_CALENDAR
    )
    private val GROUP_CAMERA = arrayOf(
        permission.CAMERA
    )
    private val GROUP_CONTACTS = arrayOf(
        permission.READ_CONTACTS, permission.WRITE_CONTACTS, permission.GET_ACCOUNTS
    )
    private val GROUP_LOCATION = arrayOf(
        permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION
    )
    private val GROUP_MICROPHONE = arrayOf(
        permission.RECORD_AUDIO
    )
    private val GROUP_PHONE = arrayOf(
        permission.READ_PHONE_STATE, permission.MODIFY_PHONE_STATE, permission.CALL_PHONE,
        permission.READ_CALL_LOG, permission.WRITE_CALL_LOG,
        permission.ADD_VOICEMAIL, permission.USE_SIP, permission.PROCESS_OUTGOING_CALLS
    )
    private val GROUP_SENSORS = arrayOf(
        permission.BODY_SENSORS
    )
    private val GROUP_SMS = arrayOf(
        permission.SEND_SMS, permission.RECEIVE_SMS, permission.READ_SMS,
        permission.RECEIVE_WAP_PUSH, permission.RECEIVE_MMS
    )
    private val GROUP_STORAGE = arrayOf(
        permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * DES: 把权限组和单独权限等全部转换为 单独权限的数组
     * TIME: 2019/6/4 0004 下午 4:37
     */
    fun getPermissions(vararg perms: String): Array<String> {
        val perm = ArrayList<String>()
        for (permission in perms) {
            perm.addAll(group2Permissions(permission))
        }
        val permArray = arrayOfNulls<String>(perm.size)
        return perm.toArray(permArray)
    }

    private fun group2Permissions(@Restriction permission: String): Array<String> {
        // DES: 优先处理权限组
        return when (permission) {
            G_CALENDAR -> GROUP_CALENDAR
            G_CAMERA, Manifest.permission.CAMERA -> GROUP_CAMERA
            G_CONTACTS -> GROUP_CONTACTS
            G_LOCATION -> GROUP_LOCATION
            G_MICROPHONE, Manifest.permission.RECORD_AUDIO -> GROUP_MICROPHONE
            G_PHONE -> GROUP_PHONE
            G_SENSORS -> GROUP_SENSORS
            G_SMS -> GROUP_SMS
            G_STORAGE -> GROUP_STORAGE
            else -> arrayOf(permission)
        }
    }

    @StringDef(
        G_CALENDAR,
        G_CAMERA,
        G_CONTACTS,
        G_LOCATION,
        G_MICROPHONE,
        G_PHONE,
        G_SENSORS,
        G_SMS,
        G_STORAGE,
        permission.READ_EXTERNAL_STORAGE,
        permission.WRITE_EXTERNAL_STORAGE,
        permission.READ_CALENDAR,
        permission.WRITE_CALENDAR,
        permission.CAMERA,
        permission.READ_CONTACTS,
        permission.WRITE_CONTACTS,
        permission.GET_ACCOUNTS,
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION,
        permission.RECORD_AUDIO,
        permission.BODY_SENSORS,
        permission.READ_PHONE_STATE,
        permission.MODIFY_PHONE_STATE,
        permission.CALL_PHONE,
        permission.READ_CALL_LOG,
        permission.WRITE_CALL_LOG,
        permission.ADD_VOICEMAIL,
        permission.USE_SIP,
        permission.PROCESS_OUTGOING_CALLS,
        permission.SEND_SMS,
        permission.RECEIVE_SMS,
        permission.READ_SMS,
        permission.RECEIVE_WAP_PUSH,
        permission.RECEIVE_MMS
    )
    @Retention(RetentionPolicy.SOURCE)
    annotation class Restriction
}