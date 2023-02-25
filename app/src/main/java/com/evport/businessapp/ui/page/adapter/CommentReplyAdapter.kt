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
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.ReplyDetail
import com.evport.businessapp.databinding.AdapterCommentReplyListBinding
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter


/**
 * Create by KunMinX at 20/4/19
 */
class CommentReplyAdapter(val context: Context) :
    SimpleDataBindingAdapter<ReplyDetail, AdapterCommentReplyListBinding>(
        context,
        R.layout.adapter_comment_reply_list,
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
    var delClick: ((item: ReplyDetail) -> Any)? = null

    protected override fun onBindItem(
        binding: AdapterCommentReplyListBinding,
        item: ReplyDetail,
        holder: RecyclerView.ViewHolder
    ) {
        val s = CommentReplyAdapter(mContext)
        binding.adapter = s
        binding.info = item
        binding.ivDelete.setOnClickListener {
            delClick?.invoke(item)
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

//        item.distance = mContext.getDistanceShow(item.distance)


//        stringInterceptionChangeRed(
//            binding.tvStreet,
//            item.replySourceUserName.toString(),
//            "Reply" +item.replySourceUserName + item.replyContent()
//        )


        val spannableString = SpannableString("Reply " +item.replySourceUserName + item.replyContent())
        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#00A0E9"))
        spannableString.setSpan(foregroundColorSpan, 6, item.replySourceUserName!!.length+6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.tvStreet.setMText(spannableString)


        binding.ivImage.setImageIsWifi(item.replyAvatarUrl)
    }

}