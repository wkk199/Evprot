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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.HomeDeviceBean
import com.evport.businessapp.databinding.AdapterHomeStationDeviceChildBinding
import com.evport.businessapp.utils.homeDiviceTypeCanStart
import com.evport.businessapp.utils.homeDiviceTypeCanStartShow
import com.evport.businessapp.utils.socketTypeIcon

/**
 * Create by KunMinX at 20/4/19
 */
class HomeStationDeviceChildAdapter(context: Context?) :
    SimpleDataBindingAdapter<HomeDeviceBean, AdapterHomeStationDeviceChildBinding>(
        context,
        R.layout.adapter_home_station_device_child,
        object : DiffUtil.ItemCallback<HomeDeviceBean>() {
            override fun areItemsTheSame(
                oldItem: HomeDeviceBean,
                newItem: HomeDeviceBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HomeDeviceBean,
                newItem: HomeDeviceBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    var startClick: ((item: HomeDeviceBean) -> Any)? = null
    var updateClick: ((item: HomeDeviceBean, start: Boolean) -> Any)? = null
    var deleteClick: ((item: HomeDeviceBean, start: Boolean) -> Any)? = null
    protected override fun onBindItem(
        binding: AdapterHomeStationDeviceChildBinding,
        item: HomeDeviceBean,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        item.type.toString().socketTypeIcon()?.apply {
            binding.imgSocket.setImageResource(this)
        }
        binding.scan.isEnabled = item.status.toString().homeDiviceTypeCanStart()


        binding.llTime.isVisible = false
        binding.llTimeEnd.isVisible = false
        item.homeSchedule?.apply {
            binding.scan.isVisible = item.status.toString().homeDiviceTypeCanStartShow()
            binding.llTime.isVisible = startTime!=null
            binding.llTimeEnd.isVisible = true
            binding.tvStartTime.text = startTimeUtc2Str()
            binding.tvEndTime.text = stopTimeUtc2Str()
            binding.imgSDel.isEnabled = startTime!=null
            binding.imgEDel.isEnabled = stopTime!=null
        }
        binding.scan.setOnClickListener {
            startClick?.invoke(item)
        }
        binding.imgSEdit.setOnClickListener {
            updateClick?.invoke(item, true)
        }
        binding.imgSDel.setOnClickListener {
            deleteClick?.invoke(item, true)
        }
        binding.imgEEdit.setOnClickListener {
            updateClick?.invoke(item, false)
        }
        binding.imgEDel.setOnClickListener {
            deleteClick?.invoke(item, false)
        }

    }


}