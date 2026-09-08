package com.example.testapplication.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.data.remote.api.PokemonApiService
import com.example.testapplication.data.remote.dto.PokemonResponse
import com.example.testapplication.domain.repository.PokemonRepository
import com.google.gson.Gson
import javax.inject.Inject
import androidx.core.content.edit
import com.example.testapplication.domain.Pokemon
import com.google.gson.reflect.TypeToken

class RealPokemonRepository @Inject constructor(
    private val apiService: PokemonApiService
) : PokemonRepository {
    override suspend fun getPokemonFromNetwork(request: GetPokemonRequest): PokemonResponse {
        return apiService.getPokemonFeed(request.limit, request.offset)
    }
}