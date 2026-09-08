package com.example.testapplication.data.remote.mapper

import androidx.core.net.toUri
import com.example.testapplication.data.remote.dto.PokeMonDetails
import com.example.testapplication.data.remote.dto.PokemonResponse
import com.example.testapplication.domain.Pokemon
import com.example.testapplication.domain.PokemonDomainResponse
import com.example.testapplication.local.PokemonEntity

fun PokeMonDetails.toEntity() : Pokemon {
    val name = this.name
    val imageUrl = this.url.toImageUrl()

    return Pokemon(
        id = url.extractPokemonId(),
        name = name,
        imageUrl = imageUrl
    )
}

fun PokemonResponse.toDomain(): PokemonDomainResponse {
    val previousOffset = this.previous.getOffset()
    val nextOffset = this.next.getOffset()
    val pokemons = this.results?.map { it.toEntity() } ?: emptyList()

    return PokemonDomainResponse(
        nextCursor = nextOffset,
        previousCursor = previousOffset,
        pokemons = pokemons
    )
}

/*fun PokemonDomainResponse.toEntities(): List<PokemonEntity> {
    return pokemons.map { it.toEntity(this.next.getOffset() ?: 0) }.orEmpty()
}*/

fun PokeMonDetails.toEntity(nextCursor: Int): PokemonEntity {
    return PokemonEntity(
        id = url.extractPokemonId(),
        name = name,
        imageUrl = url.toImageUrl(),
        nextCursor = nextCursor
    )
}

fun PokemonEntity.toEntity(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}

fun Pokemon.toEntity(nextCursor: Int): PokemonEntity {
    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        nextCursor = nextCursor
    )
}



private fun String.extractPokemonId(): Int {
    return this.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
}

private fun String.toImageUrl(): String {
    val id = this.extractPokemonId()
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}

private fun String?.getOffset(): Int? {
    val url = this
    val offset = url?.toUri()?.getQueryParameter("offset")?.toIntOrNull()
    return offset
}

