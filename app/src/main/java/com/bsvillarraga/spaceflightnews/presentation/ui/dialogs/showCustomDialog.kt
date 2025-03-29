package com.bsvillarraga.spaceflightnews.presentation.ui.dialogs

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showCustomDialog(
    context: Context,
    title: String? = null,
    message: String,
    positiveButtonText: String = "Aceptar",
    negativeButtonText: String? = null,
    neutralButtonText: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    onNeutralClick: (() -> Unit)? = null
) {
    val builder = MaterialAlertDialogBuilder(context)
        .setMessage(message)

    title?.let {
        builder.setTitle(it)
    }

    builder.setPositiveButton(positiveButtonText) { dialog, _ ->
        onPositiveClick?.invoke() ?: dialog.dismiss()
    }

    negativeButtonText?.let {
        builder.setNegativeButton(it) { dialog, _ ->
            onNegativeClick?.invoke() ?: dialog.dismiss()
        }
    }

    neutralButtonText?.let {
        builder.setNeutralButton(it) { dialog, _ ->
            onNeutralClick?.invoke() ?: dialog.dismiss()
        }
    }

    builder.show()
}