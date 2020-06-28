package com.forjrking.permission.consts;

import android.Manifest.permission;

import androidx.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Description: 限制权限输入 目前无法对数组输入做限制
 * @Author: forJrking
 * @CreateDate: 2020/6/28 16:36
 * @Version: 1.0.0
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@StringDef({
        Constant.G_SMS,
        Constant.G_PHONE,
        Constant.G_CAMERA,
        Constant.G_SENSORS,
        Constant.G_STORAGE,
        Constant.G_CALENDAR,
        Constant.G_LOCATION,
        Constant.G_CONTACTS,
        Constant.G_MICROPHONE,
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
})
public @interface Restriction {}