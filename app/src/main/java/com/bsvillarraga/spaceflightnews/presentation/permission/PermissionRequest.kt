package com.bsvillarraga.spaceflightnews.presentation.permission

data class PermissionRequest(
    val type: PermissionType,
    val onGranted: () -> Unit = {},
    val onDenied: () -> Unit = {},
    val onDeniedPermanently: () -> Unit = {}
)

