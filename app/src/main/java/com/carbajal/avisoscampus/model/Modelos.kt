package com.carbajal.avisoscampus.model

enum class PermisoUi {
    SIN_PEDIR,
    PERMITIDO,
    DENEGADO
}

enum class Origen(val texto: String) {
    BOTON("Botón"),
    RECORDATORIO("Recordatorio"),
    MODO_AVION("Modo avión"),
    CARGADOR("Cargador")
}

data class Aviso(
    val id: Long = System.currentTimeMillis(),
    val origen: Origen,
    val titulo: String,
    val texto: String,
    val timestamp: Long = System.currentTimeMillis()
)
