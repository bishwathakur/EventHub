package com.example.eventhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventhub.ui.navigation.EventHubNavigation
import com.example.eventhub.ui.navigation.Screen
import com.example.eventhub.ui.theme.EventHubTheme
import com.example.eventhub.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()
                    val currentUser = authViewModel.currentUser.collectAsStateWithLifecycle()
                    
                    val startDestination = when {
                        uiState.value.isAuthenticated && currentUser.value != null -> Screen.Main.route
                        uiState.value.isAuthenticated && uiState.value.needsProfile -> Screen.AddProfile.route
                        else -> Screen.SignIn.route
                    }
                    
                    EventHubNavigation(startDestination = startDestination)
                }
            }
        }
    }
}