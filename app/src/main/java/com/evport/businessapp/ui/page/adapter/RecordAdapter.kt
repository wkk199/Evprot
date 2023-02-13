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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.RecordResp
import com.evport.businessapp.databinding.AdapterRecordBinding

/**
 * Create by KunMinX at 20/4/19
 */
class RecordAdapter(context: Context?) :
    SimpleDataBindingAdapter<RecordResp, AdapterRecordBinding>(
        context,
        R.layout.adapter_record,
        object : DiffUtil.ItemCallback<RecordResp>() {
            override fun areItemsTheSame(
                oldItem: RecordResp,
                newItem: RecordResp
            ): Boolean {
                return oldItem.transactionPk == newItem.transactionPk
                //                return false;
            }

            override fun areContentsTheSame(
                oldItem: RecordResp,
                newItem: RecordResp
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterRecordBinding,
        item: RecordResp,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item

    }

}