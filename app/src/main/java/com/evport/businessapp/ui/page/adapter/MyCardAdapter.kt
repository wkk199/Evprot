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
import com.evport.businessapp.data.bean.MyCard
import com.evport.businessapp.databinding.AdapterMyCardListBinding

/**
 * Create by KunMinX at 20/4/19
 */
class MyCardAdapter(context: Context) :
    SimpleDataBindingAdapter<MyCard, AdapterMyCardListBinding>(
        context,
        R.layout.adapter_my_card_list,
        object : DiffUtil.ItemCallback<MyCard>() {
            override fun areItemsTheSame(
                oldItem: MyCard,
                newItem: MyCard
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MyCard,
                newItem: MyCard
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var delClick :((item: MyCard?, position: Int)->Any)?=null
    var contentClick :((item: MyCard?, position: Int)->Any)?=null

    protected override fun onBindItem(
        binding: AdapterMyCardListBinding,
        item: MyCard,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
//        binding.content.setOnClickListener {
//            contentClick?.invoke(item,holder.bindingAdapterPosition)
//        }

    }

}