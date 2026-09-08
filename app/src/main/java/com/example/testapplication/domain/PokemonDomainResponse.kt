package com.example.testapplication.domain

data class PokemonDomainResponse(
    val nextCursor : Int?,
    val previousCursor: Int?,
    val pokemons: List<Pokemon>,
)
