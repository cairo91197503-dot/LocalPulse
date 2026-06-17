package com.localpulse.app.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.localpulse.app.data.tasks.TasksRepository
import com.localpulse.app.domain.model.DailyTask
import com.localpulse.app.domain.model.UserProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<DailyTask>>(emptyList())
    val tasks: StateFlow<List<DailyTask>> = _tasks

    private val _progress = MutableStateFlow<UserProgress?>(null)
    val progress: StateFlow<UserProgress?> = _progress

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadTasks() }

    fun loadTasks() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            tasksRepository.getTodayTasks(uid).onSuccess { _tasks.value = it }
            tasksRepository.getUserProgress(uid).onSuccess { _progress.value = it }
            _isLoading.value = false
        }
    }

    fun completeTask(taskId: String, xp: Int) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            tasksRepository.completeTask(uid, taskId, xp).onSuccess {
                _progress.value = it
                _tasks.value = _tasks.value.map { task ->
                    if (task.id == taskId) task.copy(isCompleted = true) else task
                }
            }
        }
    }
}
