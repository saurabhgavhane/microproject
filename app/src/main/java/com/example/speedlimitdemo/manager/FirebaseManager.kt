package com.example.speedlimitdemo.manager

import android.util.Log
import javax.inject.Inject

class FirebaseManager @Inject constructor() : INotificationClient {

    override fun sendNotification(title: String, message: String, carId: String) {
        //Send firebase notification...
        Log.d(FirebaseManager::class.java.canonicalName,"Firebase notification sent..")
    }

}
