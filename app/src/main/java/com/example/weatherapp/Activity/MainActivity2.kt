package com.example.weatherapp.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Adapter.ForecastAdapter
import com.example.weatherapp.Model.CityApi
import com.example.weatherapp.Model.CurrentWeatherApiClass
import com.example.weatherapp.Model.ForecastWeatherApi
import com.example.weatherapp.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

public const val API_KEY = "fe02a6b6389e2ba9aff21103d2dbe6fd"

class MainActivity2 : AppCompatActivity() {

    private lateinit var cityTextView: TextView

    private lateinit var temperatureTextView: TextView
    private lateinit var temperatureMinTextView: TextView
    private lateinit var temperatureMaxTextView: TextView

    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var weatherPic: ImageView
    private lateinit var cityIcon: ImageView

    private lateinit var forecastView: RecyclerView

    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        cityTextView = findViewById(R.id.city)

        temperatureTextView = findViewById(R.id.temperature)
        temperatureMinTextView = findViewById(R.id.temperatureMin)
        temperatureMaxTextView = findViewById(R.id.temperatureMax)

        humidityTextView = findViewById(R.id.humidity)
        pressureTextView = findViewById(R.id.pressureTextView)
        windTextView = findViewById(R.id.wind)
        weatherPic = findViewById(R.id.weatherPic)

        forecastView = findViewById(R.id.forecastList)
        cityIcon = findViewById(R.id.addCityIcon)

        cityIcon.setOnClickListener{
            val intent = Intent(this, CityToBeChosenActivity::class.java)
            startActivity(intent)
        }



        fetchWeatherData("Warsaw")
        cityTextView.setText("Warsaw")
        fetchWeatherForecast("Warsaw")
    }

    private fun fetchWeatherForecast(city: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val file = File(filesDir, "$city.json")
                val gson = Gson()
                var weatherObject: ForecastWeatherApi? = null

//                if (file.exists()) {
//                val bufferedReader = file.bufferedReader()
//                val inputString = bufferedReader.use { it.readText() }

//                weatherObject = gson.fromJson(inputString, ForecastWeatherApi::class.java)
//                } else {
                val url =
                    URL("https://api.openweathermap.org/data/2.5/forecast?q=$city&appid=$API_KEY")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(stream))
                    val buffer = StringBuffer()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        buffer.append(line + "\n")
                    }
                    val response = buffer.toString()

                    weatherObject = gson.fromJson(response, ForecastWeatherApi::class.java)

                    val weatherJson = gson.toJson(weatherObject)
                    val fos = openFileOutput("$city.json", Context.MODE_PRIVATE)
                    fos.write(weatherJson.toByteArray())
                    fos.close()

                    weatherObject?.let {
                        val list = it.list?.toMutableList()
                        if (list != null) {
                            runOnUiThread {
                                forecastAdapter.differ.submitList(list)
                                val context = this@MainActivity2
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                    adapter = forecastAdapter
                                }
                            }
                        }
                    }

                } else {
                    throw Exception("Failed to connect")
                }
//                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private fun fetchWeatherData(city: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val file = File(filesDir, "$city.json")
                val gson = Gson()
                var weatherObject: CurrentWeatherApiClass? = null

//                if (file.exists()) {
//                    val bufferedReader = file.bufferedReader()
//                    val inputString = bufferedReader.use { it.readText() }
//
//                    weatherObject = gson.fromJson(inputString, CurrentWeatherApiClass::class.java)
//                } else {
                    val url =
                        URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connect()

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val stream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(stream))
                        val buffer = StringBuffer()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            buffer.append(line + "\n")
                        }
                        val response = buffer.toString()

                        weatherObject = gson.fromJson(response, CurrentWeatherApiClass::class.java)

                        val weatherJson = gson.toJson(weatherObject)
                        val fos = openFileOutput("$city.json", Context.MODE_PRIVATE)
                        fos.write(weatherJson.toByteArray())
                        fos.close()
                    } else {
                        throw Exception("Failed to connect")
                    }
//                }

                val main = weatherObject?.main
                val tempC = main?.temp?.minus(273)?.roundToInt()
                val tempMin = main?.tempMin?.minus(273)?.roundToInt()
                val tempMax = main?.tempMax?.minus(273)?.roundToInt()
                val humidity = main?.humidity
                val pressure = main?.pressure
                val wind = weatherObject.wind?.speed

                launch(Dispatchers.Main) {
                    temperatureTextView.text = "${tempC}°C"
                    temperatureMinTextView.text = "${tempMin}°C"
                    temperatureMaxTextView.text = "${tempMax}°C"
                    humidityTextView.text = "$humidity%"
                    pressureTextView.text = "$pressure hPa"
                    windTextView.text = "$wind km/h"

                    when(weatherObject.weather?.get(0)?.main) {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}