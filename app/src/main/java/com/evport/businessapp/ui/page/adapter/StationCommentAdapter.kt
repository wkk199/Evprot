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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Comment
import com.evport.businessapp.databinding.AdapterStationCommentListBinding
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi

/**
 * Create by KunMinX at 20/4/19
 */
class StationCommentAdapter(context: Context) :
    SimpleDataBindingAdapter<Comment, AdapterStationCommentListBinding>(
        context,
        R.layout.adapter_station_comment_list,
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
    var delClick: ((item: Comment) -> Any)? = null

    protected override fun onBindItem(
        binding: AdapterStationCommentListBinding,
        item: Comment,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.ivDelete.setOnClickListener {
            delClick?.invoke(item)
        }
        binding.ivDelete.isVisible =!item.hasDelFlag()&& mContext.getUser()?.userPk == item.appUserPk
//        val s = ChargeGunListChildAdapter(mContext)
//        binding.adapter = s
        item.ratingString()?.apply {

            binding.ratingBar.rating = if (this.isNotEmpty())item.ratingString().toFloat() else 0.0f
        }
//        item.distance = mContext.getDistanceShow(item.distance)

        binding.ivImage.setImageIsWifi(item.avatarUrl)
    }

    init {
//        setOnItemClickListener(OnItemClickListener<Comment> { item: Comment?, position: Int ->
//
//
//        })
    }
}