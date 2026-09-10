package com.messq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messq.app.data.MealCategory
import com.messq.app.data.MenuItem
import com.messq.app.data.MessQData
import com.messq.app.data.firebase.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val repo = MenuRepository()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadMenu() }

    fun loadMenu() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.seedMenuIfEmpty()
            val items = repo.getMenuItems()
            _menuItems.value = if (items.isEmpty()) {
                MessQData.breakfastItems + MessQData.lunchItems + MessQData.dinnerItems + MessQData.snackItems
            } else {
                items
            }
            _isLoading.value = false
        }
    }

    fun getByCategory(category: MealCategory): List<MenuItem> =
        _menuItems.value.filter { it.category == category }
}
