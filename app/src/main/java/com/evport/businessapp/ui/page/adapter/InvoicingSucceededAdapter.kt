package com.evport.businessapp.ui.page.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.InvoiceHistoryBean
import com.evport.businessapp.databinding.AdapterInvoicingSucceededBinding
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

class InvoicingSucceededAdapter (context: Context) :
    SimpleDataBindingAdapter<InvoiceHistoryBean, AdapterInvoicingSucceededBinding>(
        context,
        R.layout.adapter_invoicing_succeeded,
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
        binding: AdapterInvoicingSucceededBinding,
        item: InvoiceHistoryBean,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.more.setOnClickListener {
            moreClick!!.invoke(item,holder.bindingAdapterPosition)
        }


    }
}