package com.carbajal.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.avisoscampus.data.AvisosRepository
import com.carbajal.avisoscampus.model.Aviso
import com.carbajal.avisoscampus.model.Origen
import com.carbajal.avisoscampus.model.PermisoUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(
    private val repository: AvisosRepository,
    private val mostrarNotificacion: (Aviso) -> Boolean
) : ViewModel() {

    private val _permiso = MutableStateFlow(PermisoUi.SIN_PEDIR)
    val permiso: StateFlow<PermisoUi> = _permiso.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    val avisos: StateFlow<List<Aviso>> = repository.avisos

    private var trabajo: Job? = null
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan: StateFlow<Int?> = _faltan.asStateFlow()

    private val _escuchandoAvion = MutableStateFlow(false)
    val escuchandoAvion: StateFlow<Boolean> = _escuchandoAvion.asStateFlow()

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) {
            _mensaje.value = null
            PermisoUi.PERMITIDO
        } else {
            PermisoUi.DENEGADO
        }
    }

    fun actualizarPermiso(concedido: Boolean, requiereSolicitud: Boolean) {
        if (concedido || _permiso.value != PermisoUi.SIN_PEDIR || !requiereSolicitud) {
            permisoRespondido(concedido)
        }
    }

    fun noSePudoMostrar() {
        permisoRespondido(false)
        _mensaje.value = "Sin permiso: el aviso se guardó en el historial, pero no se mostró"
    }

    fun avisaraHora() {
        viewModelScope.launch {
            val aviso = Aviso(
                origen = Origen.BOTON,
                titulo = "Tarea lista",
                texto = "Tu entrega fue registrada"
            )
            publicarAviso(aviso)
        }
    }

    fun recordarEn(segundos: Int = 10) {
        if (trabajo?.isActive == true) return
        trabajo = viewModelScope.launch {
            try {
                for (i in segundos downTo 1) {
                    _faltan.value = i
                    delay(1000)
                }
                val aviso = Aviso(
                    origen = Origen.RECORDATORIO,
                    titulo = "Recordatorio",
                    texto = "Revisa tu laboratorio pendiente. Consulta las indicaciones de la " +
                        "actividad y verifica que tu entrega esté lista antes de enviarla."
                )
                publicarAviso(aviso)
            } finally {
                _faltan.value = null
            }
        }
    }

    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }

    fun cambiarEscucha(valor: Boolean) {
        _escuchandoAvion.value = valor
    }

    fun alCambiarModoAvion(activo: Boolean) {
        if (!_escuchandoAvion.value) return

        viewModelScope.launch {
            val estado = if (activo) "Activado" else "Desactivado"
            val aviso = Aviso(
                origen = Origen.MODO_AVION,
                titulo = "Modo avión",
                texto = estado
            )
            publicarAviso(aviso)
        }
    }

    private suspend fun publicarAviso(aviso: Aviso) {
        repository.agregar(aviso)
        if (!mostrarNotificacion(aviso)) {
            noSePudoMostrar()
        }
    }

}
