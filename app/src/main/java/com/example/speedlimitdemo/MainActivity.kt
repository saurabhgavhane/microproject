package com.example.speedlimitdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speedlimitdemo.manager.NotificationManager
import com.example.speedlimitdemo.model.SpeedService
import com.example.speedlimitdemo.utils.ApplicationPreferences
import com.example.speedlimitdemo.utils.Constants
import com.example.speedlimitdemo.utils.NotificationsHelper
import com.example.speedlimitdemo.utils.PreferenceType
import com.example.speedlimitdemo.viewmodel.SpeedViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var speedViewModel: SpeedViewModel

    @Inject
    lateinit var applicationPreferences: ApplicationPreferences

    @Inject
    lateinit var notificationManager: NotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MyApplication).applicationComponent.inject(this)


        //Assuming we already have car id and fleet id.
        applicationPreferences.setString(PreferenceType.CAR_ID, Constants.CAR_ID)
        applicationPreferences.setString(PreferenceType.FLEET_ID, Constants.FLEET_ID)

        //Initiate notification client.
        notificationManager.initFirebaseClient()

        Log.d(
            MainActivity::class.java.canonicalName,
            "Car id :" + applicationPreferences.getString(PreferenceType.CAR_ID)
        )

        Log.d(
            MainActivity::class.java.canonicalName,
            "Fleet id :" + applicationPreferences.getString(PreferenceType.FLEET_ID)
        )

        // Fleet company sets default speed limit
        speedViewModel.getDefaultSpeedLimit(Constants.CAR_ID, Constants.FLEET_ID)


        // Rental agent sets a specific speed limit for a car.
        speedViewModel.setSpeedLimitForCar(Constants.CAR_ID, Constants.MAX_SPEED)


        //Get speed limit set for a car by agent.
        speedViewModel.getSpeedLimitForCar(Constants.CAR_ID)


        speedViewModel.speedLiveData.observe(this) { speed ->
            // Update UI with current speed
            findViewById<TextView>(R.id.speedTextView).text = "Speed: $speed km/h"
        }

        speedViewModel.speedLimitExceededLiveData.observe(this) { exceeded ->
            if (exceeded) {
                showWarningAlert()
            }
        }

        speedViewModel.errorLiveData.observe(this) { error ->
            // Handle error
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }

        Log.d(MainActivity::class.java.canonicalName, "STARTING SERVICE..")
        //checkLocationPermission()
        //Start the Location Service.
        Intent(this, SpeedService::class.java).also {
            startService(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(MainActivity::class.java.canonicalName, "Activity On Destroy")
        Intent(this, SpeedService::class.java).also {
            stopService(it)
        }
    }

    private fun showWarningAlert() {
        //For car we will show notification on screen.
        NotificationsHelper.buildNotification(
            context = this,
            title = "Speed Warning",
            description = "You are exceeding the speed limit!",
        )


        /*AlertDialog.Builder(this).setTitle("Speed Warning")
            .setMessage("You are exceeding the speed limit!")
            .setPositiveButton(android.R.string.ok, null).show()
            */
    }
}