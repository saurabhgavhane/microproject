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

/*
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }


    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Start the Location Service.
                        Intent(this, SpeedService::class.java).also {
                            startService(it)
                        }

                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {


                        Toast.makeText(
                            this,
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

*/
