/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evport.businessapp.ui.page.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Connector
import com.evport.businessapp.data.bean.Device
import com.evport.businessapp.databinding.AdapterStationDeviceListBinding

/**
 * Create by KunMinX at 20/4/19
 */
class StationDeviceAdapter(context: Context) :
    SimpleDataBindingAdapter<Device, AdapterStationDeviceListBinding>(
        context,
        R.layout.adapter_station_device_list,
        object : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(
                oldItem: Device,
                newItem: Device
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Device,
                newItem: Device
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var likeClick: ((item: Device, position:Int) -> Any)? = null
    var childClick: ((item: Connector, position:Int) -> Any)? = null
    override fun onBindItem(
        binding: AdapterStationDeviceListBinding,
        item: Device,
        holder: RecyclerView.ViewHolder
    ) {
        val s = StationDeviceChildAdapter(mContext).apply {
            setOnItemClickListener { item, position ->
               childClick?.invoke(item,position)
//                (Pair("pk", item.connectorPk))

            }
        }
        binding.adapter = s
        binding.info = item
        binding.selleRecyclerView.adapter = s
        s.submitList(item.connectors)
        if (item.isCollect)
            binding.ivLike.setImageResource(R.drawable.icon_station_like)
        else
            binding.ivLike.setImageResource(R.drawable.icon_unlike)
        binding.ivLike.setOnClickListener {
            likeClick?.invoke(item,holder.bindingAdapterPosition)
        }

    }

}