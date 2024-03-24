package com.example.weatherapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity2 : AppCompatActivity() {

    private lateinit var cityEditText: EditText
    private lateinit var getWeatherButton: Button
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var pressureTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        cityEditText = findViewById(R.id.cityEditText)
        getWeatherButton = findViewById(R.id.getWeatherButton)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        humidityTextView = findViewById(R.id.humidityTextView)
        pressureTextView = findViewById(R.id.pressureTextView)

        getWeatherButton.setOnClickListener {
            val city = cityEditText.text.toString()
            fetchWeatherData(city)
        }
    }

    private fun fetchWeatherData(city: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://api.openweathermap.org/data/2.5/weather?q=$city&appid=fe02a6b6389e2ba9aff21103d2dbe6fd")
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

                    val jsonObject = JSONObject(response)
                    val main = jsonObject.getJSONObject("main")
                    val temp = main.getString("temp")
                    val humidity = main.getString("humidity")
                    val pressure = main.getString("pressure")

                    launch(Dispatchers.Main) {
                        temperatureTextView.text = "Temperature: $temp°C"
                        humidityTextView.text = "Humidity: $humidity%"
                        pressureTextView.text = "Pressure: $pressure hPa"
                    }
                } else {
                    throw Exception("Failed to connect")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}