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
import com.evport.businessapp.data.bean.NotiSys
import com.evport.businessapp.databinding.AdapterNotificationSysListBinding

/**
 * Create by KunMinX at 20/4/19
 */
class NotificationSysAdapter(context: Context) :
    SimpleDataBindingAdapter<NotiSys, AdapterNotificationSysListBinding>(
        context,
        R.layout.adapter_notification_sys_list,
        object : DiffUtil.ItemCallback<NotiSys>() {
            override fun areItemsTheSame(
                oldItem: NotiSys,
                newItem: NotiSys
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: NotiSys,
                newItem: NotiSys
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var contentClick :((item: NotiSys, position: Int)->Any)?=null

    protected override fun onBindItem(
        binding: AdapterNotificationSysListBinding,
        item: NotiSys,
        holder: RecyclerView.ViewHolder
    ) {

        binding.info = item
        if (holder.absoluteAdapterPosition ==currentList.size-1 ){
            binding.viewLiner.visibility =  View.GONE
        }
//        binding.content.setOnClickListener {
//            contentClick?.invoke(item,holder.bindingAdapterPosition)
//        }

    }

}