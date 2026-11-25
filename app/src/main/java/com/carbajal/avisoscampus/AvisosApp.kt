package com.carbajal.avisoscampus

import android.app.Application
import com.carbajal.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanales(this)
    }
}
