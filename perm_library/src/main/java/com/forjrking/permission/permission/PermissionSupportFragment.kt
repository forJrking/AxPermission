package com.forjrking.permission.permission

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.forjrking.permission.R
import com.forjrking.permission.permission.EasyPermissions.PermissionCallbacks
import com.forjrking.permission.permission.PermissionActivityLifecycle.Companion.isAvailable
import com.forjrking.permission.permission.PermissionUtils.Companion.detachFragment
import com.forjrking.permission.permission.PermissionUtils.Companion.getPermissionLabel
import com.forjrking.permission.util.XLogger.d
import java.util.*

/**
 * @author Administrator
 */
class PermissionSupportFragment : Fragment(), PermissionCallbacks {

    private var mListener: RequestPermListener? = null
    private lateinit var mPermissions: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            d("权限被异常关闭，重建中")
        }
        retainInstance = true
    }

    fun setPermissions(permissions: Array<String>, listener: RequestPermListener) {
        mPermissions = permissions
        mListener = listener
        // DES: 可以在请求前加上自己的弹窗申请被拒绝的权限
        val label = getPermissionLabel(activity!!, *mPermissions!!)
        val rationale = getString(R.string.permission_should_show_rationale, label)
        EasyPermissions.requestPermissions(this, rationale, mPermissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, granted: List<String>) {
        if (granted.isNotEmpty()) {
            mListener?.onPermissionsGranted(granted.toTypedArray())
        }
    }

    override fun onPermissionsDenied(requestCode: Int, denied: List<String>) {
        if (denied.isNotEmpty()) {
            val denieds = denied.toTypedArray()
            mListener?.onPermissionsDenied(denieds)
            if (requestCode == EasyPermissions.PERM_REQ_CODE && isAvailable(activity)) {
                // DES: 获取拒绝的权限的名称集合
                val labelRes = activity!!.applicationInfo.labelRes
                val name = activity!!.resources.getString(labelRes)
                val label = getPermissionLabel(activity!!, *denieds)
                val rationale = getString(R.string.permission_again_show_rationale, name, label)
                val onClickListener = DialogInterface.OnClickListener { _, _ ->
                    detachFragment(this@PermissionSupportFragment)
                }
                val askAgain = EasyPermissions.checkDeniedPermissionsNeverAskAgain(
                    this,
                    rationale,
                    R.string.aop_permission_tip,
                    R.string.aop_permission_setting,
                    R.string.aop_permission_denied,
                    onClickListener,
                    denied
                )
                if (!askAgain) {
                    onClickListener.onClick(null, 0)
                }
            } else {
                detachFragment(this)
            }
        }
    }

    override fun onPermissionsAllGranted() {
        mListener?.onAllPermissionsGranted()
        detachFragment(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (mPermissions != null && mListener != null) {
            d("onActivityResult$requestCode")
            if (requestCode == EasyPermissions.SETTINGS_REQ_CODE && isAvailable(activity)) {
                val granted: MutableList<String> = ArrayList()
                val denied: MutableList<String> = ArrayList()
                for (perm in mPermissions!!) {
                    if (EasyPermissions.hasPermissions(activity, perm)) {
                        granted.add(perm)
                    } else {
                        denied.add(perm)
                    }
                }
                if (granted.isNotEmpty() && denied.isEmpty()) {
                    onPermissionsAllGranted()
                } else {
                    onPermissionsGranted(requestCode, granted)
                    onPermissionsDenied(requestCode, denied)
                }
            }
        }
    }
}