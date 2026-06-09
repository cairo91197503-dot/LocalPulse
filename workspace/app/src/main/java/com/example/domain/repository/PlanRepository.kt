package com.example.domain.repository

import com.example.domain.model.Plan
import com.example.domain.model.UserPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class PlanRepository {

    // Simulação do Firestore para o agente
    private val _userPlan = MutableStateFlow(UserPlan())
    val userPlan: Flow<UserPlan> = _userPlan.asStateFlow()

    init {
        resetMonthlyCounterIfNeeded()
    }

    fun isPro(): Boolean {
        return _userPlan.value.plan == Plan.PRO
    }

    fun getAiRepliesRemaining(): Int {
        if (isPro()) return Int.MAX_VALUE
        val remaining = 3 - _userPlan.value.aiRepliesUsedThisMonth
        return if (remaining > 0) remaining else 0
    }

    fun incrementAiRepliesUsed() {
        if (isPro()) return
        resetMonthlyCounterIfNeeded()
        _userPlan.update {
            it.copy(aiRepliesUsedThisMonth = it.aiRepliesUsedThisMonth + 1)
        }
    }

    fun resetMonthlyCounterIfNeeded() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val resetMonth = Calendar.getInstance().apply {
            timeInMillis = _userPlan.value.aiRepliesResetDate
        }.get(Calendar.MONTH)

        if (currentMonth != resetMonth || _userPlan.value.aiRepliesResetDate == 0L) {
            _userPlan.update {
                it.copy(
                    aiRepliesUsedThisMonth = 0,
                    aiRepliesResetDate = System.currentTimeMillis()
                )
            }
        }
    }
    
    // Método temporário para simular upgrade na demo
    fun simulateUpgradeToPro() {
        _userPlan.update {
            it.copy(plan = Plan.PRO)
        }
    }
}
