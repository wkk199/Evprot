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
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.BaseDataBindingAdapter.OnItemClickListener
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.SocketType
import com.evport.businessapp.databinding.AdapterFilterNameBinding
import com.evport.businessapp.utils.socketTypeIcon
import com.evport.businessapp.utils.socketTypeIconFalse
import java.util.*

/**
 * Create by KunMinX at 20/4/19
 */
class FilterHomeListAdapter(context: Context) :
    SimpleDataBindingAdapter<SocketType, AdapterFilterNameBinding>(
        context,
        R.layout.adapter_filter_name,
        object : DiffUtil.ItemCallback<SocketType>() {
            override fun areItemsTheSame(
                oldItem: SocketType,
                newItem: SocketType
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SocketType,
                newItem: SocketType
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    private var selectItems: ArrayList<SocketType>? = ArrayList()
    var selectItem: ArrayList<SocketType>?
        get() = selectItems
        set(selectItem) {
            selectItems = selectItem
            notifyDataSetChanged()
        }

    fun clickSelectItem(selectItem: SocketType) {
        if (selectItems != null) {
            if (selectItems!!.contains(selectItem)) selectItems!!.remove(selectItem) else selectItems!!.add(
                selectItem
            )
        } else {
            selectItems = ArrayList()
            selectItems!!.add(selectItem)
        }
    }

    @SuppressLint("ResourceAsColor")
    protected override fun onBindItem(
        binding: AdapterFilterNameBinding,
        item: SocketType,
        holder: RecyclerView.ViewHolder
    ) {
        var s = false
        selectItems?.map {
            if (it.operatorPk == item.operatorPk) {
                s = true
                return@map
            }
        }

        if (item.name.socketTypeIconFalse() == null) {
            binding.imgSocket.visibility = View.GONE
        } else {
            binding.imgSocket.visibility = View.VISIBLE
        }
        if (s) {
            binding.tvId.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            item.colorBgStatu = mContext.resources.getColor(R.color.color_EBF8FE)
            item.name.socketTypeIcon()?.apply {
                binding.imgSocket.setImageResource(this)
            }
        } else {
            binding.tvId.setTextColor(mContext.resources.getColor(R.color.color_8F9293))
            item.colorBgStatu = mContext.resources.getColor(R.color.light_green)
            item.name.socketTypeIconFalse()?.apply {
                binding.imgSocket.setImageResource(this)
            }
        }



        binding.info = item
    }

    init {
        setOnItemClickListener(OnItemClickListener<SocketType> { item: SocketType, position: Int ->
            clickSelectItem(item)
            notifyDataSetChanged()
        })
    }
}