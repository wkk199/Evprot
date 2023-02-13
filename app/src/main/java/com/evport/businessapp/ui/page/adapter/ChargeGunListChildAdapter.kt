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
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.BaseDataBindingAdapter.OnItemClickListener
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Sockets
import com.evport.businessapp.databinding.AdapterChargeGunChildBinding
import com.evport.businessapp.utils.loader.GlideMy.ImageLoader

/**
 * Create by KunMinX at 20/4/19
 */
class ChargeGunListChildAdapter(context: Context) :
    SimpleDataBindingAdapter<Sockets, AdapterChargeGunChildBinding>(
        context,
        R.layout.adapter_charge_gun_child,
        object : DiffUtil.ItemCallback<Sockets>() {
            override fun areItemsTheSame(
                oldItem: Sockets,
                newItem: Sockets
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Sockets,
                newItem: Sockets
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterChargeGunChildBinding,
        item: Sockets,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        item.socketTypeIcon()?.apply {
            binding.imgSocket.setImageResource(this)
        }
    }

    init {
        setOnItemClickListener(
            OnItemClickListener<Sockets> { item: Sockets?, position: Int -> }
        )
    }
}