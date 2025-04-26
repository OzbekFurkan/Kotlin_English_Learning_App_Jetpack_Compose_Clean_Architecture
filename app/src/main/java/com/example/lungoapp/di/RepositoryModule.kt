package com.example.lungoapp.di

import com.example.lungoapp.data.repository.AuthRepositoryImpl
import com.example.lungoapp.domain.repository.AuthRepository
import com.example.lungoapp.domain.repository.WordRepository
import com.example.lungoapp.data.repository.WordRepositoryImpl
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository
} 