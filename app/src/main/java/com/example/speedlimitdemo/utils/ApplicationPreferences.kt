package com.example.speedlimitdemo.utils

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ApplicationPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun setString(key: PreferenceType, value: String) = runBlocking(Dispatchers.IO) {
        with(sharedPreferences.edit()) {
            putString(key.toString(), value)
            apply()
        }
    }

    fun getString(key: PreferenceType) = runBlocking(Dispatchers.IO) {
        sharedPreferences.getString(key.toString(), null)
    }

    fun setInt(key: PreferenceType, value: Int) = runBlocking(Dispatchers.IO) {
        with(sharedPreferences.edit()) {
            putInt(key.toString(), value)
            apply()
        }
    }

    fun getInt(key: PreferenceType) = runBlocking(Dispatchers.IO) {
        sharedPreferences.getInt(key.toString(), 0)
    }
}
