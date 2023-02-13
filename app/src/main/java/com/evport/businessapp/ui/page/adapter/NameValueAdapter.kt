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
import com.evport.businessapp.data.bean.NameValue
import com.evport.businessapp.databinding.AdapterNameValueBinding

/**
 * Create by KunMinX at 20/4/19
 */
class NameValueAdapter(context: Context?) :
    SimpleDataBindingAdapter<NameValue, AdapterNameValueBinding>(
        context,
        R.layout.adapter_name_value,
        object : DiffUtil.ItemCallback<NameValue>() {
            override fun areItemsTheSame(
                oldItem: NameValue,
                newItem: NameValue
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NameValue,
                newItem: NameValue
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    @SuppressLint("ResourceAsColor")
    protected override fun onBindItem(
        binding: AdapterNameValueBinding,
        item: NameValue,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item


    }

}