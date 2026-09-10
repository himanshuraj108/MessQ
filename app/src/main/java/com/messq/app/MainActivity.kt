package com.messq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.messq.app.navigation.CartState
import com.messq.app.navigation.Screen
import com.messq.app.ui.screens.*
import com.messq.app.ui.theme.MessQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessQTheme {
                MessQApp()
            }
        }
    }
}

@Composable
fun MessQApp() {
    val navController = rememberNavController()
    val cartState = remember { CartState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var confirmedCartItems by remember { mutableStateOf(listOf<com.messq.app.data.CartItem>()) }
    var confirmedTotalAmount by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                cartState = cartState,
                onNavigateToQueue = { navController.navigate(Screen.Queue.route) },
                onNavigateToMenu = { navController.navigate(Screen.Menu.route) },
                onNavigateToPreOrder = { navController.navigate(Screen.PreOrder.route) },
                onNavigateToOrderHistory = { navController.navigate(Screen.OrderHistory.route) },
                onNavigateToAssistant = { navController.navigate(Screen.AIAssistant.route) },
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        composable(Screen.Queue.route) {
            QueueScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                cartState = cartState,
                onNavigateToPreOrder = { navController.navigate(Screen.PreOrder.route) },
                onBack = { navController.popBackStack() },
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.PreOrder.route) {
            PreOrderScreen(
                cartState = cartState,
                onNavigateToOrderConfirmed = {
                    confirmedCartItems = cartState.items.toList()
                    confirmedTotalAmount = cartState.totalAmount
                    navController.navigate(Screen.OrderConfirmed.route) {
                        popUpTo(Screen.PreOrder.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OrderConfirmed.route) {
            OrderConfirmedScreen(
                cartItems = confirmedCartItems,
                totalAmount = confirmedTotalAmount,
                onViewOrders = {
                    navController.navigate(Screen.OrderHistory.route) {
                        popUpTo(Screen.OrderConfirmed.route) { inclusive = true }
                    }
                },
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AIAssistant.route) {
            AIAssistantScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
