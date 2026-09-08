package com.example.testapplication.data.remote.dto

data class PokemonResponse(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<PokeMonDetails>?
)