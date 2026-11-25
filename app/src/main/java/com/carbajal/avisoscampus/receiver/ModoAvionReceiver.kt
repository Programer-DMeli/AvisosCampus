package com.carbajal.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ModoAvionReceiver(private val alCambiar: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ModoAvionReceiver", "onReceive action: ${intent.action}")
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val activo = intent.getBooleanExtra("state", false)
            Log.d("ModoAvionReceiver", "Airplane mode changed: activo=$activo")
            alCambiar(activo)
        }
    }
}
