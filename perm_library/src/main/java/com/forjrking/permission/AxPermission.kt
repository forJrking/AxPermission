package com.forjrking.permission

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.forjrking.permission.permission.PermissionUtils
import com.forjrking.permission.permission.RequestPermListener

/**
 * @Description:
 * @Author: forJrking
 * @Copyright: 诺瓦
 * @CreateDate: 2020/6/23 18:06
 * @Version: 1.0.0
 */
class AxPermission : ContentProvider() {

    companion object {
        @JvmStatic
        var isdebug = false

        @JvmStatic
        var listener: RequestPermListener? = null
    }

    override fun onCreate(): Boolean {
        PermissionUtils.init(context?.applicationContext as Application?)
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun query(
        uri: Uri, projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null


}