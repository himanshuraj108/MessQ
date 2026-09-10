package com.messq.app.navigation

import androidx.compose.runtime.*
import com.messq.app.data.CartItem
import com.messq.app.data.MenuItem
import com.messq.app.data.MessQData

// Simple state holder for cart
class CartState {
    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = _items

    val totalAmount: Int get() = _items.sumOf { it.menuItem.price * it.quantity }
    val totalItems: Int get() = _items.sumOf { it.quantity }

    fun addItem(item: MenuItem) {
        val existing = _items.find { it.menuItem.id == item.id }
        if (existing != null) {
            val idx = _items.indexOf(existing)
            _items[idx] = existing.copy(quantity = existing.quantity + 1)
        } else {
            _items.add(CartItem(item, 1))
        }
    }

    fun removeItem(item: MenuItem) {
        val existing = _items.find { it.menuItem.id == item.id }
        if (existing != null) {
            if (existing.quantity > 1) {
                val idx = _items.indexOf(existing)
                _items[idx] = existing.copy(quantity = existing.quantity - 1)
            } else {
                _items.remove(existing)
            }
        }
    }

    fun getQuantity(item: MenuItem): Int =
        _items.find { it.menuItem.id == item.id }?.quantity ?: 0

    fun clear() = _items.clear()
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Queue : Screen("queue")
    object Menu : Screen("menu")
    object PreOrder : Screen("preorder")
    object OrderConfirmed : Screen("order_confirmed")
    object AIAssistant : Screen("ai_assistant")
    object OrderHistory : Screen("order_history")
    object Profile : Screen("profile")
    object ForgotPassword : Screen("forgot_password")
}
