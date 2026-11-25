package com.carbajal.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import com.carbajal.avisoscampus.model.PermisoUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AvisosViewModel : ViewModel() {

    private val _permiso = MutableStateFlow(PermisoUi.SIN_PEDIR)
    val permiso: StateFlow<PermisoUi> = _permiso.asStateFlow()

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) {
            PermisoUi.PERMITIDO
        } else {
            PermisoUi.DENEGADO
        }
    }
}
