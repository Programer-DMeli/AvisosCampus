package com.carbajal.avisoscampus.notificacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.carbajal.avisoscampus.MainActivity
import com.carbajal.avisoscampus.R
import com.carbajal.avisoscampus.model.Aviso
import com.carbajal.avisoscampus.model.Origen

object Notificador {
    private const val CANAL_AVISOS = "avisos"
    // Conserva el canal existente y los ajustes que el usuario haya elegido.
    private const val CANAL_URGENTES = "modo_avion"

    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(CANAL_AVISOS, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
            )
            manager.createNotificationChannel(
                NotificationChannel(CANAL_URGENTES, "Urgentes", NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        val perm = tienePermiso(context)
        Log.d("Notificador", "mostrar: ${aviso.titulo} - ${aviso.texto}, tienePermiso=$perm")
        if (!perm) return false

        val intent = Intent.makeMainActivity(ComponentName(context, MainActivity::class.java)).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        val pendiente = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val urgente = aviso.origen == Origen.RECORDATORIO || aviso.origen == Origen.MODO_AVION
        val canal = if (urgente) CANAL_URGENTES else CANAL_AVISOS
        val prioridad = if (urgente) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT

        val notification = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .setPriority(prioridad)
            .setAutoCancel(true)
            .setContentIntent(pendiente)
            .build()

        NotificationManagerCompat.from(context).notify(aviso.id.toInt(), notification)
        return true
    }
}
