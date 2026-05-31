package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.AppTutorialDialog
import com.example.ui.theme.AppTheme
import com.example.ui.viewmodel.PulseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        
        // Edge-To-Edge styling enablement
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val pulseViewModel: PulseViewModel = viewModel()
                    var showTutorial by remember { mutableStateOf(true) }

                    DashboardScreen(
                        viewModel = pulseViewModel,
                        onShowTutorial = { showTutorial = true }
                    )

                    if (showTutorial) {
                        AppTutorialDialog(
                            onDismiss = { showTutorial = false }
                        )
                    }
                }
            }
        }
    }
}
