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
import kotlinx.coroutines.withTimeoutOrNull

class MenuViewModel : ViewModel() {
    private val repo = MenuRepository()

    private val allDefaultItems = MessQData.breakfastItems + MessQData.lunchItems + MessQData.dinnerItems + MessQData.snackItems
    private val _menuItems = MutableStateFlow<List<MenuItem>>(allDefaultItems)
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            try {
                withTimeoutOrNull(2500L) {
                    repo.seedMenuIfEmpty()
                    val items = repo.getMenuItems()
                    if (items.isNotEmpty()) {
                        _menuItems.value = items
                    }
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getByCategory(category: MealCategory): List<MenuItem> =
        _menuItems.value.filter { it.category == category }
}
