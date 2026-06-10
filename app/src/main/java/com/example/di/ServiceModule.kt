package com.example.di

import com.example.data.service.AiResponseServiceImpl
import com.example.data.service.AuthServiceImpl
import com.example.domain.service.AiResponseService
import com.example.domain.service.AuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuthService(
        authServiceImpl: AuthServiceImpl
    ): AuthService

    @Binds
    @Singleton
    abstract fun bindAiResponseService(
        aiResponseServiceImpl: AiResponseServiceImpl
    ): AiResponseService
}
