package com.bsvillarraga.spaceflightnews.presentation.permission

import android.Manifest

/**
 * Clase encargada de gestionar solicitudes de permisos en cadena.
 *
 * "PermissionChainManager" permite agregar múltiples solicitudes de permisos y ejecutarlas
 * de manera secuencial. Cada permiso se solicita y, dependiendo de la respuesta del usuario,
 * se continúa con la siguiente solicitud o se finaliza el proceso.
 *
 * @param permissionHandler Manejador encargado de solicitar los permisos al sistema.
 */
class PermissionChainManager(
    private val permissionHandler: PermissionHandler
) {
    private val requestsQueue = ArrayDeque<PermissionRequest>()

    /**
     * Agrega una solicitud de permiso a la cadena.
     *
     * @param request Instancia de [PermissionRequest] que define el permiso solicitado
     * y las acciones a tomar en caso de concesión o denegación.
     * @return [PermissionChainManager] para permitir encadenamiento de llamadas.
     */

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