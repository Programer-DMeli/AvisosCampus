package com.carbajal.avisoscampus.data

import com.carbajal.avisoscampus.model.Aviso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AvisosRepository {
    val avisos: Flow<List<Aviso>>
    suspend fun agregar(aviso: Aviso)
}

class AvisosRepositoryMemoria : AvisosRepository {
    private val lista = mutableListOf<Aviso>()
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: Flow<List<Aviso>> = _avisos.asStateFlow()

    override suspend fun agregar(aviso: Aviso) {
        lista.add(0, aviso)
        _avisos.value = lista.toList()
    }
}
