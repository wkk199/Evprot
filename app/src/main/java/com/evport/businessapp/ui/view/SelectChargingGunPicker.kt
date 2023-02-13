package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Connector
import com.evport.businessapp.databinding.PopSelectChargingGunPickerBinding
import com.evport.businessapp.ui.page.adapter.SelectChargingGunAdapter
import com.lxj.xpopup.core.BottomPopupView

class SelectChargingGunPicker (context: Context,val connectors: List<Connector>? = null) : BottomPopupView(context) {
    private lateinit var bind: PopSelectChargingGunPickerBinding

    private var mOkBlock: (Connector) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_select_charging_gun_picker
    }
    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }
   fun  initView(){
       val s = SelectChargingGunAdapter(context).apply {
           setOnItemClickListener { item, position ->
          mOkBlock.invoke(item)
              // dismiss()
           }
       }
       bind.recyclerView.adapter = s
       s.submitList(connectors)
bind.close.setOnClickListener {
    dismiss()
}
    }

    fun setCallBack( okBlock: (Connector) -> Unit = {}) {
        mOkBlock = okBlock
    }
}