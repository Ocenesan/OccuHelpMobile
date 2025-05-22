package com.example.occuhelp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSessionViewModel : ViewModel() {
    private val _loggedInUserName = MutableStateFlow<String?>(null)
    val loggedInUserName: StateFlow<String?> = _loggedInUserName.asStateFlow()

    fun setUser(name: String?) {
        _loggedInUserName.value = name
    }

    fun clearUser() {
        _loggedInUserName.value = null
    }
}