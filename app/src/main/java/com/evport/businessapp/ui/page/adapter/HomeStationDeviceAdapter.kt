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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.HomeDeviceBean
import com.evport.businessapp.data.bean.HomeStationBean
import com.evport.businessapp.databinding.AdapterHomeStationDeviceListBinding

/**
 * Create by KunMinX at 20/4/19
 */
class HomeStationDeviceAdapter(context: Context) :
    SimpleDataBindingAdapter<HomeStationBean, AdapterHomeStationDeviceListBinding>(
        context,
        R.layout.adapter_home_station_device_list,
        object : DiffUtil.ItemCallback<HomeStationBean>() {
            override fun areItemsTheSame(
                oldItem: HomeStationBean,
                newItem: HomeStationBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HomeStationBean,
                newItem: HomeStationBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    var likeClick: ((item: HomeStationBean, position:Int) -> Any)? = null
    var isExpand = true//默认展开
    var delClick :((item: HomeStationBean?, position: Int)->Any)?=null
    var childClick: ((item: HomeDeviceBean) -> Any)? = null
    var childUpdateClick: ((item: HomeDeviceBean, isStart:Boolean) -> Any)? = null
    var childDelClick: ((item: HomeDeviceBean, isStart:Boolean) -> Any)? = null
    override fun onBindItem(
        binding: AdapterHomeStationDeviceListBinding,
        item: HomeStationBean,
        holder: RecyclerView.ViewHolder
    ) {
        val s = HomeStationDeviceChildAdapter(mContext).apply {
            startClick = {
                childClick?.invoke(it)!!
            }
            updateClick = {it, start ->
                childUpdateClick?.invoke(it,start)!!
            }
            deleteClick = {
                it, start ->
                childDelClick?.invoke(it,start)!!
            }
        }
        binding.adapter = s
        binding.info = item
        binding.selleRecyclerView.adapter = s
        s.submitList(item.connectors)
        if (isExpand) {
            binding.ivLike.setImageResource(R.drawable.icon_f_expand)
            binding.selleRecyclerView.isVisible = true
        } else {
            binding.ivLike.setImageResource(R.drawable.icon_f_add_circle)
            binding.selleRecyclerView.isVisible = false
        }
        when(item.status){
            "0"->{
                binding.tvStatus.text = "setting failed"
            }
            "1"->{

            }
            "2"->{

                binding.tvStatus.text = "Waiting for access to home load power"
            }
        }

        binding.ivLike.setOnClickListener {
            likeClick?.invoke(item,holder.bindingAdapterPosition)
        }

        binding.btnDelete.setOnClickListener {
            delClick?.invoke(item,holder.bindingAdapterPosition)
        }
    }

}