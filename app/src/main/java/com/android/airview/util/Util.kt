package com.android.airview.util

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.airview.R
import com.android.airview.ui.main.MainFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


object Util {

    // Create bitmap from drawable by res id
    fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor? {
        if (context != null) {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            vectorDrawable!!.setBounds(
                0,
                0,
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight
            )
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        return null
    }

    // Request a permission from user
    fun requestPermission(fragment: Fragment?, permissions: List<String>) {
        fragment?.let { frg ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                frg.requestPermissions(
                    permissions.toTypedArray(),
                    MainFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }

        }
    }

    // Check for a permission list
    fun hasPermissions(context: Context?, permissions: List<String>): Boolean {
        context?.let { ctx ->
            val neededPermissions = mutableListOf<String>()
            for (perm in permissions) {
                if (!hasPermission(ctx, perm)) {
                    neededPermissions.add(perm)
                }
            }

            if (neededPermissions.isEmpty())
                return true

            return false
        } ?: run {
            return false
        }
    }

    // Check for a single permission
    fun hasPermission(context: Context?, permission: String): Boolean {
        context?.let { ctx ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ctx.checkSelfPermission(
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }

            return true
        } ?: run {
            return false
        }
    }

    // Check for play service
    fun checkPlayServices(activity: Activity?): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    activity,
                    resultCode,
                    MainFragment.PLAY_SERVICES_RESOLUTION_REQUEST
                )
            } else {
                activity?.finish()
            }

            return false
        }

        return true
    }

    // Create a dialog with custom layout
    fun createLoadingProgress(
        layoutId: Int,
        context: Context
    ): Dialog = Dialog(context, R.style.Theme_BlurredTheme).apply {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.setContentView(layoutId)
        this.setCancelable(false)
        window?.setBackgroundDrawableResource(R.color.shadow)
    }

    fun createInfoDialog(context: Context, message: String, callback: (Boolean) -> Unit) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(
                context.getString(R.string.label_ok)
            ) { _, _ ->
                callback(true)
            }.setNegativeButton(context.getString(R.string.label_cancel)) { _, _ ->
                callback(false)
            }.create().show()
    }

}