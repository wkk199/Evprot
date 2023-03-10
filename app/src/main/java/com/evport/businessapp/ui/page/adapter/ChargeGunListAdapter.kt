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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.StationListBean
import com.evport.businessapp.databinding.AdapterChargeGunListBinding
import com.evport.businessapp.utils.getDistanceShow
import com.evport.businessapp.utils.jumpMap
import com.evport.businessapp.utils.setImageIsWifi
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

/**
 * Create by KunMinX at 20/4/19
 */
class ChargeGunListAdapter(context: Context) :
    SimpleDataBindingAdapter<StationListBean, AdapterChargeGunListBinding>(
        context,
        R.layout.adapter_charge_gun_list,
        object : DiffUtil.ItemCallback<StationListBean>() {
            override fun areItemsTheSame(
                oldItem: StationListBean,
                newItem: StationListBean
            ): Boolean {
                return oldItem.stationPk == newItem.stationPk
            }

            override fun areContentsTheSame(
                oldItem: StationListBean,
                newItem: StationListBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterChargeGunListBinding,
        item: StationListBean,
        holder: RecyclerView.ViewHolder
    ) {
        val s = ChargeGunListChildAdapter(mContext)
        binding.adapter = s
        item.rating?.apply {
            binding.ratingBar.rating = if (this.isNotEmpty())item.ratingString().toFloat() else 0.0f
        }
        binding.selleRecyclerView.adapter = s
        s.submitList(item.sockets)
        item.distance = mContext.getDistanceShow(item.distance)
        binding.info = item
        binding.ivImage.setImageIsWifi(item.stationAvatarUrl)
        binding.ivNav.setOnClickListener {
            mContext.jumpMap(item)
        }
    }

    init {
//        setOnItemClickListener(OnItemClickListener<GunListBean> { item: GunListBean?, position: Int ->
//
//
//        })
    }


}