package com.messq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messq.app.data.CartItem
import com.messq.app.data.firebase.OrderData
import com.messq.app.data.firebase.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val repo = OrderRepository()

    private val _orders = MutableStateFlow<List<OrderData>>(emptyList())
    val orders: StateFlow<List<OrderData>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _orders.value = repo.getOrderHistory()
            _isLoading.value = false
        }
    }

    fun placeOrder(cartItems: List<CartItem>, slot: String, date: String, total: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repo.placeOrder(cartItems, slot, date, total)
            _isLoading.value = false
        }
    }
}
