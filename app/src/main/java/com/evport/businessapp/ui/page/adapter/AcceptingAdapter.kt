package com.evport.businessapp.ui.page.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.InvoiceHistoryBean
import com.evport.businessapp.databinding.AdapterAcceptinglistBinding
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

class AcceptingAdapter(context: Context) :
    SimpleDataBindingAdapter<InvoiceHistoryBean, AdapterAcceptinglistBinding>(
        context,
        R.layout.adapter_acceptinglist,
        object : DiffUtil.ItemCallback<InvoiceHistoryBean>() {
            override fun areItemsTheSame(
                oldItem: InvoiceHistoryBean,
                newItem: InvoiceHistoryBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: InvoiceHistoryBean,
                newItem: InvoiceHistoryBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    var moreClick :((item: InvoiceHistoryBean?, position: Int)->Any)?=null
    protected override fun onBindItem(
        binding: AdapterAcceptinglistBinding,
        item: InvoiceHistoryBean,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.more.setOnClickListener {
            moreClick!!.invoke(item,holder.bindingAdapterPosition)
        }


    }
}