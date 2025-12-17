
package com.unilost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unilost.navigation.NavigationGraph
import com.unilost.ui.theme.UniLostTheme
import com.unilost.viewmodel.MainViewModel
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            UniLostTheme {
                Surface(modifier = Modifier) {
                    val navController = rememberNavController()
                    val vm: MainViewModel = viewModel()
                    NavigationGraph(navController = navController, startDestination = "splash")
                }
            }
        }
    }
}
