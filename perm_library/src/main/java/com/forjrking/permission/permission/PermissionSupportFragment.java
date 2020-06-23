package com.forjrking.permission.permission;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.forjrking.permission.R;
import com.forjrking.permission.util.XLogger;

import java.util.ArrayList;
import java.util.List;

import static com.forjrking.permission.permission.EasyPermissions.PERM_REQ_CODE;
import static com.forjrking.permission.permission.EasyPermissions.SETTINGS_REQ_CODE;


/**
 * @author Administrator
 */
public class PermissionSupportFragment extends Fragment implements EasyPermissions.PermissionCallbacks {


    private RequestPermListener mListener;
    private String[] mPermissions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            XLogger.d("权限被异常关闭，重建中");
        }
        setRetainInstance(true);
    }

    public void setPermissions(String[] permissions, RequestPermListener listener) {
        mPermissions = permissions;
        mListener = listener;
        // DES: 可以在请求前加上自己的弹窗申请被拒绝的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String label = PermissionUtils.getPermissionLabel(getActivity(), mPermissions);
            String rationale = getString(R.string.permission_should_show_rationale, label);
            EasyPermissions.requestPermissions(this, rationale, PERM_REQ_CODE, mPermissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> granted) {
        if (!granted.isEmpty()) {
            String[] granteds = new String[granted.size()];
            mListener.onPermissionsGranted(granted.toArray(granteds));
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> denied) {
        if (!denied.isEmpty()) {
            String[] denieds = new String[denied.size()];
            mListener.onPermissionsDenied(denied.toArray(denieds));
            Activity activity = getActivity();
            if (requestCode == PERM_REQ_CODE && PermissionActivityLifecycle.isAvailable(activity)) {
                // DES: 获取拒绝的权限的名称集合
                int labelRes = activity.getApplication().getApplicationInfo().labelRes;
                String name = activity.getResources().getString(labelRes);
                String label = PermissionUtils.getPermissionLabel(activity, denieds);
                String rationale = getString(R.string.permission_again_show_rationale, name, label);
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.detachFragment(PermissionSupportFragment.this);
                    }
                };
                boolean askAgain = EasyPermissions.checkDeniedPermissionsNeverAskAgain(this, rationale, R.string.aop_permission_tip
                        , R.string.aop_permission_setting, R.string.aop_permission_denied, onClickListener, denied);
                if (!askAgain) {
                    onClickListener.onClick(null, 0);
                }
            } else {
                PermissionUtils.detachFragment(this);
            }
        }
    }

    @Override
    public void onPermissionsAllGranted() {
        mListener.onAllPermissionsGranted();
        PermissionUtils.detachFragment(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPermissions != null && mListener != null) {
            FragmentActivity activity = getActivity();
            XLogger.d("onActivityResult" + requestCode);
            if (requestCode == SETTINGS_REQ_CODE && PermissionActivityLifecycle.isAvailable(activity)) {
                List<String> granted = new ArrayList<String>();
                List<String> denied = new ArrayList<String>();
                for (String perm : mPermissions) {
                    if (EasyPermissions.hasPermissions(activity, perm)) {
                        granted.add(perm);
                    } else {
                        denied.add(perm);
                    }
                }
                if (!granted.isEmpty() && denied.isEmpty()) {
                    onPermissionsAllGranted();
                } else {
                    onPermissionsGranted(requestCode, granted);
                    onPermissionsDenied(requestCode, denied);
                }
            }
        }
    }

}