package com.localpulse.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocalPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        com.localpulse.app.work.NotificationScheduler.scheduleDailyNotification(this)
    }
}
