package com.localpulse.app.data.user

import com.localpulse.app.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User>
    suspend fun setHasBusinessProfile(uid: String, has: Boolean): Result<Unit>
    suspend fun hasBusinessProfile(uid: String): Result<Boolean>
}
