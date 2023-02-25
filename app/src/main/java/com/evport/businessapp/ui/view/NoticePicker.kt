package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopNoticeBinding
import com.lxj.xpopup.core.CenterPopupView

class NoticePicker(
    context: Context,
) : CenterPopupView(context) {

    private var mconfirmBlock: () -> Unit = {}
    private lateinit var bind: PopNoticeBinding
    override fun getImplLayoutId() = R.layout.pop_notice
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        bind.no.setOnClickListener {
            dismiss()
        }
        bind.yes.setOnClickListener {
            dismiss()
            mconfirmBlock.invoke()
        }
    }

    fun setCallBack(confirmBlock: () -> Unit = {}) {
        this.mconfirmBlock = confirmBlock
    }
}