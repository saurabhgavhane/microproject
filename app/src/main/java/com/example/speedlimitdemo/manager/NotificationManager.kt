package com.example.speedlimitdemo.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor() {

    private var notificationClient: INotificationClient? = null


    fun initFirebaseClient() {
        notificationClient = FirebaseManager()
    }


    fun sendNotification(title: String, message: String, carId: String){
        notificationClient?.sendNotification(title,message,carId)
    }

    //Provision for AWS notification client.
    fun initAWSClient() {
        notificationClient = AWSManager()
    }


}