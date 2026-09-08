package com.example.testapplication.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplication.NetworkManager
import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.domain.usecase.GetPokemonUseCase
import com.example.testapplication.ui.model.AppendUiState
import com.example.testapplication.ui.model.FeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PokemonFeedVm @Inject constructor(
    private val useCase: GetPokemonUseCase,
    private val networkManager: NetworkManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState = _uiState

    private var isLoading = false
    private var activeJob: Job? = null

    init {
        loadFeed()
    }

    fun loadFeed() {
        if (activeJob?.isActive == true) return
        activeJob = viewModelScope.launch(Dispatchers.IO) {
            if (isLoading) {
                return@launch
            } else {
                isLoading = true
            }

            try {
                val limit = 20
                val cursor = 0
                val request = GetPokemonRequest(
                    limit = limit,
                    offset = cursor,
                    isDeviceOnline = networkManager.isOnline(),
                    isInitialFeed = true
                )
                val response = useCase.getPokemon(request, viewModelScope)

                if (response.pokemons.isNotEmpty()) {
                    val feedState = FeedUiState.Content(
                        pokemons = response.pokemons,
                        cursor = response.nextCursor,
                        limit = limit,
                        appendState = if(networkManager.isOnline().not()) AppendUiState.DeviceOffline else if (response.nextCursor == null) AppendUiState.EndReached else AppendUiState.Idle
                    )
                    _uiState.value = feedState
                } else {
                    _uiState.value = FeedUiState.Error(IllegalStateException())
                }
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMOre() {
        Log.d("TISHAN", "TRIGGERED : activeJob?.isActive ${activeJob?.isActive}")
        if (activeJob?.isActive == true) return

        activeJob = viewModelScope.launch(Dispatchers.IO) {
            val currentState = (_uiState.value as? FeedUiState.Content) ?: return@launch
            val currentList = currentState.pokemons
            val nextCursor = currentState.cursor
            val currentLimit = currentState.limit

            if (isLoading || currentState.appendState == AppendUiState.EndReached || currentState.appendState == AppendUiState.Loading || nextCursor == null) {
                return@launch
            } else if (networkManager.isOnline().not()){
                _uiState.value = FeedUiState.Content(
                    pokemons = currentList,
                    cursor = nextCursor,
                    limit = currentLimit,
                    appendState = AppendUiState.DeviceOffline
                )
                return@launch
            } else {
                _uiState.value = FeedUiState.Content(
                    pokemons = currentList,
                    cursor = nextCursor,
                    limit = currentLimit,
                    appendState = AppendUiState.Loading
                )
                isLoading = true
            }

            try {
                val request = GetPokemonRequest(
                    limit = currentLimit,
                    offset = nextCursor,
                    isDeviceOnline = true,
                    isInitialFeed = false
                )
                val response = useCase.getPokemon(request, viewModelScope)

                if (response.pokemons.isNotEmpty()) {
                    val feedState = FeedUiState.Content(
                        pokemons = currentList + response.pokemons,
                        cursor = response.nextCursor,
                        limit = currentLimit,
                        appendState = if (response.nextCursor == null) AppendUiState.EndReached else AppendUiState.Idle
                    )
                    _uiState.value = feedState
                } else {
                    _uiState.value = FeedUiState.Content(
                        pokemons = currentList,
                        cursor = nextCursor,
                        limit = currentLimit,
                        appendState = AppendUiState.Error(IllegalStateException())
                    )
                }
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Content(
                    pokemons = currentList,
                    cursor = nextCursor,
                    limit = currentLimit,
                    appendState = AppendUiState.Error(e)
                )
            } finally {
                isLoading = false
            }
        }
    }

    override fun onCleared() {
        activeJob?.cancel()
        super.onCleared()
    }

}