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

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.SocketType
import com.evport.businessapp.databinding.AdapterListStringBinding

/**
 * Create by KunMinX at 20/4/19
 */
class ListStringAdapter(context: Context?) :
    SimpleDataBindingAdapter<SocketType, AdapterListStringBinding>(
        context,
        R.layout.adapter_list_string,
        object : DiffUtil.ItemCallback<SocketType>() {
            override fun areItemsTheSame(
                oldItem: SocketType,
                newItem: SocketType
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SocketType,
                newItem: SocketType
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var selectId = -1
    @SuppressLint("ResourceAsColor")
    protected override fun onBindItem(
        binding: AdapterListStringBinding,
        item: SocketType,
        holder: RecyclerView.ViewHolder
    ) {

        if (selectId == holder.absoluteAdapterPosition) {
            binding.tvId.setTextColor(mContext.resources.getColor(R.color.colorTheme))
            item.colorBgStatu = mContext.resources.getColor(R.color.colorTheme)
        } else {
            binding.tvId.setTextColor(mContext.resources.getColor(R.color.light_text_color))
            item.colorBgStatu = mContext.resources.getColor(R.color.light_text_color)
        }
        binding.info = item


    }

}