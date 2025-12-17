package com.unilost.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Importar todas tus pantallas:
import com.unilost.ui.screens.SplashScreen
import com.unilost.ui.screens.LoginScreen
import com.unilost.ui.screens.RegisterScreen
import com.unilost.ui.screens.HomeScreen
import com.unilost.ui.screens.ItemsListScreen
import com.unilost.ui.screens.ItemDetailScreen
import com.unilost.ui.screens.PublishItemScreen
import com.unilost.ui.screens.NotificationsScreen
import com.unilost.ui.screens.ProfileScreen
import com.unilost.ui.screens.ReportesScreen   // 👈 NUEVO

// -------------------------
// Pantallas / Rutas
// -------------------------
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object List : Screen("list")
    object Detail : Screen("detail/{id}") {
        fun create(id: String) = "detail/$id"
    }
    object Publish : Screen("publish")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Reportes : Screen("reportes") // 👈 NUEVO
}

// -------------------------
// Navigation Graph
// -------------------------
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onOpenList = { navController.navigate(Screen.List.route) },
                onOpenPublish = { navController.navigate(Screen.Publish.route) },
                onOpenNotifications = { navController.navigate(Screen.Notifications.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
                onOpenReportes = { navController.navigate(Screen.Reportes.route) } // 👈 NUEVO
            )
        }

        composable(Screen.List.route) {
            ItemsListScreen(
                onOpenDetail = { id ->
                    navController.navigate(Screen.Detail.create(id))
                },
                onOpenPublish = {
                    navController.navigate(Screen.Publish.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ItemDetailScreen(
                id = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Publish.route) {
            PublishItemScreen(
                onPublished = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // 🧾 REPORTES (ADMIN)
        composable(Screen.Reportes.route) {
            ReportesScreen()
        }
    }
}
