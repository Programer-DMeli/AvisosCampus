package com.carbajal.avisoscampus.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carbajal.avisoscampus.model.PermisoUi
import com.carbajal.avisoscampus.notificacion.Notificador
import com.carbajal.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val context = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val avisos by vm.avisos.collectAsState(initial = emptyList())

    val lanzador = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(context)) {
            vm.permisoRespondido(true)
        }
        vm.eventos.collect { aviso ->
            Notificador.mostrar(context, aviso)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AvisosCampus", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Permiso
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Permiso de notificaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val textoPermiso = when (permiso) {
                        PermisoUi.SIN_PEDIR -> "Sin pedir"
                        PermisoUi.PERMITIDO -> "Permitido"
                        PermisoUi.DENEGADO -> "Denegado"
                    }
                    Text(text = "Estado: $textoPermiso")

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= 33) {
                                lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                vm.permisoRespondido(true)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Permitir notificaciones")
                    }
                }
            }

            // Card Avisos (Mostrar aviso)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Avisos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = { vm.avisaraHora() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mostrar aviso")
                    }
                }
            }

            // Historial
            HistorialAvisos(
                avisos = avisos,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
