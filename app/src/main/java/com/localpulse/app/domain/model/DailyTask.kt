package com.localpulse.app.domain.model

data class DailyTask(
    val id: String = "",
    val emoji: String = "",
    val title: String = "",
    val description: String = "",
    val xp: Int = 10,
    val isCompleted: Boolean = false,
    val category: String = ""
)

data class UserProgress(
    val uid: String = "",
    val totalXp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val lastTaskDate: String = "",
    val tasksCompletedToday: Int = 0
)
