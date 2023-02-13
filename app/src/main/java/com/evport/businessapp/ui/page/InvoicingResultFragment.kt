package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.InvoicingResultViewModel
import com.evport.businessapp.utils.LiveBus

class InvoicingResultFragment : BaseFragment() {
    private var mViewModel: InvoicingResultViewModel? = null
    val type by lazy {
        arguments?.getInt("type", 0)
    }

    override fun initViewModel() {
        mViewModel = getFragmentViewModel(InvoicingResultViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_invoicing_result, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }


    inner class ClickProxy {
        fun back() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_REFRESH, "", ""))
            nav().navigateUp()
        }

        fun backToInvoicing() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_REFRESH, "", ""))
            nav().navigateUp()
        }

        fun history() {

            nav().navigate(R.id.action_global_billingHistoryFragment)
        }
    }
    //定义回调
    var callback = object: OnBackPressedCallback(
        true // default to enabled
    ) {
        override fun handleOnBackPressed() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_REFRESH, "", ""))
            nav().navigateUp()
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
        //获取Activity的返回键分发器添加回调
        requireActivity().onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback)
    }

    fun initView() {
        mViewModel!!.type.value = type
        if (type == 0) {
            mViewModel!!.typeHint.value = "提交成功"
            mViewModel!!.typeHint1.value = "可前往开票历史查看开票进度"
        } else {
            mViewModel!!.typeHint.value = "提交失败"
            mViewModel!!.typeHint1.value = "请尝试重新提交开具发票"
        }

    }
    private fun initObserve() {
        LiveBus.getInstance().observeEvent(this, Observer {
          if (it.type==Configs.INVOICUNG_HISTORY) {
                nav().navigateUp()
            }
        })
    }
}