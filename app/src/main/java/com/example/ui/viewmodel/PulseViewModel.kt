package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// Represents a historic pulse + health reading
data class PulseRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val heartRateBpm: Int,
    val spo2Percentage: Int,
    val category: String, // e.g. "Resting", "Active", "Normal", "Post-Workout"
    val note: String,
    val dateString: String = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(timestamp))
)

// User physical measurements
data class HealthProfile(
    val weightKg: Float = 72.5f,
    val heightCm: Float = 175.0f,
    val systolicBp: Int = 120,
    val diastolicBp: Int = 80,
    val isMetric: Boolean = true
)

// Scanner simulation stages
enum class ScanStage {
    IDLE,
    PREPARING,
    MEASURING,
    SUCCESS,
    CANCELLED
}

class PulseViewModel : ViewModel() {

    // Initial default records
    private val _records = MutableStateFlow<List<PulseRecord>>(
        listOf(
            PulseRecord(heartRateBpm = 64, spo2Percentage = 98, category = "Resting", note = "First morning reading. Calm."),
            PulseRecord(heartRateBpm = 112, spo2Percentage = 97, category = "Active", note = "Right after 15-min cardio jog."),
            PulseRecord(heartRateBpm = 72, spo2Percentage = 99, category = "Normal", note = "Idle computer work reading.")
        )
    )
    val records: StateFlow<List<PulseRecord>> = _records.asStateFlow()

    // Health profile info
    private val _profile = MutableStateFlow(HealthProfile())
    val profile: StateFlow<HealthProfile> = _profile.asStateFlow()

    // Scanner state variables
    private val _scanStage = MutableStateFlow(ScanStage.IDLE)
    val scanStage: StateFlow<ScanStage> = _scanStage.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _currentHeartRateSim = MutableStateFlow(0)
    val currentHeartRateSim: StateFlow<Int> = _currentHeartRateSim.asStateFlow()

    private val _currentSpO2Sim = MutableStateFlow(0)
    val currentSpO2Sim: StateFlow<Int> = _currentSpO2Sim.asStateFlow()

    private val _scanMessage = MutableStateFlow("Hold finger on core to begin scan")
    val scanMessage: StateFlow<String> = _scanMessage.asStateFlow()

    private var scanJob: Job? = null

    // Filter current logs
    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Update global search query/filter
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Update metrics profile values
    fun updateProfile(weight: Float, height: Float, systolic: Int, diastolic: Int, isMetric: Boolean) {
        _profile.value = HealthProfile(
            weightKg = weight,
            heightCm = height,
            systolicBp = systolic,
            diastolicBp = diastolic,
            isMetric = isMetric
        )
    }

    // Trigger local manual input logging
    fun logManualEntry(bpm: Int, spo2: Int, category: String, note: String) {
        val newRecord = PulseRecord(
            heartRateBpm = bpm.coerceIn(40, 220),
            spo2Percentage = spo2.coerceIn(80, 100),
            category = category,
            note = note.ifBlank { "Manual Log" }
        )
        _records.value = listOf(newRecord) + _records.value
    }

    // Delete single track
    fun deleteRecord(id: String) {
        _records.value = _records.value.filter { it.id != id }
    }

    // Scanner coroutine pipeline simulation
    fun startFingerScan() {
        if (_scanStage.value == ScanStage.MEASURING || _scanStage.value == ScanStage.PREPARING) return

        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _scanStage.value = ScanStage.PREPARING
            _scanProgress.value = 0f
            _scanMessage.value = "Calibrating optical sensor... Hold still."
            delay(1200)

            _scanStage.value = ScanStage.MEASURING
            val baseHR = Random.nextInt(62, 85)
            
            // Increment progress slowly over 6 seconds to create realism
            val scanDurationMs = 6000L
            val tickRateMs = 150L
            val totalTicks = (scanDurationMs / tickRateMs).toInt()

            for (tick in 1..totalTicks) {
                _scanProgress.value = tick.toFloat() / totalTicks
                
                // Fluctuating heartbeat simulator values
                val wiggle = Random.nextInt(-3, 3)
                _currentHeartRateSim.value = baseHR + wiggle
                _currentSpO2Sim.value = Random.nextInt(96, 100)

                _scanMessage.value = when {
                    tick < totalTicks * 0.3 -> "Detecting blood flow pulses..."
                    tick < totalTicks * 0.6 -> "Reading pulse wave patterns..."
                    else -> "Finalizing oxygen saturation level..."
                }

                delay(tickRateMs)
            }

            // Finished successfully! Generate values and log automatically
            _scanProgress.value = 1.0f
            _scanStage.value = ScanStage.SUCCESS
            _scanMessage.value = "Pulse scanning completed!"
            
            val finalHR = _currentHeartRateSim.value
            val finalSpO2 = _currentSpO2Sim.value
            
            logManualEntry(
                bpm = finalHR,
                spo2 = finalSpO2,
                category = "Normal",
                note = "Saved from live fingertip pulse scan."
            )
        }
    }

    // Interrupt finger pulse scanner
    fun cancelScan() {
        scanJob?.cancel()
        _scanProgress.value = 0f
        _scanStage.value = ScanStage.IDLE
        _scanMessage.value = "Hold finger on core to begin scan"
    }

    // Helper statistics getters
    fun getAverageHeartRate(): Int {
        val valid = _records.value
        if (valid.isEmpty()) return 0
        return valid.map { it.heartRateBpm }.average().toInt()
    }

    fun getAverageSpO2(): Int {
        val valid = _records.value
        if (valid.isEmpty()) return 0
        return valid.map { it.spo2Percentage }.average().toInt()
    }
}
