
package com.unilost.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unilost.viewmodel.MainViewModel

@Composable
fun SplashScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val isLoggedIn = viewModel.isAuthenticated()
    LaunchedEffect(true) {
        scale.animateTo(1f, animationSpec = tween(700))
        delay(1500)
        if (isLoggedIn) onNavigateToHome() else onNavigateToLogin()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("UniLost", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}
