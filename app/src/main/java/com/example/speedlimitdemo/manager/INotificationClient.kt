package com.example.speedlimitdemo.manager

interface INotificationClient {

    fun sendNotification(title: String, message: String, carId: String)

}
