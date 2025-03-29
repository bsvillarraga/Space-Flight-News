package com.bsvillarraga.spaceflightnews.presentation.permission

import android.Manifest

class PermissionChainManager(
    private val permissionHandler: PermissionHandler
) {
    private val requestsQueue = ArrayDeque<PermissionRequest>()

    fun addPermission(request: PermissionRequest): PermissionChainManager {
        requestsQueue.add(request)
        return this
    }

    fun execute() {
        processNext()
    }

    private fun processNext() {
        val nextRequest = requestsQueue.removeFirstOrNull() ?: return

        when (nextRequest.type) {
            PermissionType.RECORD_AUDIO -> permissionHandler.requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                onGranted = {
                    nextRequest.onGranted()
                    processNext()
                },
                onDenied = {
                    nextRequest.onDenied()
                },
                onDeniedPermanently = {
                    nextRequest.onDeniedPermanently()
                }
            )
        }
    }
}