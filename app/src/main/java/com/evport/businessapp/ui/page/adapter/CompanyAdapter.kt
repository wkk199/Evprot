package com.evport.businessapp.ui.page.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.CompanyBean
import com.evport.businessapp.databinding.AdapterCompanyBinding
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter

class CompanyAdapter(context: Context) :
    SimpleDataBindingAdapter<CompanyBean, AdapterCompanyBinding>(
        context,
        R.layout.adapter_company,
        object : DiffUtil.ItemCallback<CompanyBean>() {
            override fun areItemsTheSame(
                oldItem: CompanyBean,
                newItem: CompanyBean
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CompanyBean,
                newItem: CompanyBean
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    var selClick: ((item: CompanyBean?, position: Int) -> Any)? = null
    protected override fun onBindItem(
        binding: AdapterCompanyBinding,
        item: CompanyBean,
        holder: RecyclerView.ViewHolder
    ) {
        binding.info = item
        binding.item.setOnClickListener {
            selClick!!.invoke(item, holder.bindingAdapterPosition)
        }

    }
}