package com.example.testapplication.domain.usecase

import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.domain.PokemonDomainResponse
import com.example.testapplication.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(
    private val repository: PokemonRepository,
) {
    suspend fun getPokemon(request: GetPokemonRequest): PokemonDomainResponse {
        return repository.getPokemonFromNetwork(request)!!
    }
}