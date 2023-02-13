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
import com.evport.businessapp.data.bean.ReplyDetail
import com.evport.businessapp.databinding.AdapterStationCommentReplyListBinding
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi

/**
 * Create by KunMinX at 20/4/19
 */
class StationCommentReplyAdapter(context: Context) :
    SimpleDataBindingAdapter<Comment, AdapterStationCommentReplyListBinding>(
        context,
        R.layout.adapter_station_comment_reply_list,
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
    var viewAllClick: ((item: Comment) -> Any)? = null
    var delClick: ((item: Comment) -> Any)? = null
    var delClickRep: ((item: ReplyDetail) -> Any)? = null
    var itemClick: ((name: String, pk: String, type: String) -> Any)? = null

    protected override fun onBindItem(
        binding: AdapterStationCommentReplyListBinding,
        item: Comment,
        holder: RecyclerView.ViewHolder
    ) {
        val s = CommentReplyAdapter(mContext).apply {
            setOnItemClickListener { item, position ->
                itemClick?.invoke(item?.replyName.toString(), item?.commentsReplyPk.toString(), "reply")
            }
            delClick = {
                delClickRep?.invoke(it)
                val b = it.hasDelFlag()
                it.delFlag = b.toString()
                notifyDataSetChanged()
            }
        }
        binding.listComment.adapter = s
        s.submitList(item.replyComment)

        binding.adapter = s
        binding.info = item
//        if (item.replyComment?.size!! >= 3) {
//            binding.tvViewComments.visibility = View.VISIBLE
//        } else {
//            binding.tvViewComments.visibility = View.GONE
//
//        }
        binding.tvViewComments.setOnClickListener {
            viewAllClick?.invoke(item)
        }
        if (!item.hasDelFlag()) {
            if (mContext.getUser()?.userPk == item.replyUserPk) {
                binding.ivDelete.visibility = View.VISIBLE
            } else {
                binding.ivDelete.visibility = View.GONE
            }
        } else {
            binding.ivDelete.visibility = View.GONE
        }
        binding.ivDelete.setOnClickListener {
            delClick?.invoke(item)
        }

//        item.distance = mContext.getDistanceShow(item.distance)

        binding.ivImage.setImageIsWifi(if(item.avatarUrl.isNullOrBlank()) item.replyAvatarUrl else item.avatarUrl )
    }

    init {
//        setOnItemClickListener(OnItemClickListener<Comment> { item: Comment?, position: Int ->
//            itemClick?.invoke(item?.replyName.toString(), item?.commentsPk.toString(), "comment")
//
//        })
    }
}