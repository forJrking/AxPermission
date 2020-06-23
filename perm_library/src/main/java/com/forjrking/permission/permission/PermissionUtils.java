package com.forjrking.permission.permission;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.forjrking.permission.util.XLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 安卓8.0以下申请一个权限，用户同意后整个权限组的权限都不用申请可以直接使用
 * 8.0后每个权限都要单独申请，不能一次申请通过后,整个权限组都不用申请;
 * 但是用户同意权限组内一个之后，其它申请直接通过(之前是不申请可以直接用，现在是申请直接过(但是必须申请))
 *
 * @author 岛主
 */

public class PermissionUtils {

    private static String FRAGMENT_TAG = "Aop_fragmentRequestPermission";
    private static PermissionActivityLifecycle mLifecycle;
    private String[] mPermissions;

    public static void init(Application application) {
        if (mLifecycle == null) {
            mLifecycle = new PermissionActivityLifecycle();
            application.registerActivityLifecycleCallbacks(mLifecycle);
        } else {
            XLogger.d("不需要重复初始化");
        }
    }

    /**
     * 获取请求权限实例
     *
     * @return 请求权限工具对象
     */
    public static PermissionUtils getInstance() {
        return new PermissionUtils();
    }

    /**
     * 需要请求的权限列表
     *
     * @param permissions 权限列表
     * @return 返回自身链式编程
     */
    public PermissionUtils request(String... permissions) {
        mPermissions = permissions;
        return this;
    }

    /**
     * 筛选出需要申请的权限
     *
     * @param permissions 权限列表
     * @return 需要申请的权限
     */
    private String[] getNeedRequestPermissions(Activity act, String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String p : permissions) {
            if (!EasyPermissions.hasPermissions(act, p)) {
                list.add(p);
            }
        }
        String[] needRequest = new String[list.size()];
        return list.toArray(needRequest);
    }

    /**
     * 执行权限请求
     *
     * @param listener 请求结果回调
     */
    public void execute(RequestPermListener listener) {
        Activity activity = mLifecycle.getActivity();
        if (activity == null) {
            // DES: 是不是改拒绝有点纠结
            return;
        }
        // DES: 清单文件检查一次  AOP编程和常规写代码不一样，使用者更加简单 开发者代码需要多 所以要多帮助校验
        if (!PermissionXmlCheck.checkManifestPermission(activity, mPermissions)) {
            if (listener != null) {
                listener.onPermissionsDenied(mPermissions);
            }
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || EasyPermissions.hasPermissions(activity, mPermissions)) {
            if (listener != null) {
                listener.onAllPermissionsGranted();
            }
            return;
        }

        String[] permissions = getNeedRequestPermissions(activity, mPermissions);
        XLogger.d("需要请求的权限:" + Arrays.toString(permissions));
        if (activity instanceof FragmentActivity) {
            FragmentManager supportFragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            PermissionSupportFragment permissionSupportFragment = (PermissionSupportFragment) supportFragmentManager.findFragmentByTag(FRAGMENT_TAG);
            if (null == permissionSupportFragment) {
                //makes other transactions execute immediately
                boolean hasOtherTask = supportFragmentManager.executePendingTransactions();
                XLogger.d(" begin commit permissionSupportFragment \n begin with another transactions: " + hasOtherTask);
                permissionSupportFragment = new PermissionSupportFragment();
                supportFragmentManager.beginTransaction()
                        .add(permissionSupportFragment, FRAGMENT_TAG)
                        .commitNowAllowingStateLoss();
            }
            permissionSupportFragment.setPermissions(permissions, listener);
        } else {
            android.app.FragmentManager fragmentManager = activity.getFragmentManager();
            PermissionFragment permissionFragment = (PermissionFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
            if (null == permissionFragment) {
                //makes other transactions execute immediately
                boolean hasOtherTask = fragmentManager.executePendingTransactions();
                XLogger.d(" begin commit permissionFragment \n begin with another transactions: " + hasOtherTask);
                permissionFragment = new PermissionFragment();
                fragmentManager.beginTransaction()
                        .add(permissionFragment, FRAGMENT_TAG)
                        .commitAllowingStateLoss();
                //make it commit like commitNow
                fragmentManager.executePendingTransactions();
            }
            permissionFragment.setPermissions(permissions, listener);
        }
    }

    /**
     * DES: 解绑Fragment
     * TIME: 2019/6/4 0004 上午 9:51
     */
    public static void detachFragment(Object obj) {
        XLogger.d("权限请求完成销毁:" + obj.toString());
        if (obj instanceof Fragment) {
            Fragment fragmentSupport = (Fragment) obj;
            if (!fragmentSupport.isAdded()) return;
            FragmentTransaction transaction = fragmentSupport.getFragmentManager().beginTransaction();
            transaction.detach(fragmentSupport);
            transaction.remove(fragmentSupport);
            transaction.commitNowAllowingStateLoss();
        } else if (obj instanceof android.app.Fragment) {
            android.app.Fragment fragmentApp = (android.app.Fragment) obj;
            if (!fragmentApp.isAdded()) return;
            android.app.FragmentManager fragmentManager = fragmentApp.getFragmentManager();
            android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.detach(fragmentApp);
            fragmentTransaction.remove(fragmentApp);
            fragmentTransaction.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
    }

    /**
     * DES: 设置开启提示
     * TIME: 2019/6/4 0004 上午 9:51
     */
    public static String getPermissionLabel(Context context, String... permissions) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String permission : permissions) {
            if (!EasyPermissions.hasPermissions(context, permission)) {
                String permissionName = PermissionUtils.getPermissionName(context, permission);
                if (!permissionName.isEmpty() && sb.indexOf(permissionName) == -1) {
                    sb.append(permissionName).append(",");
                }
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        String permissionList = sb.toString();
        return permissionList.replaceAll("(\\s\\[.*\\]\\s)\\1+", "$1");
    }

    /**
     * 获取权限的名称,自动按设备语言显示
     *
     * @param context    上下文
     * @param permission 权限
     * @return 权限名称
     */
    private static String getPermissionName(Context context, String permission) {
        String permissionName = "";
        PackageManager pm = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = pm.getPermissionInfo(permission, 0);
            PermissionGroupInfo groupInfo = pm.getPermissionGroupInfo(permissionInfo.group, 0);
            permissionName = groupInfo.loadLabel(pm).toString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permissionName;
    }
}