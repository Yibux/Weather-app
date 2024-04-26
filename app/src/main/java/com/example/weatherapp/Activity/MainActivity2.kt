package com.example.weatherapp.Activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.Adapter.ViewPagerAdapter
import com.example.weatherapp.Model.CurrentWeatherApiClass
import com.example.weatherapp.Model.ForecastWeatherApi
import com.example.weatherapp.R
import com.example.weatherapp.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate

public const val API_KEY = "fe02a6b6389e2ba9aff21103d2dbe6fd"

class MainActivity2 : AppCompatActivity() {

    private lateinit var unitsFromFile: String
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        userPreferences = UserPreferences(this)
        unitsFromFile = userPreferences.getTemperatureUnit()

        setFetchTime()
        getWeather()

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = ViewPagerAdapter(this)

    }

    fun setFetchTime() {
        val period = when (userPreferences.getFetchWeatherFrequency()) {
            "10 seconds" -> 10
            "30 seconds" -> 30
            "1 minute" -> 60
            "5 minutes" -> 300
            "10 minutes" -> 600
            else -> 600
        }

        val timer = Timer()
        timer.schedule(object : TimerTask(){
            override fun run() {
                getWeather()
            }
        }, 0, period.toLong() * 1000)
    }

    fun getWeather() {
        val file = File(filesDir, "city.txt")
        if(file.exists()) {
            val bufferedReader = file.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            fetchWeatherData(inputString)
            fetchWeatherForecast(inputString)
            runOnUiThread{
                makeText(
                    this,
                    "Fetching weather data for $inputString",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            makeText(
                this,
                "No city selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fetchWeatherForecast(city: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val file = File(filesDir, "$city.json")
                val gson = Gson()
                var weatherObject: ForecastWeatherApi? = null

                if (isNetworkAvailable(this@MainActivity2)) {
                    val url =
                        URL("https://api.openweathermap.org/data/2.5/forecast?q=$city&units=metric&appid=$API_KEY")
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


                    } else {
                        withContext(Dispatchers.Main) {
                            makeText(
                                this@MainActivity2,
                                "Failed to connect",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        makeText(
                            this@MainActivity2,
                            "No internet connection. Fetching data from cache.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if(file.exists()) {
                        val bufferedReader = file.bufferedReader()
                        val inputString = bufferedReader.use { it.readText() }

                        weatherObject = gson.fromJson(inputString, ForecastWeatherApi::class.java)
                    } else {
                        withContext(Dispatchers.Main) {
                            makeText(
                                this@MainActivity2,
                                "File from cache does not exist. Please connect to the internet to fetch data.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    makeText(
                        this@MainActivity2,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchWeatherData(city: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val gson = Gson()
                var weatherObject: CurrentWeatherApiClass? = null

                if (isNetworkAvailable(this@MainActivity2)) {

                    val url =
                        URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_KEY")
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
                        val fos = openFileOutput("forecast_$city.json", Context.MODE_PRIVATE)
                        fos.write(weatherJson.toByteArray())
                        fos.close()
                    } else {
                        throw Exception("Failed to connect")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    makeText(
                        this@MainActivity2,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
        }
    }
}