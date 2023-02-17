package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Strategy
import com.evport.businessapp.databinding.PopTimePickerBinding
import com.evport.businessapp.ui.page.adapter.PopTimePickerAdapter
import com.lxj.xpopup.core.BottomPopupView


class PopTimePicker(context: Context, var strategy: List<Strategy>) : BottomPopupView(context) {
    private lateinit var bind: PopTimePickerBinding

    override fun getImplLayoutId(): Int {
        return R.layout.pop_time_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {

        bind.tvCancel.setOnClickListener {
            dismiss()
        }

        val layoutManager1 = LinearLayoutManager(context)
        bind.rlTime .layoutManager = layoutManager1
        bind.rlTime .adapter = PopTimePickerAdapter(context,strategy)
        bind.rlTime.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL) )



    }

    override fun beforeShow() {
        super.beforeShow()

    }
    fun setCallBack(currYear: String, currMonth: String, okBlock: (String) -> Unit = {}) {


    }
}