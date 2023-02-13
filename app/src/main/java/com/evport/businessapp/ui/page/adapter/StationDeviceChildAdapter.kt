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
import com.evport.businessapp.databinding.AdapterStationDeviceChildBinding
import com.evport.businessapp.utils.socketTypeIcon

/**
 * Create by KunMinX at 20/4/19
 */
class StationDeviceChildAdapter(context: Context?) :
    SimpleDataBindingAdapter<Connector, AdapterStationDeviceChildBinding>(
        context,
        R.layout.adapter_station_device_child,
        object : DiffUtil.ItemCallback<Connector>() {
            override fun areItemsTheSame(
                oldItem: Connector,
                newItem: Connector
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Connector,
                newItem: Connector
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterStationDeviceChildBinding,
        item: Connector,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        item.socket.toString().socketTypeIcon()?.apply {
            binding.imgSocket.setImageResource(this)
        }
        
    }

}