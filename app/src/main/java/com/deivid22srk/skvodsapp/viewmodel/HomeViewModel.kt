package com.deivid22srk.skvodsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deivid22srk.skvodsapp.network.ApiClient
import com.google.gson.JsonElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: JsonElement) : UiState()
    data class Error(val message: String) : UiState()
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = ApiClient.api.getNowLive()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = UiState.Success(response.body()!!)
                } else {
                    _uiState.value = UiState.Error("Failed to load data: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown network error")
            }
        }
    }
}