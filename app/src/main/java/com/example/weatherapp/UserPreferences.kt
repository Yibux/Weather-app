package com.example.weatherapp

import android.content.Context

class UserPreferences(val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("weatherApp", Context.MODE_PRIVATE)

    fun saveTemperatureUnit(unit: String) {
        sharedPreferences.edit().putString("temperatureUnit", unit).apply()
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getString("temperatureUnit", "metric") ?: "metric"
    }

    public fun saveFetchWeatherFrequency(frequency: String) {
        sharedPreferences.edit().putString("fetchWeatherFrequency", frequency).apply()
    }

    fun getFetchWeatherFrequency(): String {
        return sharedPreferences.getString("fetchWeatherFrequency", "10 minutes") ?: "10 minutes"
    }
}