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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.FamilyMemberBean
import com.evport.businessapp.databinding.AdapterHomeMemberListStringBinding
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi

/**
 * Create by KunMinX at 20/4/19
 */
class HomeMemberListStringAdapter(context: Context?) :
    SimpleDataBindingAdapter<FamilyMemberBean, AdapterHomeMemberListStringBinding>(
        context,
        R.layout.adapter_home_member_list_string,
        object : DiffUtil.ItemCallback<FamilyMemberBean>() {
            override fun areItemsTheSame(
                oldItem: FamilyMemberBean,
                newItem: FamilyMemberBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: FamilyMemberBean,
                newItem: FamilyMemberBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var delClick:((item : FamilyMemberBean)->Any)?=null
    var canDel = true
    @SuppressLint("ResourceAsColor")
    protected override fun onBindItem(
        binding: AdapterHomeMemberListStringBinding,
        item: FamilyMemberBean,
        holder: RecyclerView.ViewHolder
    ) {

//        if (selectId == holder.absoluteAdapterPosition) {
//            binding.tvId.setTextColor(mContext.resources.getColor(R.color.colorTheme))
//            item.colorBgStatu = mContext.resources.getColor(R.color.colorTheme)
//        } else {
//            binding.tvId.setTextColor(mContext.resources.getColor(R.color.light_text_color))
//            item.colorBgStatu = mContext.resources.getColor(R.color.light_text_color)
//        }
//        binding.info = item

        binding.tvId.text = item.userName
        binding.avatar.setImageIsWifi(item.avatarUrl)
        binding.img.isVisible = item.userPk!= mContext.getUser()?.userPk&&canDel
        binding.img.setOnClickListener {
            delClick?.invoke(item)
        }
    }

}