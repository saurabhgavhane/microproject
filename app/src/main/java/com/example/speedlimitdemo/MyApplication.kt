package com.example.speedlimitdemo

import android.app.Application
import com.example.speedlimitdemo.di.AppModule
import com.example.speedlimitdemo.di.ApplicationComponent
import com.example.speedlimitdemo.di.DaggerApplicationComponent
import com.example.speedlimitdemo.manager.NotificationManager
import javax.inject.Inject

class MyApplication : Application() {


    @Inject
    lateinit var notificationManager: NotificationManager


    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
    initDagger()
    }

    private fun initDagger(){
        applicationComponent = DaggerApplicationComponent.builder().appModule(AppModule(this)).build()
        //notificationManager.initFirebaseClient()
    }
}