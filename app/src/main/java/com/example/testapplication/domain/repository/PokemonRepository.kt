package com.example.testapplication.domain.repository

import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.domain.PokemonDomainResponse

interface PokemonRepository {
    suspend fun getPokemonFromNetwork(request: GetPokemonRequest): PokemonDomainResponse?
    suspend fun getPokemonFromFromDb(): PokemonDomainResponse?
    suspend fun savePokemonsToDb(response: PokemonDomainResponse)
}