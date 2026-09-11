package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FloatingOverlayMenu
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.PanelViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PanelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MafiaPanelApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MafiaPanelApp(viewModel: PanelViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val modFeatures by viewModel.modFeatures.collectAsState()
    val currentSessionKey by viewModel.currentSessionKey.collectAsState()

    // Handle back button logically
    BackHandler(enabled = true) {
        if (modFeatures.isMenuExpanded) {
            viewModel.setFloatingMenuExpanded(false)
        } else if (currentScreen is AppScreen.AdminPanel) {
            if (currentSessionKey != null) {
                viewModel.navigateTo(AppScreen.Dashboard)
            } else {
                viewModel.navigateTo(AppScreen.Login)
            }
        } else if (currentScreen is AppScreen.Dashboard) {
            viewModel.logout()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .safeDrawingPadding()
    ) {
        // Main Screen Navigation
        Crossfade(
            targetState = currentScreen,
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                is AppScreen.Login -> {
                    LoginScreen(viewModel = viewModel)
                }
                is AppScreen.Dashboard -> {
                    DashboardScreen(viewModel = viewModel)
                }
                is AppScreen.AdminPanel -> {
                    AdminPanelScreen(viewModel = viewModel)
                }
            }
        }

        // Floating Mod Menu Bubble and Overlay on top
        if (modFeatures.isOverlayActive) {
            FloatingOverlayMenu(viewModel = viewModel)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
