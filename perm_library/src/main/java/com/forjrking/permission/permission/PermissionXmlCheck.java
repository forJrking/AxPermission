package com.forjrking.permission.permission;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: XML中是否申请相关权限
 * @Author: 岛主
 * @CreateDate: 2019/6/3 0003 下午 7:26
 * @Version: 1.0.0
 */
public class PermissionXmlCheck {
    /**
     * 检查动态权限是否都在清单文件注册
     */
    public static boolean checkManifestPermission(Context context, String... perms) {
        List<String> notRegPermissions = getNotRegPermissions(context, perms);
        boolean empty = notRegPermissions.isEmpty();
        if (empty) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (String notRegPermission : notRegPermissions) {
            sb.append(" [")
                    .append(notRegPermission)
                    .append("] ");
        }
        sb.append("权限未在清单文件注册!");
        String permissionList = sb.toString();
        String s = permissionList.replaceAll("(\\s\\[.*\\]\\s)\\1+", "$1");
        Log.e("MagicPermission Error: ", s);
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 获取动态申请却没有在清单文件注册的权限
     */
    private static List<String> getNotRegPermissions(Context context, String... perms) {
        String[] requiredPermissions = getRequiredPermissions(context);
        List<String> list = Arrays.asList(requiredPermissions);
        List<String> notReg = new ArrayList<>();
        for (String permission : perms) {
            if (!list.contains(permission)) {
                notReg.add(permission);
            }
        }
        return notReg;
    }

    /**
     * 获取清单文件中注册的权限
     */
    private static String[] getRequiredPermissions(Context context) {
        try {
            PackageInfo info =
                    context.getPackageManager()
                            .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }
}
