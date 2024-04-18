package com.example.weatherapp.Activity

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class CityToBeChosenActivity : AppCompatActivity() {
    private val cityAdapter by lazy { CityAdapter() }
    private val cityAdapterForFavouritesCities by lazy { CityAdapter() }
    private lateinit var cityViewer : RecyclerView
    private lateinit var favCitiesViewer : RecyclerView
    private lateinit var newCityTextHolder : EditText
    private lateinit var test : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_city_to_be_chosen)
        cityViewer = findViewById(R.id.cityList)
        newCityTextHolder = findViewById(R.id.newCityTextHolder)
        favCitiesViewer = findViewById(R.id.favouriteCityList)
        test = findViewById(R.id.testText)

        newCityTextHolder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: android.text.Editable?) {
                if(s?.length!! >= 3 ) {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val gson = Gson()
                            var weatherObject: List<CityApi.CityApiItem>? = null

                            val url =
                                URL("https://api.openweathermap.org/geo/1.0/direct?q=$s&limit=4&appid=$API_KEY")
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

                                weatherObject = gson.fromJson(response, Array<CityApi.CityApiItem>::class.java).toList()

                                weatherObject.let {
                                    val list = it.toMutableList()
                                    if (list != null) {
                                        runOnUiThread {
                                            cityAdapter.differ.submitList(list)
                                            val context = this@CityToBeChosenActivity
                                            cityViewer.apply {
                                                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                                                adapter = cityAdapter
                                            }
                                        }
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@CityToBeChosenActivity,
                                        "Failed to connect",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@CityToBeChosenActivity,
                                    e.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        })

        //TODO: fix Load favourite cities
        val file = File(filesDir, "fav_cities.txt")
        if (file.exists()) {
            val reader = BufferedReader(file.reader())
            val cities = reader.readLines()
            val list = mutableListOf<CityApi.CityApiItem>()
            for (city in cities) {
                list.add(CityApi.CityApiItem(null, 0.0, null, 0.0, city, null))
            }

            test.setText(list.toString())
            runOnUiThread {
                cityAdapterForFavouritesCities.differ.submitList(list)
                val context = this@CityToBeChosenActivity
                favCitiesViewer.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = cityAdapterForFavouritesCities
                }
            }
        }


    }
}