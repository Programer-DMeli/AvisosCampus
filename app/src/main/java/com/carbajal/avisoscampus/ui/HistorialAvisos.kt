package com.carbajal.avisoscampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carbajal.avisoscampus.model.Aviso
import com.carbajal.avisoscampus.model.Origen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    val esquema = MaterialTheme.colorScheme
    val oscuro = isSystemInDarkTheme()
    val tarjeta = if (oscuro) esquema.surfaceContainerLow else Color(0xFFFFFEFF)
    val fondoFila = if (oscuro) esquema.surfaceContainerHigh else Color(0xFFEEF2F7)
    val borde = if (oscuro) esquema.outlineVariant else Color(0xFFD6DEE8)
    val titulo = if (oscuro) Color(0xFF9DCEFA) else Color(0xFF196B9A)
    val formatoHora = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tarjeta, contentColor = esquema.onSurface),
        border = BorderStroke(1.dp, borde),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Historial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titulo
            )

            if (avisos.isEmpty()) {
                Text(
                    text = "Sin avisos todavía",
                    style = MaterialTheme.typography.bodyMedium,
                    color = esquema.onSurfaceVariant
                )
            } else {
                avisos.take(20).forEach { aviso ->
                    FilaAviso(aviso, formatoHora.format(Date(aviso.timestamp)), fondoFila)
                }
            }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso, hora: String, fondo: Color) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(fondo, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (maxWidth < 250.dp || LocalDensity.current.fontScale > 1.3f) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                EtiquetaOrigen(aviso.origen)
                ContenidoAviso(aviso)
                HoraAviso(hora)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EtiquetaOrigen(aviso.origen)
                ContenidoAviso(aviso, Modifier.weight(1f))
                HoraAviso(hora)
            }
        }
    }
}

@Composable
private fun EtiquetaOrigen(origen: Origen) {
    val fondo = when (origen) {
        Origen.BOTON -> Color(0xFF226DB1)
        Origen.RECORDATORIO -> Color(0xFFF4AA00)
        Origen.MODO_AVION -> Color(0xFF7350B9)
        Origen.CARGADOR -> Color(0xFF21654C)
    }
    val texto = if (origen == Origen.RECORDATORIO) Color(0xFF2B1B00) else Color.White

    Surface(shape = RoundedCornerShape(50), color = fondo, contentColor = texto) {
        Text(
            text = origen.texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContenidoAviso(aviso: Aviso, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = aviso.titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = aviso.texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HoraAviso(hora: String) {
    Text(
        text = hora,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
