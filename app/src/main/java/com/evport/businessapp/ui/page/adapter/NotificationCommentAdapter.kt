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
import com.evport.businessapp.data.bean.Comment
import com.evport.businessapp.databinding.AdapterNotificationCommentListBinding
import com.evport.businessapp.utils.setImageIsWifi

/**
 * Create by KunMinX at 20/4/19
 */
class NotificationCommentAdapter(context: Context) :
    SimpleDataBindingAdapter<Comment, AdapterNotificationCommentListBinding>(
        context,
        R.layout.adapter_notification_comment_list,
        object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(
                oldItem: Comment,
                newItem: Comment
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Comment,
                newItem: Comment
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var delClick :((item: Comment?, position: Int)->Any)?=null
    var contentClick :((item: Comment?, position: Int)->Any)?=null

    protected override fun onBindItem(
        binding: AdapterNotificationCommentListBinding,
        item: Comment,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        item.rating?.apply {

            binding.ratingBar.rating = if (this.isNotEmpty())this.toFloat() else 0.0f
        }
        if (!item.hasDelFlag()) {
            binding.tvContent.setTextColor(mContext.getColor(R.color.color_8f))
//            if (mContext.getUser()?.userPk == item.replyUserPk) {
                binding.btnDelete.visibility = View.VISIBLE
//            } else {
//                binding.btnDelete.visibility = View.GONE
//            }
        } else {
            binding.tvContent.setTextColor(mContext.getColor(R.color.color_8f))
            binding.btnDelete.visibility = View.GONE
        }
        binding.btnDelete.setOnClickListener {
            delClick?.invoke(item,holder.bindingAdapterPosition)
        }
        binding.content.setOnClickListener {
            contentClick?.invoke(item,holder.bindingAdapterPosition)
        }
        binding.ivImage.setImageIsWifi(item.stationAvatarUrl)

    }

}