package com.example.testapplication.ui.model

import com.example.testapplication.domain.Pokemon

sealed class FeedUiState {
    object Loading: FeedUiState()

    data class Content(
        val pokemons: List<Pokemon>,
        val cursor: Int?,
        val limit: Int,
        val appendState: AppendUiState
    ): FeedUiState()

    data class Error(val error: Throwable): FeedUiState()
}