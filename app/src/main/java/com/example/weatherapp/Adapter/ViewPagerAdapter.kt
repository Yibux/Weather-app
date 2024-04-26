package com.example.weatherapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapp.fragment.ForecastFragment
import com.example.weatherapp.fragment.FragmentBasicInformation
import com.example.weatherapp.fragment.FragmentLogicButtons

class ViewPagerAdapter(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentBasicInformation()
            1 -> ForecastFragment()
            2 -> FragmentLogicButtons()
            else -> throw IllegalArgumentException("No such fragment")
        }
    }
}