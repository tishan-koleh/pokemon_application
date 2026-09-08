package com.example.testapplication.domain.usecase

import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.domain.PokemonDomainResponse
import com.example.testapplication.domain.repository.PokemonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository,
) {
    suspend fun getPokemon(request: GetPokemonRequest, scope: CoroutineScope): PokemonDomainResponse {

        val result = if (request.isInitialFeed && request.isDeviceOnline.not()){
            repository.getPokemonFromFromDb()
        }else{
            val result = repository.getPokemonFromNetwork(request)
            result?.let { scope.launch { repository.savePokemonsToDb(it) } }
            result
        }

        if (result == null){
            throw IllegalStateException("NO_RESULT")
        }

        return result
    }
}