package com.asurspace.criminalintent.util

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object UtilPermissions {

    const val PERMISSION_ALL = 1

    val PERMISSIONS = arrayOf(
        permission.READ_EXTERNAL_STORAGE,
        permission.CAMERA
    )

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}