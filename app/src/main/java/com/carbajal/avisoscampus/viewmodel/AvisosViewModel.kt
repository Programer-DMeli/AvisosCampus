package com.carbajal.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carbajal.avisoscampus.data.AvisosRepository
import com.carbajal.avisoscampus.model.Aviso
import com.carbajal.avisoscampus.model.Origen
import com.carbajal.avisoscampus.model.PermisoUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(
    private val repository: AvisosRepository
) : ViewModel() {

    private val _permiso = MutableStateFlow(PermisoUi.SIN_PEDIR)
    val permiso: StateFlow<PermisoUi> = _permiso.asStateFlow()

    val avisos: Flow<List<Aviso>> = repository.avisos

    private val _eventos = MutableSharedFlow<Aviso>()
    val eventos: SharedFlow<Aviso> = _eventos.asSharedFlow()

    private var trabajo: Job? = null
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan: StateFlow<Int?> = _faltan.asStateFlow()

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) {
            PermisoUi.PERMITIDO
        } else {
            PermisoUi.DENEGADO
        }
    }

    fun avisaraHora() {
        viewModelScope.launch {
            val aviso = Aviso(
                origen = Origen.BOTON,
                titulo = "Tarea lista",
                texto = "Tu entrega fue registrada"
            )
            repository.agregar(aviso)
            _eventos.emit(aviso)
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
                repository.agregar(aviso)
                _eventos.emit(aviso)
            } finally {
                _faltan.value = null
            }
        }
    }

    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }
}
