package com.example.domain.service

import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUser: Flow<String?>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}
