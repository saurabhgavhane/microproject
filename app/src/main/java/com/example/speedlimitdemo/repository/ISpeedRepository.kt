package com.example.speedlimitdemo.repository

import com.example.speedlimitdemo.model.DefaultSpeedLimit
import com.example.speedlimitdemo.model.SpeedLimit

interface ISpeedRepository {
    suspend fun getDefaultSpeedLimit(carId: String, fleetId: String): DefaultSpeedLimit?
    suspend fun getSpeedLimitForCar(carId: String): SpeedLimit?
    suspend fun setSpeedLimitForCar(speedLimit: SpeedLimit)
}