package com.example.weatherapp.Activity

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Adapter.CityAdapter
import com.example.weatherapp.Model.CityApi
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityCityToBeChosenBinding
import com.example.weatherapp.databinding.CityViewholderBinding
import com.example.weatherapp.databinding.SingleDayWeatherBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CityToBeChosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCityToBeChosenBinding
    private val cityAdapter by lazy { CityAdapter() }
    private lateinit var cityViewer : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        cityViewer = findViewById(R.id.cityList)

        binding = ActivityCityToBeChosenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            newCityTextHolder.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: android.text.Editable?) {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val gson = Gson()
                            var weatherObject: CityApi? = null

                            val url =
                                URL("http://api.openweathermap.org/geo/1.0/direct?q=London&limit=5&appid=$API_KEY")
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

                                weatherObject = gson.fromJson(response, CityApi::class.java)

                                weatherObject?.let {
                                    cityAdapter.differ.submitList(it as List<CityApi.CityApiItem>?)
                                    cityViewer.apply {
                                        layoutManager = LinearLayoutManager(this@CityToBeChosenActivity, LinearLayoutManager.HORIZONTAL, false)
                                        adapter = cityAdapter
                                    }
                                }


                            } else {
                                throw Exception("Failed to connect")
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }
}