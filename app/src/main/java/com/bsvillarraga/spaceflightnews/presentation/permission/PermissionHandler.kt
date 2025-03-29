package com.bsvillarraga.spaceflightnews.presentation.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bsvillarraga.spaceflightnews.core.extensions.isPermissionGranted
import com.bsvillarraga.spaceflightnews.presentation.ui.dialogs.showCustomDialog

class PermissionHandler(
    private val fragment: Fragment
) {
    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null
    private var onPermissionDeniedPermanently: (() -> Unit)? = null

    private val permissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            handlePermissionsResult(permissions)
        }

    fun requestPermissions(
        permissions: Array<String>,
        onGranted: () -> Unit,
        onDenied: (() -> Unit)? = null,
        onDeniedPermanently: (() -> Unit)? = null
    ) {
        this.onPermissionGranted = onGranted
        this.onPermissionDenied = onDenied
        this.onPermissionDeniedPermanently = onDeniedPermanently

        val context = fragment.requireContext()
        if (permissions.all { context.isPermissionGranted(it) }) {
            onPermissionGranted?.invoke()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val context = fragment.requireContext()

        val allGranted = permissions.all { it.value }

        if (allGranted) {
            onPermissionGranted?.invoke()
        } else {
            val permanentlyDenied = permissions.filter { (permission, isGranted) ->
                !isGranted && !fragment.shouldShowRequestPermissionRationale(permission)
            }

            if (permanentlyDenied.isNotEmpty()) {
                onPermissionDeniedPermanently?.invoke()
                showPermissionSettingsDialog(context)
            } else {
                onPermissionDenied?.invoke()
                Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionSettingsDialog(context: Context) {
        showCustomDialog(
            context = context,
            title = "Permiso necesario",
            message = "Esta aplicación necesita permisos para funcionar correctamente. Por favor, habilítalos desde la configuración de la aplicación.",
            positiveButtonText = "Abrir configuración",
            neutralButtonText = "Cancelar",
            onPositiveClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        )
    }
}