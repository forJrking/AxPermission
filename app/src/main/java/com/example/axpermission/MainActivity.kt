package com.example.axpermission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.forjrking.permission.AxPermission
import com.forjrking.permission.annotation.Permission
import com.forjrking.permission.annotation.PermissionDenied
import com.forjrking.permission.consts.Constant
import com.forjrking.permission.permission.RequestPermListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //开启日志调试
        AxPermission.isdebug = true
        //全局拦截接口
        AxPermission.listener = object : RequestPermListener {

            override fun onPermissionsDenied(perms: Array<String>) {}

            override fun onPermissionsGranted(perms: Array<String>?) {}

            override fun onAllPermissionsGranted() {}
        }
    }

    @Permission(["AAA"])
    fun textClick(@Constant.Restriction view: View) {

    }

    @PermissionDenied(["a"])
    fun onDie() {

    }
}