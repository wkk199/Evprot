package com.evport.businessapp.ui.page.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Connector
import com.evport.businessapp.databinding.AdapterSelectChargingGunBinding
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

class SelectChargingGunAdapter(context: Context?) :
    SimpleDataBindingAdapter<Connector, AdapterSelectChargingGunBinding>(
        context,
        R.layout.adapter_select_charging_gun,
        object : DiffUtil.ItemCallback<Connector>() {
            override fun areItemsTheSame(
                oldItem: Connector,
                newItem: Connector
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Connector,
                newItem: Connector
            ): Boolean {
                return oldItem == newItem
            }
        }) {
    protected override fun onBindItem(
        binding: AdapterSelectChargingGunBinding,
        item: Connector,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        /*     item.socket.toString().socketTypeIcon()?.apply {
                 binding.imgSocket.setImageResource(this)
             }*/

    }
}
