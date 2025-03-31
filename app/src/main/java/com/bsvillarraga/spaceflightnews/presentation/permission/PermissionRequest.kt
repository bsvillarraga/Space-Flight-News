package com.bsvillarraga.spaceflightnews.presentation.permission

/**
 * Representa una solicitud de permiso dentro de la aplicación.
 *
 * Esta clase encapsula el tipo de permiso que se solicita y las acciones a ejecutar
 * en función de la respuesta del usuario.
 *
 * @property type Tipo de permiso a solicitar (definido en [PermissionType]).
 * @property onGranted Acción a ejecutar cuando el permiso es otorgado.
 * @property onDenied Acción a ejecutar cuando el permiso es denegado temporalmente.
 * @property onDeniedPermanently Acción a ejecutar cuando el usuario deniega el permiso de forma permanente.
 */

data class PermissionRequest(
    val type: PermissionType,
    val onGranted: () -> Unit = {},
    val onDenied: () -> Unit = {},
    val onDeniedPermanently: () -> Unit = {}
)

