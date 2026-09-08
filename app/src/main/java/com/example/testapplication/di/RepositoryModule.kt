package com.example.testapplication.di

import com.example.testapplication.data.repository.RealPokemonRepository
import com.example.testapplication.domain.repository.PokemonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsPokemonRepository(impl : RealPokemonRepository): PokemonRepository
}