package com.evport.businessapp.ui.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.CompanyBean
import com.evport.businessapp.databinding.PopCompanyPickerBinding
import com.evport.businessapp.ui.page.adapter.CompanyAdapter
import com.lxj.xpopup.core.AttachPopupView

class CompanyPicker @JvmOverloads constructor(context: Context) : AttachPopupView(context) {
    private lateinit var bind: PopCompanyPickerBinding
    override fun getImplLayoutId() = R.layout.pop_company_picker
    var mAdapter = CompanyAdapter(context).apply {

    }

    @SuppressLint("ResourceType")
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    fun initView() {
        bind.apply {
            rvCompany.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    fun setData(companyBeans: ArrayList<CompanyBean>) {
        mAdapter.submitList(companyBeans)

    }
}