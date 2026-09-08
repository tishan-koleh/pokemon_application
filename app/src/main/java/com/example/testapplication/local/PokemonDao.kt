package com.example.testapplication.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {

    @Query("SELECT * FROM ${DbConstants.POKEMON_ENTITY_TABLE_NAME} ORDER BY ${DbConstants.NEXT_CURSOR} ASC")
    suspend fun getAllPokemons(): List<PokemonEntity>

    @Insert(entity = PokemonEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemons(pokemons: List<PokemonEntity>)

    @Query("SELECT COUNT(*) FROM ${DbConstants.POKEMON_ENTITY_TABLE_NAME}")
    suspend fun getCount(): Int

    @Query("DELETE FROM ${DbConstants.POKEMON_ENTITY_TABLE_NAME}")
    suspend fun deleteAll()

}