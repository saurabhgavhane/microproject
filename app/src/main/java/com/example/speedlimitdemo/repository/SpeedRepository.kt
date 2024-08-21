package com.example.speedlimitdemo.repository

import com.example.speedlimitdemo.manager.NotificationManager
import com.example.speedlimitdemo.model.DefaultSpeedLimit
import com.example.speedlimitdemo.model.SpeedLimit
import com.example.speedlimitdemo.utils.ApplicationPreferences
import com.example.speedlimitdemo.utils.Constants
import com.example.speedlimitdemo.utils.PreferenceType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SpeedRepository @Inject constructor() : ISpeedRepository {


    @Inject
    lateinit var applicationPreferences: ApplicationPreferences

    @Inject
    lateinit var notificationManager: NotificationManager


    // Send a notification through Firebase Cloud Messaging
    fun sendNotificationToCompany(title: String, message: String, carId: String) {
        notificationManager.sendNotification(title, message, carId)
    }


    override suspend fun getDefaultSpeedLimit(carId: String, fleetId: String): DefaultSpeedLimit {
        applicationPreferences.setInt(PreferenceType.DEFAULT_MAX_SPEED, Constants.DEFAULT_MAX_SPEED)
        return DefaultSpeedLimit(
            fleetId,
            applicationPreferences.getInt(PreferenceType.DEFAULT_MAX_SPEED)
        )
    }

    override suspend fun getSpeedLimitForCar(carId: String): SpeedLimit? {
        //API call to get speed limit value...
        val maxSpeed = applicationPreferences.getInt(PreferenceType.MAX_SPEED)
        return SpeedLimit(
            carId,
            if (maxSpeed == 0) applicationPreferences.getInt(PreferenceType.DEFAULT_MAX_SPEED) else maxSpeed
        )
    }

    override suspend fun setSpeedLimitForCar(speedLimit: SpeedLimit) {
        //Set speed limit for a car.
        applicationPreferences.setInt(PreferenceType.MAX_SPEED, speedLimit.maxSpeed)
    }
}
