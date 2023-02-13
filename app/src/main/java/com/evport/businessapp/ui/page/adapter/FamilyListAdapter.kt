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
import com.evport.businessapp.data.bean.FamilyList
import com.evport.businessapp.databinding.AdapterFamilyListBinding

/**
 * Create by KunMinX at 20/4/19
 */
class FamilyListAdapter(context: Context?) :
    SimpleDataBindingAdapter<FamilyList, AdapterFamilyListBinding>(
        context,
        R.layout.adapter_family_list,
        object : DiffUtil.ItemCallback<FamilyList>() {
            override fun areItemsTheSame(
                oldItem: FamilyList,
                newItem: FamilyList
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: FamilyList,
                newItem: FamilyList
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    var selectItem : FamilyList?=null

    @SuppressLint("ResourceAsColor")
    protected override fun onBindItem(
        binding: AdapterFamilyListBinding,
        item: FamilyList,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item

        when (item.homePk){
            "-1"->{
                binding.img.setImageResource(R.drawable.icon_f_add)
                binding.img.isVisible = true
            }
            "-2"->{
                binding.img.setImageResource(R.drawable.icon_f_setting)
                binding.img.isVisible = true
            }
            else->{
                binding.img.setImageResource(R.drawable.icon_f_select)
                binding.img.isVisible = false
                selectItem?.apply {
                    binding.img.isVisible = this.homePk == item.homePk
                }
            }
        }


    }

}