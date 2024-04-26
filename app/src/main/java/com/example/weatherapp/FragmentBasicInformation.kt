package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp.Model.CurrentWeatherApiClass
import com.google.gson.Gson
import java.io.File
import kotlin.math.roundToInt

class FragmentBasicInformation : Fragment() {

    private lateinit var cityTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var temperatureMinTextView: TextView
    private lateinit var temperatureMaxTextView: TextView

    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var weatherPic: ImageView
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

        val file = File(context?.filesDir, "city.txt")
        if(file.exists()) {
            val bufferedReader = file.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            cityTextView.text = inputString

            val file = File(context?.filesDir, "forecast_${inputString}.json")
            if(file.exists()) {
                val bufferedReader = file.bufferedReader()
                val inputString = bufferedReader.use { it.readText() }
                val gson = Gson()
                val weatherObject = gson.fromJson(inputString, CurrentWeatherApiClass::class.java)
                updateWeather(weatherObject)
            }
        } else {
            cityTextView.text = "No city selected"
        }

        return view
    }

    private fun updateWeather(weatherObject: CurrentWeatherApiClass?) {
        val main = weatherObject?.main
        val tempC = main?.temp?.minus(273)?.roundToInt()
        val tempMin = main?.tempMin?.minus(273)?.roundToInt()
        val tempMax = main?.tempMax?.minus(273)?.roundToInt()
        val humidity = main?.humidity
        val pressure = main?.pressure
        val wind = weatherObject?.wind?.speed

        temperatureTextView.text = "${tempC}°C"
        temperatureMinTextView.text = "${tempMin}°C"
        temperatureMaxTextView.text = "${tempMax}°C"
        humidityTextView.text = "$humidity%"
        pressureTextView.text = "$pressure hPa"
        windTextView.text = "$wind km/h"

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