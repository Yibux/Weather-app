package com.example.weatherapp.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import com.example.weatherapp.Activity.CityToBeChosenActivity
import com.example.weatherapp.Activity.MainActivity2
import com.example.weatherapp.R
import com.example.weatherapp.UserPreferences
import java.io.File

class FragmentLogicButtons : Fragment(){

    private lateinit var cityIcon: ImageView
    private lateinit var favouriteCityIcon: ImageView
    private lateinit var settingsIcon: RadioGroup
    private lateinit var temperatureSet: Switch
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logic_buttons, container, false)
        val radioOptions = arrayOf("1 minute", "5 minutes", "10 minutes")

        cityIcon = view.findViewById(R.id.addCityIcon)
        favouriteCityIcon = view.findViewById(R.id.addToFavIcon)
        settingsIcon = view.findViewById(R.id.fetchWeatherFrequency)
        temperatureSet = view.findViewById(R.id.temperature)
        val userPreferences = UserPreferences(requireContext())
        val fetchWeatherFrequency = userPreferences.getFetchWeatherFrequency()

        for ((index, option) in radioOptions.withIndex()) {
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()
            radioButton.text = option
            radioButton.setTextColor(Color.WHITE)
            settingsIcon.addView(radioButton)

            if (fetchWeatherFrequency == option) {
                settingsIcon.check(radioButton.id)
            }
        }

        settingsIcon.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<View>(checkedId)
            val checkedIndex = group.indexOfChild(checkedRadioButton)
            val selectedOption = radioOptions[checkedIndex]
            userPreferences.saveFetchWeatherFrequency(selectedOption)
            val intent = Intent(requireContext(), MainActivity2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        temperatureSet.isChecked = userPreferences.getTemperatureUnit() != "metric"

        temperatureSet.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked) {
                userPreferences.saveTemperatureUnit("imperial")
            } else {
                userPreferences.saveTemperatureUnit("metric")
            }

            val fragmentManager = (activity as MainActivity2).supportFragmentManager
            for (fragment in fragmentManager.fragments) {
                if (fragment != null) {
                    fragmentManager.beginTransaction().detach(fragment).commitNowAllowingStateLoss()
                    fragmentManager.beginTransaction().attach(fragment).commitAllowingStateLoss()
                }
            }
        }


        val cityFile = File(context?.filesDir, "city.txt")
        if (cityFile.exists()) {
            val bufferedReader = cityFile.bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            setOnClickIcons(inputString)
        } else
            setOnClickIcons("No city selected")

        return view
    }

    private fun handleFavouriteCityIcon(cityName: String) {
        val file = File(context?.filesDir, "fav_cities.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        if (cityName != "No city selected") {
            val isCityFavourite = file.readText().contains(cityName)

            if (isCityFavourite) {
                favouriteCityIcon.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                favouriteCityIcon.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
    }

    private fun setOnClickIcons(cityName: String) {
        handleFavouriteCityIcon(cityName)
        cityIcon.setOnClickListener{
            val intent = Intent(requireContext(), CityToBeChosenActivity::class.java)
            startActivity(intent)
        }

        favouriteCityIcon.setOnClickListener {
            val file = File(context?.filesDir, "fav_cities.txt")
            if (!file.exists()) {
                file.createNewFile()
            }

                if (cityName != "No city selected") {
                    val isCityFavourite = file.readText().contains(cityName)

                    if (isCityFavourite) {
                        val bufferedReader = file.bufferedReader()
                        val country = bufferedReader.use { it.readText() }
                        val listOfNewFavoriteCities = mutableListOf<String>()
                        country.lines().forEach { line ->
                            if (!line.contains(cityName) && line.isNotBlank()) {
                                listOfNewFavoriteCities.add(line + "\n")
                            }
                        }
                        file.delete()
                        file.createNewFile()
                        val fos = requireContext().openFileOutput("fav_cities.txt", Context.MODE_APPEND)
                        listOfNewFavoriteCities.forEach {
                            fos.write(it.toByteArray())
                        }

                        favouriteCityIcon.setImageResource(android.R.drawable.btn_star_big_off)
                        makeText(
                            requireContext(),
                            "City removed from favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val fos = requireContext().openFileOutput("fav_cities.txt", Context.MODE_APPEND)

                        val file2 = File(context?.filesDir, "country.txt")
                        val bufferedReader = file2.bufferedReader()
                        val country = bufferedReader.use { it.readText() }

                        fos.write(cityName.toByteArray())
                        fos.write(" ".toByteArray())
                        fos.write(country.toByteArray())
                        fos.write("\n".toByteArray())
                        fos.close()
                        favouriteCityIcon.setImageResource(android.R.drawable.btn_star_big_on)
                        makeText(
                            requireContext(),
                            "City $cityName added to favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                makeText(
                    requireContext(),
                    "No city selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}