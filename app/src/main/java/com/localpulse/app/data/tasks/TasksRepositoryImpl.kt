package com.localpulse.app.data.tasks

import com.google.firebase.firestore.FirebaseFirestore
import com.localpulse.app.domain.model.DailyTask
import com.localpulse.app.domain.model.TasksBank
import com.localpulse.app.domain.model.UserProgress
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TasksRepository {

    private fun getTodayDateString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(cal.time)
    }

    override suspend fun getTodayTasks(uid: String): Result<List<DailyTask>> {
        return try {
            val todayDate = getTodayDateString()
            val tasksDocRef = firestore.collection("users").document(uid).collection("daily_tasks").document(todayDate)

            // Try to get existing tasks for today
            val snapshot = tasksDocRef.get().await()

            if (snapshot.exists()) {
                // Return existing tasks, mapping completed status from array
                val completedIds = snapshot.get("completedIds") as? List<String> ?: emptyList()
                val tasksData = snapshot.get("tasks") as? List<Map<String, Any>> ?: emptyList()
                
                val resultTasks = tasksData.map { taskMap ->
                    val id = taskMap["id"] as? String ?: ""
                    DailyTask(
                        id = id,
                        emoji = taskMap["emoji"] as? String ?: "",
                        title = taskMap["title"] as? String ?: "",
                        description = taskMap["description"] as? String ?: "",
                        xp = (taskMap["xp"] as? Number)?.toInt() ?: 10,
                        category = taskMap["category"] as? String ?: "",
                        isCompleted = completedIds.contains(id)
                    )
                }
                Result.success(resultTasks)
            } else {
                // Generate new tasks
                val cal = Calendar.getInstance()
                val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
                val newTasks = TasksBank.getTasksForToday(dayOfYear)

                // Save new tasks
                val initialData = hashMapOf(
                    "tasks" to newTasks,
                    "completedIds" to emptyList<String>()
                )
                tasksDocRef.set(initialData).await()

                // Check for streak reset
                val progressResult = getUserProgress(uid).getOrNull()
                if (progressResult != null && progressResult.lastTaskDate.isNotEmpty() && progressResult.lastTaskDate != getTodayDateString()) {
                     val newStreak = if (progressResult.lastTaskDate == getYesterdayDateString() && progressResult.tasksCompletedToday == 3) {
                         progressResult.currentStreak
                     } else {
                         0
                     }
                     // Update progress to reset tasksCompletedToday
                     firestore.collection("users").document(uid).collection("progress").document("data")
                         .set(
                             progressResult.copy(
                                 tasksCompletedToday = 0,
                                 lastTaskDate = todayDate,
                                 currentStreak = newStreak
                             )
                         ).await()
                }

                Result.success(newTasks)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTask(uid: String, taskId: String, xp: Int): Result<UserProgress> {
        return try {
            val todayDate = getTodayDateString()
            val tasksDocRef = firestore.collection("users").document(uid).collection("daily_tasks").document(todayDate)

            // Update completedIds
            val snapshot = tasksDocRef.get().await()
            val completedIds = (snapshot.get("completedIds") as? List<String> ?: emptyList()).toMutableList()
            
            if (!completedIds.contains(taskId)) {
                completedIds.add(taskId)
                tasksDocRef.update("completedIds", completedIds).await()

                // Update progress
                val currentProgress = getUserProgress(uid).getOrDefault(UserProgress(uid = uid))
                val newTotalXp = currentProgress.totalXp + xp
                val newLevel = minOf((newTotalXp / 100) + 1, 6)
                
                var newStreak = currentProgress.currentStreak
                val newTasksCompletedToday = currentProgress.tasksCompletedToday + 1
                
                // If it's the first task of the day and we did not do it yesterday, it's already 0 streak. 
                // But if they reach 3 tasks today AND streak was updated, wait... let's just do:
                // Actually the problem statement: "Adicionar taskId em completedIds", "Somar XP ao totalXp", "Calcular novo level: level = (totalXp / 100) + 1 (máximo 6)"
                // "Incrementar streak se completou tarefas ontem" (handled in generate, but actually if they complete all 3 today we can increment streak).
                if (newTasksCompletedToday == 3) {
                    newStreak += 1
                }

                val newProgress = currentProgress.copy(
                    totalXp = newTotalXp,
                    level = newLevel,
                    tasksCompletedToday = newTasksCompletedToday,
                    lastTaskDate = todayDate,
                    currentStreak = newStreak
                )

                firestore.collection("users").document(uid).collection("progress").document("data")
                    .set(newProgress).await()

                Result.success(newProgress)
            } else {
                getUserProgress(uid)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProgress(uid: String): Result<UserProgress> {
        return try {
            val progressRef = firestore.collection("users").document(uid).collection("progress").document("data")
            val snapshot = progressRef.get().await()
            if (snapshot.exists()) {
                val progress = snapshot.toObject(UserProgress::class.java)
                Result.success(progress ?: UserProgress(uid = uid))
            } else {
                Result.success(UserProgress(uid = uid))
            }
        } catch (e: Exception) {
             Result.failure(e)
        }
    }
}
