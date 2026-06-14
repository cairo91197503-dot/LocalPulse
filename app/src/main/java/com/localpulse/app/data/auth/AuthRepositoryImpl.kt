package com.localpulse.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.localpulse.app.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of [AuthRepository] using Firebase Authentication.
 *
 * @property firebaseAuth The [FirebaseAuth] instance provided via Hilt.
 */
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: com.localpulse.app.data.user.UserRepository
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val existingUser = userRepository.getUser(firebaseUser.uid).getOrNull()
                val user = existingUser?.copy(
                    lastLoginAt = System.currentTimeMillis(),
                    name = firebaseUser.displayName ?: existingUser.name,
                    photoUrl = firebaseUser.photoUrl?.toString() ?: existingUser.photoUrl
                ) ?: User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    lastLoginAt = System.currentTimeMillis()
                )
                userRepository.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to retrieve user after sign in."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString()
            )
        } else {
            null
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
