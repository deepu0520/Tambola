package com.newitzone.desitambola.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher


object PermissionUtil {
    interface PermissionCallback {
        fun onPermissionGranted()
        fun onPermissionDenied()
        fun onPermissionPermanentlyDenied()
    }

    fun requestPermission(
        activity: Activity,
        permission: String,
        launcher: ActivityResultLauncher<String>,
        callback: PermissionCallback
    ) {
        when {
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                callback.onPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                showRationaleDialog(activity) {
                    launcher.launch(permission)
                }
            }

            else -> {
                launcher.launch(permission)
            }
        }
    }

    fun handlePermissionResult(
        activity: Activity,
        permission: String,
        isGranted: Boolean,
        callback: PermissionCallback
    ) {
        if (isGranted) {
            callback.onPermissionGranted()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                callback.onPermissionDenied()
            } else {
                callback.onPermissionPermanentlyDenied()
            }
        }
    }

    private fun showRationaleDialog(context: Context, onProceed: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Permission Required")
            .setMessage("This feature needs permission. Please grant it to proceed.")
            .setPositiveButton("Proceed") { _, _ -> onProceed() }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }
}
