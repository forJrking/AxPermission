package com.example.axpermission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.forjrking.permission.annotation.Permission
import com.forjrking.permission.annotation.PermissionDenied
import com.forjrking.permission.consts.Constant

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Permission(permissions = [Constant.G_CAMERA])
    fun textClick(view: View) {

    }

    @PermissionDenied
    fun onDie(){

    }
}