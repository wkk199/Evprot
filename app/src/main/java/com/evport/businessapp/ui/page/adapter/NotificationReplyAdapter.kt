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
import com.evport.businessapp.data.bean.ReplyDetail
import com.evport.businessapp.databinding.AdapterNotificationReplyListBinding
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi

/**
 * Create by KunMinX at 20/4/19
 */
class NotificationReplyAdapter(context: Context) :
    SimpleDataBindingAdapter<ReplyDetail, AdapterNotificationReplyListBinding>(
        context,
        R.layout.adapter_notification_reply_list,
        object : DiffUtil.ItemCallback<ReplyDetail>() {
            override fun areItemsTheSame(
                oldItem: ReplyDetail,
                newItem: ReplyDetail
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ReplyDetail,
                newItem: ReplyDetail
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var delClick :((item: ReplyDetail?, position: Int)->Any)?=null
    var contentClick :((item: ReplyDetail?, position: Int)->Any)?=null

    protected override fun onBindItem(
        binding: AdapterNotificationReplyListBinding,
        item: ReplyDetail,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.btnDelete.setOnClickListener {
            delClick?.invoke(item,holder.bindingAdapterPosition)
        }
        binding.content.setOnClickListener {
            contentClick?.invoke(item,holder.bindingAdapterPosition)
        }
        if (!item.hasDelFlag()) {
            binding.tvStreet.setTextColor(mContext.getColor(R.color.light_text_color_b))
            if (mContext.getUser()?.userPk == item.replyUserPk) {
                binding.btnDelete.visibility = View.VISIBLE
            } else {
                binding.btnDelete.visibility = View.GONE
            }
        } else {
            binding.tvStreet.setTextColor(mContext.getColor(R.color.light_text_color))
            binding.btnDelete.visibility = View.GONE
        }
        binding.ivImage.setImageIsWifi(item.replyAvatarUrl)
        binding.ivStation.setImageIsWifi(item.stationAvatarUrl)
//        binding.ivImage.setImageURI(item.replyAvatarUrl)

    }

}