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
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Fee
import com.evport.businessapp.databinding.AdapterStationDeviceFeeBinding
import com.evport.businessapp.utils.DateUtil

/**
 * Create by KunMinX at 20/4/19
 */
class StationDeviceFeeAdapter(context: Context?) :
    SimpleDataBindingAdapter<Fee, AdapterStationDeviceFeeBinding>(
        context,
        R.layout.adapter_station_device_fee,
        object : DiffUtil.ItemCallback<Fee>() {
            override fun areItemsTheSame(
                oldItem: Fee,
                newItem: Fee
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Fee,
                newItem: Fee
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterStationDeviceFeeBinding,
        item: Fee,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.time.setTextColor(mContext.resources.getColor(R.color.black_5E5E5E))
        binding.tvTitle.setTextColor(mContext.resources.getColor(R.color.color_8F9293))
        binding.tvAll.setTextColor(mContext.resources.getColor(R.color.color_8F9293))
        binding.tvUnit.setTextColor(mContext.resources.getColor(R.color.color_8F9293))

        item.time?.split("-")?.apply {
            if (DateUtil.containNowDate(this[0].trim(),this[1].trim())){
                binding.time.setTextColor(mContext.resources.getColor(R.color.color_006999))
                binding.tvTitle.setTextColor(mContext.resources.getColor(R.color.idle))
                binding.tvAll.setTextColor(mContext.resources.getColor(R.color.idle))
                binding.tvUnit.setTextColor(mContext.resources.getColor(R.color.idle))
                binding.tvNow.visibility=View.VISIBLE
                binding.clBg.background = mContext.resources.getDrawable(R.drawable.shape_filter_bt1)
                binding.line2.visibility=View.GONE
            }else{
                binding.tvNow.visibility=View.GONE
                binding.line2.visibility=View.VISIBLE
                binding.clBg.background = null
            }
        }


    }

}