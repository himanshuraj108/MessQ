package com.messq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messq.app.data.firebase.AuthRepository
import com.messq.app.data.firebase.QueueRepository
import com.messq.app.data.firebase.QueueStatusData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel : ViewModel() {
    private val queueRepo = QueueRepository()
    private val authRepo = AuthRepository()

    private val _queueStatus = MutableStateFlow(QueueStatusData(messId = "main", currentCount = 45, maxCapacity = 200, waitTimeMinutes = 5))
    val queueStatus: StateFlow<QueueStatusData> = _queueStatus

    private val _userName = MutableStateFlow("Himanshu Raj")
    val userName: StateFlow<String> = _userName

    private val _walletBalance = MutableStateFlow(500)
    val walletBalance: StateFlow<Int> = _walletBalance

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                withTimeoutOrNull(2000L) {
                    _queueStatus.value = queueRepo.getQueueStatus("main")
                }
            } catch (e: Exception) {
            }
            try {
                withTimeoutOrNull(2000L) {
                    _userName.value = authRepo.getUserName()
                }
            } catch (e: Exception) {
            }
            try {
                withTimeoutOrNull(2000L) {
                    _walletBalance.value = authRepo.getWalletBalance()
                }
            } catch (e: Exception) {
            }
        }
    }
}
