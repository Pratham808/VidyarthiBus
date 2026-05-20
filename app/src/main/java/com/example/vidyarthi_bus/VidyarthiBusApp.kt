package com.example.vidyarthi_bus

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VidyarthiBusApp : Application() {
    override fun onCreate() {
        // Initialize persistence BEFORE anything else
        try {
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
                Log.d("VidyarthiBusApp", "Persistence enabled early")
            }
        } catch (e: Exception) {
            Log.e("VidyarthiBusApp", "Early persistence fail", e)
        }
        
        super.onCreate()
        Log.d("VidyarthiBusApp", "Application onCreate started")
        
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("alerts")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("VidyarthiBusApp", "Subscribed to alerts")
                    }
                }
        } catch (e: Exception) {
            Log.e("VidyarthiBusApp", "FCM init fail", e)
        }
    }
}