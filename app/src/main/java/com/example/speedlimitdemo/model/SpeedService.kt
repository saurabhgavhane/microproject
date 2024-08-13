package com.example.speedlimitdemo.model

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.example.speedlimitdemo.MyApplication
import com.example.speedlimitdemo.utils.Constants
import com.example.speedlimitdemo.utils.NotificationsHelper
import com.example.speedlimitdemo.viewmodel.SpeedViewModel
import javax.inject.Inject

class SpeedService : Service() {

    @Inject
    lateinit var speedViewModel: SpeedViewModel

    private lateinit var locationManager: LocationManager
    private val locationListener = LocationListenerImpl()

    companion object {
        const val SERVICE_ID = 105
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(SpeedService::class.java.canonicalName, "SERVICE ON CREATE...")
        (application as MyApplication).applicationComponent.inject(this)
        /*val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher_round))
            .setContentTitle("Detection Location")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setChannelId(CHANNEL_ID)
            .setSilent(false)
            .setAutoCancel(false)
            .build()

        startForeground(
            SERVICE_ID, notificationBuilder, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )*/

        startAsForegroundService()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        super.onCreate()
    }

    private fun startAsForegroundService() {
        // create the notification channel
        val channel = NotificationsHelper.createNotificationChannel()
        NotificationsHelper.getNotificationManager(context = this).createNotificationChannel(channel)

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.buildForegroundNotification(
                this,
                title = "Detecting vehicle speed.",
                description = "Detecting vehicle speed....",
                isOngoing = true
            ),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()

        //Call Vehicle HAL API to get vehicle speed data...
        return START_STICKY
    }

    private fun startLocationUpdates() {
        try {
            Log.d(SpeedService::class.java.canonicalName, "STARTING LOCATION UPDATES..")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                Constants.MIN_TIME,
                Constants.MIN_DISTANCE,
                locationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private inner class LocationListenerImpl : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(SpeedService::class.java.canonicalName, "SPEED : ${location.speed}")
            // Convert m/s to km/h
            val speedInKmH: Double = if (Constants.SHOW_IN_KM) location.speed * 3.6
            else location.speed as Double
            //Update view model livedata
            speedViewModel.checkSpeed(speedInKmH)

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(Service::class.java.canonicalName, "Service On Destroy")
        locationManager.removeUpdates(locationListener)
    }
}
