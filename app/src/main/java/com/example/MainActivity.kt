package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MainAppScaffold
import com.example.ui.theme.BgrDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BgrViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BgrViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgrDarkBackground
                ) {
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    MainAppScaffold(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
