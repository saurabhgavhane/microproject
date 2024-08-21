package com.example.speedlimitdemo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedlimitdemo.model.DefaultSpeedLimit
import com.example.speedlimitdemo.model.SpeedLimit
import com.example.speedlimitdemo.repository.SpeedRepository
import com.example.speedlimitdemo.utils.ApplicationPreferences
import com.example.speedlimitdemo.utils.PreferenceType
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeedViewModel @Inject constructor(private val repository: SpeedRepository) : ViewModel() {

    @Inject
    lateinit var applicationPreferences: ApplicationPreferences

    private var speedLimit: SpeedLimit? = null
    private lateinit var defaultSpeedLimit: DefaultSpeedLimit
    val speedLiveData = MutableLiveData<Int>()
    val speedLimitExceededLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    fun checkSpeed(currentSpeed: Int) {
        val maxSpeed: Int = speedLimit?.maxSpeed ?: defaultSpeedLimit.maxSpeed
        speedLiveData.postValue(currentSpeed)
        Log.d(SpeedViewModel::class.java.canonicalName,"Max Speed : $maxSpeed   Current Speed:$currentSpeed")
        if (currentSpeed > maxSpeed) {
            speedLimitExceededLiveData.postValue(true)
            val carId:String = applicationPreferences.getString(PreferenceType.CAR_ID)?:""

            //One can add check/delay of specific time to avoid sending continuous notification.
            repository.sendNotificationToCompany(
                title = "Speed Limit Exceeded.",
                message = "Car : $carId has exceeded the speed limit.",
                carId = carId
            )
        } else {
            speedLimitExceededLiveData.postValue(false)
        }
    }

    fun getDefaultSpeedLimit(carId:String, fleetId: String) {
        viewModelScope.launch {
            defaultSpeedLimit = repository.getDefaultSpeedLimit(carId,fleetId)
            Log.d(SpeedViewModel::class.java.canonicalName,"Default Speed: $defaultSpeedLimit")
            //Use default speed limit if no specific limit is set for the car
        }
    }

    fun getSpeedLimitForCar(carId: String) {
        viewModelScope.launch {
            speedLimit = repository.getSpeedLimitForCar(carId)
            Log.d(SpeedViewModel::class.java.canonicalName,"Max Speed: $speedLimit")
            // Use this speed limit if available
        }
    }

    fun setSpeedLimitForCar(carId: String, maxSpeed: Int) {
        viewModelScope.launch {
            repository.setSpeedLimitForCar(SpeedLimit(carId, maxSpeed))
        }
    }
}
