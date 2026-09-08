package com.example.testapplication.data.repository

import com.example.testapplication.data.remote.api.GetPokemonRequest
import com.example.testapplication.data.remote.api.PokemonApiService
import com.example.testapplication.data.remote.mapper.toDomain
import com.example.testapplication.data.remote.mapper.toEntity
import com.example.testapplication.domain.PokemonDomainResponse
import com.example.testapplication.domain.repository.PokemonRepository
import com.example.testapplication.local.PokemonDao
import javax.inject.Inject

class RealPokemonRepository @Inject constructor(
    private val apiService: PokemonApiService,
    private val dao: PokemonDao,
) : PokemonRepository {
    override suspend fun getPokemonFromNetwork(request: GetPokemonRequest): PokemonDomainResponse? {
        return runCatching { apiService.getPokemonFeed(request.limit, request.offset).toDomain() }.getOrNull()
    }

    override suspend fun getPokemonFromFromDb(): PokemonDomainResponse? {
        return runCatching {
            if (dao.getCount() > 0){
                val entities = dao.getAllPokemons()
                val nextOffset = entities.last().nextCursor
                val pokemons = entities.map { it.toEntity() }
                return PokemonDomainResponse(
                    nextCursor = nextOffset,
                    previousCursor = 0,
                    pokemons = pokemons
                )
            }else{
                null
            }
        }.getOrNull()
    }

    override suspend fun savePokemonsToDb(response: PokemonDomainResponse) {
        runCatching {
            val entities = response.pokemons.map { it.toEntity(response.nextCursor ?: 0) }
            dao.insertPokemons(entities)
        }
    }
}