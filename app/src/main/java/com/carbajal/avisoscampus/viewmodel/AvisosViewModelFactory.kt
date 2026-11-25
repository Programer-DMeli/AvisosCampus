package com.carbajal.avisoscampus.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carbajal.avisoscampus.data.AvisosRepository

class AvisosViewModelFactory(
    private val aplicacion: Application,
    private val repository: AvisosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvisosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AvisosViewModel(aplicacion, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
