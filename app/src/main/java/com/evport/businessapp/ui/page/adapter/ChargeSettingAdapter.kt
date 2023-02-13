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
import com.evport.businessapp.data.bean.ChargeSetting
import com.evport.businessapp.databinding.AdapterChargeSettingBinding

/**
 * Create by KunMinX at 20/4/19
 */
class ChargeSettingAdapter(context: Context, val canDelete: Boolean = false) :
    SimpleDataBindingAdapter<ChargeSetting, AdapterChargeSettingBinding>(
        context,
        R.layout.adapter_charge_setting,
        object : DiffUtil.ItemCallback<ChargeSetting>() {
            override fun areItemsTheSame(
                oldItem: ChargeSetting,
                newItem: ChargeSetting
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ChargeSetting,
                newItem: ChargeSetting
            ): Boolean {
                return oldItem.chargeBoxId == newItem.chargeBoxId
            }
        }) {

    var deleteClick: ((position: Int,item: ChargeSetting) -> Any)?=null

    protected override fun onBindItem(
        binding: AdapterChargeSettingBinding,
        item: ChargeSetting,
        holder: RecyclerView.ViewHolder
    ) {
        if (canDelete)
            binding.ivDelete.visibility = View.VISIBLE
        else
            binding.ivDelete.visibility = View.GONE
        binding.ivDelete.setOnClickListener {
            deleteClick?.invoke(holder.bindingAdapterPosition,item)
        }

        binding.info = item
    }
}