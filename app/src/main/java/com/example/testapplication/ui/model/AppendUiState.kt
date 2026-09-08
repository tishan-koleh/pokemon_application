package com.example.testapplication.ui.model

sealed class AppendUiState {
    object Loading: AppendUiState()
    object Idle: AppendUiState()
    data class Error(val error: Throwable): AppendUiState()
    object Empty: AppendUiState()
    object EndReached: AppendUiState()
}