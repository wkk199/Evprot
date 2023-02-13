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
import com.evport.businessapp.data.bean.Feedback
import com.evport.businessapp.databinding.AdapterNotificationFeedbackListBinding

/**
 * Create by KunMinX at 20/4/19
 */
class NotificationFeedbackAdapter(context: Context) :
    SimpleDataBindingAdapter<Feedback, AdapterNotificationFeedbackListBinding>(
        context,
        R.layout.adapter_notification_feedback_list,
        object : DiffUtil.ItemCallback<Feedback>() {
            override fun areItemsTheSame(
                oldItem: Feedback,
                newItem: Feedback
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Feedback,
                newItem: Feedback
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var delClick :((item: Feedback?, position: Int)->Any)?=null
    var contentClick :((item: Feedback?, position: Int)->Any)?=null
    var recyClick :((item: Feedback?, position: Int)->Any)?=null

    protected override fun onBindItem(
        binding: AdapterNotificationFeedbackListBinding,
        item: Feedback,
        holder: RecyclerView.ViewHolder
    ) {

        val adapter =  ListStringImageAdapter(context = mContext).apply {
            setOnItemClickListener { items, positions ->
                recyClick?.invoke(item,positions)
            }
        }
//        binding.selleRecyclerView.setOnClickListener {
//
//        }
//        (
//            SpaceItemDecoration(
//                10.todp(),
//                ContextCompat.getColor(mContext, android.R.color.transparent),
//                SpaceItemDecoration.VERTICAL_LIST,
//                0, 0,
//                SpaceItemDecoration.TYPE_ALL
//            )
//        )

        binding.adapter =adapter
//        if (list.size>0) {
//            binding.selleRecyclerView.isVisible =true
//        }
//        else
//            binding.selleRecyclerView.isVisible =false
        binding.info = item
        binding.btnDelete.setOnClickListener {
            delClick?.invoke(item,holder.bindingAdapterPosition)
        }
        binding.content.setOnClickListener {
            contentClick?.invoke(item,holder.bindingAdapterPosition)
        }

    }

}