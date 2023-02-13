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
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.databinding.AdapterImgGridBinding

/**
 * Create by KunMinX at 20/4/19
 */
class ListGridImageAdapter( var  context: Context? ,val data:List<String>) :
    SimpleDataBindingAdapter<String, AdapterImgGridBinding>(
        context,
        R.layout.adapter_img_grid,
        object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    @SuppressLint("ResourceAsColor")
    protected override fun onBindItem(
        binding: AdapterImgGridBinding,
        item: String,
        holder: RecyclerView.ViewHolder
    ) {
        Log.e("hm----item",item)//这里没有那条数据是吧
        Log.e("hm----item","${holder.bindingAdapterPosition}   $item")
        context?.let { Glide.with(it).load(item).into(binding.imgUri) }

       binding.infos = item
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}