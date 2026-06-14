package com.localpulse.app.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.localpulse.app.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(user.uid)
                .set(
                    mapOf(
                        "uid" to user.uid,
                        "name" to user.name,
                        "email" to user.email,
                        "photoUrl" to user.photoUrl,
                        "plan" to user.plan,
                        "lastLoginAt" to user.lastLoginAt
                    ),
                    SetOptions.merge()
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(uid: String): Result<User> {
        return try {
            val doc = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val user = doc.toObject(User::class.java)
                ?: return Result.failure(Exception("Usuário não encontrado"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setHasBusinessProfile(uid: String, has: Boolean): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(uid)
                .set(
                    mapOf("hasBusinessProfile" to has),
                    SetOptions.merge()
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasBusinessProfile(uid: String): Result<Boolean> {
        return try {
            val doc = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val hasProfile = doc.getBoolean("hasBusinessProfile") ?: false
            Result.success(hasProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
