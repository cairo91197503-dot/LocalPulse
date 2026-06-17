package com.localpulse.app.data.tasks

import com.localpulse.app.domain.model.DailyTask
import com.localpulse.app.domain.model.UserProgress

interface TasksRepository {
    suspend fun getTodayTasks(uid: String): Result<List<DailyTask>>
    suspend fun completeTask(uid: String, taskId: String, xp: Int): Result<UserProgress>
    suspend fun getUserProgress(uid: String): Result<UserProgress>
}
