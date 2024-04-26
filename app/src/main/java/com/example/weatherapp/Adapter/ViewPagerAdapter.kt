package com.example.weatherapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherapp.ForecastFragment
import com.example.weatherapp.FragmentBasicInformation

class ViewPagerAdapter(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentBasicInformation()
            1 -> ForecastFragment()
            else -> throw IllegalArgumentException("No such fragment")
        }
    }
}