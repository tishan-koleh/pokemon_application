package com.example.testapplication.data.remote.api

import com.example.testapplication.constants.PokemonApiConstants
import com.example.testapplication.data.remote.dto.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PokemonApiService {

    @GET(PokemonApiConstants.POKEMON_FEED_REQUEST)
    suspend fun getPokemonFeed(
        @Query(PokemonApiConstants.LIMIT) limit: Int,
        @Query(PokemonApiConstants.OFFSET) offset: Int
    ) : PokemonResponse

}