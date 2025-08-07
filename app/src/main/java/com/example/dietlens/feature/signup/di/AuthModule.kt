package com.example.dietlens.feature.signup.di

import com.example.dietlens.feature.signup.domain.AuthRepository
import com.example.dietlens.feature.signup.domain.FirebaseAuthRepositoryImpl
import com.example.dietlens.feature.signup.domain.RegisterUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = FirebaseAuthRepositoryImpl(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(
        authRepository: AuthRepository
    ): RegisterUserUseCase = RegisterUserUseCase(authRepository)
}