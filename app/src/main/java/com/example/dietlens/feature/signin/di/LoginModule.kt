package com.example.dietlens.feature.signin.di

import com.example.dietlens.feature.signin.domain.LoginRepository
import com.example.dietlens.feature.signin.domain.LoginRepositoryImpl
import com.example.dietlens.feature.signin.domain.LoginUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository =
        LoginRepositoryImpl(firebaseAuth)

    @Provides
    fun provideLoginUseCase(authRepository: LoginRepository): LoginUseCase =
        LoginUseCase(authRepository)
}