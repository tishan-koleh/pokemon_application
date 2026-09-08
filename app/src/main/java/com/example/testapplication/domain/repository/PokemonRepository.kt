package com.example.testapplication.domain.repository

import android.content.SharedPreferences
import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.data.remote.dto.PokemonResponse
import com.example.testapplication.domain.Pokemon
import com.google.gson.Gson

interface PokemonRepository {
    suspend fun getPokemonFromNetwork(request: GetPokemonRequest): PokemonResponse
}