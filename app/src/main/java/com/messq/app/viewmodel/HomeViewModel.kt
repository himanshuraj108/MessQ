package com.messq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messq.app.data.firebase.AuthRepository
import com.messq.app.data.firebase.QueueRepository
import com.messq.app.data.firebase.QueueStatusData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val queueRepo = QueueRepository()
    private val authRepo = AuthRepository()

    private val _queueStatus = MutableStateFlow(QueueStatusData())
    val queueStatus: StateFlow<QueueStatusData> = _queueStatus

    private val _userName = MutableStateFlow("Student")
    val userName: StateFlow<String> = _userName

    private val _walletBalance = MutableStateFlow(0)
    val walletBalance: StateFlow<Int> = _walletBalance

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _queueStatus.value = queueRepo.getQueueStatus("main")
            _userName.value = authRepo.getUserName()
            _walletBalance.value = authRepo.getWalletBalance()
        }
    }
}
