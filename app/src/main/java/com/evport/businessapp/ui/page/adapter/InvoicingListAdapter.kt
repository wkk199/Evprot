package com.evport.businessapp.ui.page.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.InvoicListBean
import com.evport.businessapp.databinding.AdapterInvoicingListBinding
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

class InvoicingListAdapter(context: Context) :
    SimpleDataBindingAdapter<InvoicListBean, AdapterInvoicingListBinding>(
        context,
        R.layout.adapter_invoicing_list,
        object : DiffUtil.ItemCallback<InvoicListBean>() {
            override fun areItemsTheSame(
                oldItem: InvoicListBean,
                newItem: InvoicListBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: InvoicListBean,
                newItem: InvoicListBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    var selClick: ((item: InvoicListBean?, position: Int) -> Any)? = null
    protected override fun onBindItem(
        binding: AdapterInvoicingListBinding,
        item: InvoicListBean,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.sel.setOnClickListener {
            selClick!!.invoke(item, holder.bindingAdapterPosition)
        }

    }
}