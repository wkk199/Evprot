package com.evport.businessapp.ui.page.activity

import android.os.Bundle
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.InvoicingResultViewModel
import com.evport.businessapp.utils.LiveBus

class InvoicingResultActivity : BaseActivity(){
    private var mViewModel: InvoicingResultViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoicing_result)
    }

    override fun initViewModel() {
        mViewModel = getActivityViewModel(InvoicingResultViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_invoicing_result, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    inner class ClickProxy {
        fun back() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_REFRESH, "", ""))
           // nav().navigateUp()
        }

        fun backToInvoicing() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_REFRESH, "", ""))
           // nav().navigateUp()
        }

        fun history() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_REFRESH, "", ""))
           // nav().navigate(R.id.action_global_billingHistoryFragment)
        }
    }

}