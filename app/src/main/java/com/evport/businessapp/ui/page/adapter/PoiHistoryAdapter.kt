package com.evport.businessapp.ui.page.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.PoiItem
import com.evport.businessapp.R
import com.evport.businessapp.databinding.AdapterPoiHistoryBinding

import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

class PoiHistoryAdapter(context: Context?) :
    SimpleDataBindingAdapter<PoiItem, AdapterPoiHistoryBinding>(
        context,
        R.layout.adapter_poi_history,
        object : DiffUtil.ItemCallback<PoiItem>() {
            override fun areItemsTheSame(
                oldItem: PoiItem,
                newItem: PoiItem
            ): Boolean {
                return oldItem == newItem
                //                return false;
            }

            override fun areContentsTheSame(
                oldItem: PoiItem,
                newItem: PoiItem
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterPoiHistoryBinding,
        item: PoiItem,
        holder: RecyclerView.ViewHolder
    ) {
        //  binding.info = item
        var index = holder.absoluteAdapterPosition;
        var listSize = currentList.size
        if (index < listSize - 1) {
            binding.cityName.text = item.cityName
            binding.title.text = item.title
            binding.content.visibility = View.VISIBLE
            binding.clearHistory.visibility = View.GONE
        } else {
            binding.content.visibility = View.GONE
            binding.clearHistory.visibility = View.VISIBLE
        }


    }

}