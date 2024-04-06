package com.example.weatherapp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Activity.MainActivity2
import com.example.weatherapp.Model.CityApi
import com.example.weatherapp.databinding.CityViewholderBinding
import com.example.weatherapp.databinding.SingleDayWeatherBinding

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>(){

    private lateinit var binding: CityViewholderBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CityViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: CityAdapter.ViewHolder, position: Int) {
        val binding = SingleDayWeatherBinding.bind(holder.itemView)
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, MainActivity2::class.java)
            intent.putExtra("lat", differ.currentList[position].lat)
            intent.putExtra("lon", differ.currentList[position].lon)
            intent.putExtra("name", differ.currentList[position].name)
            binding.root.context.startActivity(intent)
        }

    }

    override fun getItemCount() = differ.currentList.size

    private val diffCallback = object : DiffUtil.ItemCallback<CityApi.CityApiItem>() {
        override fun areItemsTheSame(oldItem: CityApi.CityApiItem, newItem: CityApi.CityApiItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CityApi.CityApiItem, newItem: CityApi.CityApiItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


}