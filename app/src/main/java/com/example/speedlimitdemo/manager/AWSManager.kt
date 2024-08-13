package com.example.speedlimitdemo.manager

import android.util.Log

class AWSManager : INotificationClient {

    override fun sendNotification(title: String, message: String, carId: String) {
        //Send AWS notification...
        Log.d(FirebaseManager::class.java.canonicalName,"AWS notification sent..")
    }

}
