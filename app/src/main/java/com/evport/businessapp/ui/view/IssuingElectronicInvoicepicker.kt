package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.CreateInvoiceVo
import com.evport.businessapp.databinding.PopIssuingElectronicInvoicePickerBinding
import com.lxj.xpopup.core.BottomPopupView

class IssuingElectronicInvoicepicker(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopIssuingElectronicInvoicePickerBinding
    private var mOkBlock: (String) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_issuing_electronic_invoice_picker
    }

    var datas = CreateInvoiceVo()
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    fun initView() {
        if (datas.invoiceType == 0) {
            bind.type.text = "个人"
        } else {
            bind.type.text = "企业"
        }
        bind.name.text = datas.name
        bind.email.text = datas.email
        bind.close.setOnClickListener {
            dismiss()
        }
        bind.next.setOnClickListener {
            mOkBlock.invoke("")
            dismiss()
        }
    }

    fun setCallBack(okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock
    }

    fun setData(datas: CreateInvoiceVo) {
        this.datas = datas
    }
}