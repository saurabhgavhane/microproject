package com.example.speedlimitdemo.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.speedlimitdemo.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: MyApplication) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = application


    @Singleton
    @Provides
    fun providesSharedPreference(context: Context): SharedPreferences =
        context.getSharedPreferences("MySharedPref", MODE_PRIVATE)

}