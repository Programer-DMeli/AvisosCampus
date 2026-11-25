package com.carbajal.avisoscampus.viewmodel

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.avisoscampus.data.AvisosRepository
import com.carbajal.avisoscampus.model.Aviso
import com.carbajal.avisoscampus.model.Origen
import com.carbajal.avisoscampus.model.PermisoUi
import com.carbajal.avisoscampus.notificacion.Notificador
import com.carbajal.avisoscampus.receiver.ModoAvionReceiver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(
    private val aplicacion: Application,
    private val repository: AvisosRepository
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
    private val receptorModoAvion = ModoAvionReceiver(::alCambiarModoAvion)

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) {
            _mensaje.value = null
            PermisoUi.PERMITIDO
        } else {
            PermisoUi.DENEGADO
        }
    }

    fun actualizarPermiso() {
        val concedido = Notificador.tienePermiso(aplicacion)
        if (concedido || _permiso.value != PermisoUi.SIN_PEDIR ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
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
                    texto = "Revisa tu laboratorio pendiente"
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
        if (_escuchandoAvion.value == valor) return

        if (valor) {
            ContextCompat.registerReceiver(
                aplicacion,
                receptorModoAvion,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } else {
            aplicacion.unregisterReceiver(receptorModoAvion)
        }
        _escuchandoAvion.value = valor
    }

    private fun alCambiarModoAvion(activo: Boolean) {
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
        if (!Notificador.mostrar(aplicacion, aviso)) {
            noSePudoMostrar()
        }
    }

    override fun onCleared() {
        cambiarEscucha(false)
        super.onCleared()
    }
}
