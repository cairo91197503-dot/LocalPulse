package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.MainViewModel
import com.example.ui.navigation.MainNavigation

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            Log.d("MainActivity", "Notification permission granted.")
        } else {
            // Inform user that that your app will not show notifications.
            Log.d("MainActivity", "Notification permission denied.")
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Review Alerts"
            val descriptionText = "Notifications for low rating reviews"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("REVIEW_ALERTS", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showLowRatingNotification(review: com.example.domain.model.Review) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val intent = Intent(
            Intent.ACTION_VIEW,
            android.net.Uri.parse("app://review_detail/${review.id}"),
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, review.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "REVIEW_ALERTS")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerta: Nota Baixa (${review.rating} estrelas)")
            .setContentText("O cliente comentou: \"${review.reviewText.take(50)}...\"")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_menu_send,
                "Responder com IA",
                pendingIntent
            )

        with(NotificationManagerCompat.from(this)) {
            notify(review.id.hashCode(), builder.build())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createNotificationChannel()
        askNotificationPermission()
        
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("MainActivity", "FCM token: $token")
        }

        setContent {
            MaterialTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                
                LaunchedEffect(Unit) {
                    mainViewModel.newLowRatingReview.collect { review ->
                        snackbarHostState.showSnackbar(
                            message = "Nova avaliação de ${review.rating} estrelas!"
                        )
                        showLowRatingNotification(review)
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainNavigation()
                    }
                }
            }
        }
    }
}
