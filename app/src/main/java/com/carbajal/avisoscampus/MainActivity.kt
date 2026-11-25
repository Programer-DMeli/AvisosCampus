package com.carbajal.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.carbajal.avisoscampus.ui.PantallaAvisos
import com.carbajal.avisoscampus.ui.theme.AvisoscampusTheme
import com.carbajal.avisoscampus.viewmodel.AvisosViewModel

class MainActivity : ComponentActivity() {
    private val vm: AvisosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvisoscampusTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaAvisos(vm = vm)
                }
            }
        }
    }
}
