package com.example.speedlimitdemo.di

import com.example.speedlimitdemo.MainActivity
import com.example.speedlimitdemo.model.SpeedService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(service : SpeedService)

}