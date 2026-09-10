package com.messq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.messq.app.navigation.CartState
import com.messq.app.navigation.Screen
import com.messq.app.ui.screens.*
import com.messq.app.ui.theme.MessQTheme
import com.messq.app.viewmodel.AuthViewModel
import com.messq.app.viewmodel.HomeViewModel
import com.messq.app.viewmodel.MenuViewModel
import com.messq.app.viewmodel.OrderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
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

    val authViewModel: AuthViewModel = viewModel()
    val menuViewModel: MenuViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()

    var confirmedCartItems by remember { mutableStateOf(listOf<com.messq.app.data.CartItem>()) }
    var confirmedTotalAmount by remember { mutableStateOf(0) }
    var confirmedSlot by remember { mutableStateOf("") }
    var confirmedDate by remember { mutableStateOf("") }

    val startDestination = if (authViewModel.isLoggedIn) Screen.Home.route else Screen.Splash.route

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                cartState = cartState,
                homeViewModel = homeViewModel,
                onNavigateToQueue = { navController.navigate(Screen.Queue.route) },
                onNavigateToMenu = { navController.navigate(Screen.Menu.route) },
                onNavigateToPreOrder = { navController.navigate(Screen.PreOrder.route) },
                onNavigateToOrderHistory = { navController.navigate(Screen.OrderHistory.route) },
                onNavigateToAssistant = { navController.navigate(Screen.AIAssistant.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
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
                menuViewModel = menuViewModel,
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
                orderViewModel = orderViewModel,
                onNavigateToOrderConfirmed = { slot, date ->
                    confirmedCartItems = cartState.items.toList()
                    confirmedTotalAmount = cartState.totalAmount
                    confirmedSlot = slot
                    confirmedDate = date
                    cartState.clear()
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
                    orderViewModel.loadHistory()
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
                orderViewModel = orderViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
