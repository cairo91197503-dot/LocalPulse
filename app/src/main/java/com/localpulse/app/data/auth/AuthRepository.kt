package com.localpulse.app.data.auth

import com.localpulse.app.domain.model.User

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    
    /**
     * Signs in with a Google ID Token.
     *
     * @param idToken The ID token received from Google Sign-In.
     * @return A [Result] containing the [User] on success, or an exception on failure.
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>

    /**
     * Signs the current user out.
     */
    suspend fun signOut()

    /**
     * Retrieves the currently authenticated user.
     *
     * @return The [User] if logged in, or null otherwise.
     */
    fun getCurrentUser(): User?

    /**
     * Checks if there is a currently authenticated user.
     *
     * @return true if a user is logged in, false otherwise.
     */
    fun isUserLoggedIn(): Boolean
}
