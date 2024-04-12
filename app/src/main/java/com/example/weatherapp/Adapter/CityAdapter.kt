package com.example.weatherapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Activity.MainActivity2
import com.example.weatherapp.Model.CityApi
import com.example.weatherapp.databinding.CityViewholderBinding

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>(){

    private lateinit var binding: CityViewholderBinding

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = CityViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = CityViewholderBinding.bind(holder.itemView)
        binding.cityName.text = differ.currentList[position].name
        binding.root.setOnClickListener {
            val cityName = differ.currentList[position].name
            val fos = binding.root.context.openFileOutput("city.txt", Context.MODE_PRIVATE)
            fos.write(cityName?.toByteArray() ?: byteArrayOf())
            fos.close()
            val intent = Intent(binding.root.context, MainActivity2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
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