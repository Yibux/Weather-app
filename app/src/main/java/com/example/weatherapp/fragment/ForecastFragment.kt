package com.example.weatherapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Adapter.ForecastAdapter
import com.example.weatherapp.Model.ForecastWeatherApi
import com.example.weatherapp.R
import com.google.gson.Gson
import java.io.File

class ForecastFragment : Fragment() {
    private lateinit var forecastView: RecyclerView
        private val forecastAdapter by lazy { ForecastAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)

        forecastView = view.findViewById(R.id.forecastList)

        val cityFile = File(context?.filesDir, "city.txt")
        if (cityFile.exists()) {
            val bufferedReader = cityFile.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            if(inputString == "" || inputString.lowercase() == "no city selected") {
                return view
            }
            val file = File(context?.filesDir, "$inputString.json")
            if (file.exists()) {
                val bufferedReader2 = file.bufferedReader()
                val inputString2 = bufferedReader2.use { it.readText() }
                val gson = Gson()
                val weatherObject = gson.fromJson(inputString2, ForecastWeatherApi::class.java)
                updateForecast(weatherObject)
            }
        }
        return view
    }

    private fun updateForecast(forecast: ForecastWeatherApi) {

            forecast.let {
                val list = it.list?.toMutableList()
                if (list != null) {
                    forecastAdapter.differ.submitList(list)
                    forecastView.apply {
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        adapter = forecastAdapter
                    }
                }
            }
        }

}

