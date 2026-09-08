package com.example.testapplication.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = DbConstants.POKEMON_ENTITY_TABLE_NAME)
data class PokemonEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = DbConstants.NAME) val name: String,
    @ColumnInfo(name = DbConstants.IMAGE_URL) val imageUrl: String,
    @ColumnInfo(name = DbConstants.NEXT_CURSOR) val nextCursor: Int,
)