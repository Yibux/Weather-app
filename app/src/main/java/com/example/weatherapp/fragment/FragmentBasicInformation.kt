package com.example.weatherapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp.Model.CurrentWeatherApiClass
import com.example.weatherapp.R
import com.example.weatherapp.UserPreferences
import com.google.gson.Gson
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class FragmentBasicInformation : Fragment() {

    private lateinit var cityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var temperatureMinTextView: TextView
    private lateinit var temperatureMaxTextView: TextView

    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var weatherPic: ImageView
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_basic_information, container, false)
        cityTextView = view.findViewById(R.id.city)
        temperatureTextView = view.findViewById(R.id.temperature)
        temperatureMinTextView = view.findViewById(R.id.temperatureMin)
        temperatureMaxTextView = view.findViewById(R.id.temperatureMax)

        humidityTextView = view.findViewById(R.id.humidity)
        pressureTextView = view.findViewById(R.id.pressureTextView)
        windTextView = view.findViewById(R.id.wind)
        weatherPic = view.findViewById(R.id.weatherPic)
        userPreferences = UserPreferences(requireContext())

        val file = File(context?.filesDir, "city.txt")
        if(file.exists()) {
            val bufferedReader = file.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            cityTextView.text = inputString

            if(inputString == "" || inputString.lowercase() == "no city selected") {
                return view
            }

            val file2 = File(context?.filesDir, "forecast_${inputString}.json")
            if(file2.exists()) {
                val bufferedReader2 = file2.bufferedReader()
                val inputString2 = bufferedReader2.use { it.readText() }
                val gson = Gson()
                val weatherObject = gson.fromJson(inputString2, CurrentWeatherApiClass::class.java)
                updateWeather(weatherObject)
            }
        } else {
            cityTextView.text = "No city selected"
        }

        return view
    }

    private fun updateWeather(weatherObject: CurrentWeatherApiClass?) {
        val main = weatherObject?.main
        val tempC = main?.temp?.roundToInt()
        val tempMin = main?.tempMin?.roundToInt()
        val tempMax = main?.tempMax?.roundToInt()
        val humidity = main?.humidity
        val pressure = main?.pressure
        val wind = weatherObject?.wind?.speed

        if(userPreferences.getTemperatureUnit() == "metric") {
            temperatureTextView.text = "${tempC}°C"
            temperatureMinTextView.text = "${tempMin}°C"
            temperatureMaxTextView.text = "${tempMax}°C"
            windTextView.text = "$wind km/h"
        }
        else {
            val fahrenheit = (tempC?.times(9)?.div(5))?.plus(32)
            temperatureTextView.text = "${fahrenheit}°F"
            temperatureMinTextView.text = "${fahrenheit}°F"
            temperatureMaxTextView.text = "${fahrenheit}°F"
            val milesPerHour = wind?.times(2.237)?.roundToLong()
            windTextView.text = "$milesPerHour mi/h"
        }

        humidityTextView.text = "$humidity%"
        pressureTextView.text = "$pressure hPa"

        when(weatherObject?.weather?.get(0)?.main) {
            "01d", "01n" -> weatherPic.setImageResource(R.drawable.sunny)
            "02d" -> weatherPic.setImageResource(R.drawable.cloudy_sunny)
            "02n" -> weatherPic.setImageResource(R.drawable.cloudy_sunny)
            "03d", "03n", "04d", "04n" -> weatherPic.setImageResource(R.drawable.cloudy)
            "09d", "09n", "10d", "10n" -> weatherPic.setImageResource(R.drawable.rainy)
            "11d", "11n" -> weatherPic.setImageResource(R.drawable.storm)
            "13d", "13n" -> weatherPic.setImageResource(R.drawable.snowy)
            "50d", "50n" -> weatherPic.setImageResource(R.drawable.windy)
            else -> weatherPic.setImageResource(R.drawable.sunny)
        }
    }
}