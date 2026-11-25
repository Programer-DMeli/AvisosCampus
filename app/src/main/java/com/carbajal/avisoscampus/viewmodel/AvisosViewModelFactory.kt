package com.carbajal.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbajal.avisoscampus.data.AvisosRepository
import com.carbajal.avisoscampus.model.Aviso

class AvisosViewModelFactory(
    private val repository: AvisosRepository,
    private val mostrarNotificacion: (Aviso) -> Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvisosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AvisosViewModel(repository, mostrarNotificacion) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
