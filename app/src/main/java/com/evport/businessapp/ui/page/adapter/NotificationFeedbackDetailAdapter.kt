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
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Feedback
import com.evport.businessapp.data.bean.FeedbackType
import com.evport.businessapp.data.bean.FeedbackTypeMy
import com.evport.businessapp.data.bean.FeedbackTypeRB
import com.evport.businessapp.databinding.AdapterNotificationFeedbackDetailBinding
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi
import org.jetbrains.anko.textColor

/**
 * Create by KunMinX at 20/4/19
 */
class NotificationFeedbackDetailAdapter(context: Context) :
    SimpleDataBindingAdapter<Feedback, AdapterNotificationFeedbackDetailBinding>(
        context,
        R.layout.adapter_notification_feedback_detail,
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
    var recyClick: ((item: Feedback?, position: Int) -> Any)? = null


    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("ResourceAsColor", "WrongConstant")
    protected override fun onBindItem(
        binding: AdapterNotificationFeedbackDetailBinding,
        item: Feedback,
        holder: RecyclerView.ViewHolder
    ) {

        Log.e(
            "onBindItem",
            "${holder.absoluteAdapterPosition} ${Gson().toJson(item.imageList())} ${item.imageList().size}"
        )

        /*  var  adapter= ListGridImageAdapter(context = mContext, data = item.imgList).apply {
              setOnItemClickListener { items: String, positions: Int ->
                  recyClick?.invoke(item, positions)
              }
          }*/
        var adapter = GridImageAdapter(mContext, item.imageList());
        adapter.setOnCallBack {
            recyClick?.invoke(item, it)
        }
        val layoutManager =
            LinearLayoutManager(mContext)
//设置布局管理器
        //设置布局管理器
        binding.selleRecyclerView .setLayoutManager(layoutManager)
//设置为垂直布局，这也是默认的
        //设置为垂直布局，这也是默认的
        layoutManager.orientation = OrientationHelper.VERTICAL
        //  adapter.submitList(item.imageList())
        //binding.adapter=adapter
        binding.selleRecyclerView.adapter = adapter


        /*       if (TextUtils.isEmpty(item.imgDir)) {
                   binding.selleRecyclerView.visibility = View.GONE
               } else {
                   binding.selleRecyclerView.visibility = View.VISIBLE
               }*/
//        binding.selleRecyclerView.addItemDecoration(
//            SpaceItemDecoration(
//                10.todp(),
//                ContextCompat.getColor(mContext, android.R.color.transparent),
//                SpaceItemDecoration.VERTICAL_LIST,
//                0, 0,
//                SpaceItemDecoration.TYPE_ALL
//            )
//        )

        when (item.showType(mContext.getUser()?.userPk)) {

            FeedbackType -> {
                binding.tvName.textColor = mContext.resources.getColor(R.color.white)
                item.bgColor = mContext.resources.getColor(R.color.grey)

                binding.tvComments.isVisible = false
                binding.ivMy.visibility = View.INVISIBLE
                binding.ivRb.visibility = View.INVISIBLE
                binding.tvName.visibility = View.VISIBLE
                binding.tvName1.visibility = View.GONE
            }
            FeedbackTypeMy -> {
                binding.tvName1.textColor = mContext.resources.getColor(R.color.white)
                item.bgColor = mContext.resources.getColor(R.color.colorTheme)
                binding.tvComments.isVisible = true
                binding.ivMy.visibility = View.VISIBLE
                binding.ivRb.visibility = View.INVISIBLE
                binding.tvName.visibility = View.GONE
                // binding.tvName1.visibility=View.VISIBLE
                binding.ivMy.setImageIsWifi(mContext.getUser()?.avatarUrl)
                if (TextUtils.isEmpty(item.feedbackContent)) {
                    binding.tvName1.visibility = View.INVISIBLE
                } else {
                    binding.tvName1.visibility = View.VISIBLE
                }

            }
            FeedbackTypeRB -> {
                binding.tvName.textColor = mContext.resources.getColor(R.color.black)
                item.bgColor = mContext.resources.getColor(R.color.light_bg_color)
                binding.tvComments.isVisible = true
                binding.ivMy.visibility = View.INVISIBLE
                binding.ivRb.visibility = View.VISIBLE
                // binding.tvName.visibility=View.VISIBLE
                binding.tvName1.visibility = View.GONE
                if (TextUtils.isEmpty(item.feedbackContent)) {
                    binding.tvName.visibility = View.INVISIBLE
                } else {
                    binding.tvName.visibility = View.VISIBLE
                }
            }

        }
        binding.info = item

    }

}