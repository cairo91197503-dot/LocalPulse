package com.example.domain.model

enum class Plan {
    FREE,
    PRO
}

data class UserPlan(
    val plan: Plan = Plan.FREE,
    val planExpiresAt: Long? = null,
    val aiRepliesUsedThisMonth: Int = 0,
    val aiRepliesResetDate: Long = 0L
)
