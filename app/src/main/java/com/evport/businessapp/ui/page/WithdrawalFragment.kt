package com.evport.businessapp.ui.page

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.bean.NameVo
import com.evport.businessapp.data.bean.RechargeReq
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.WithdrawalViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.MoneyValueFilter
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_withdrawal.*
import kotlinx.android.synthetic.main.fragment_withdrawal.input_money

class WithdrawalFragment : BaseFragment() {
    private var mViewModel: WithdrawalViewModel? = null
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(WithdrawalViewModel::class.java)
    }

    var balance = "0.00";
    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.fragment_withdrawal, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAccountBalance()
        initView()
    }

    fun initView() {
        input_money.setFilters(arrayOf<InputFilter>(MoneyValueFilter().setDigits(2)))
        input_money.doOnTextChanged { text, start, before, count ->
            if (!input_money.text.toString().isNullOrBlank()) {
                confirm_tv.isEnabled =
                    !(input_money.text.toString().toDouble() < 0.01 || input_money.text.toString()
                        .toDouble() > balance.toDouble())
                if (input_money.text.toString().toDouble() > balance.toDouble()) {
                    mViewModel!!.isShowErro.set(true)
                } else {
                    mViewModel!!.isShowErro.set(false)
                }
            } else {
                confirm_tv.isEnabled = false
                mViewModel!!.isShowErro.set(false)
            }
        }

    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun all() {
            input_money.setText(balance)
            val index: Int = input_money.text.toString().length
            input_money.setSelection(index)

        }

        fun confrim() {
            var amount = input_money.text.toString()
            if (amount.isNullOrBlank()) {
                ToastUtils.showShort("请输入退款金额")
                return
            }
            if (amount.toDouble() < 0.01 || amount.toDouble() > balance.toDouble()) {
                ToastUtils.showShort("请输入正确的退款金额")
                return
            }
            refund(amount)
        }
    }

    private fun getAccountBalance() {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                Log.e("hm----data", data.toString())

                banlance_tv.text = "可提现余额" + data + "元"
                balance = data.toString()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getRefundableAmount(NameVo(random = "1"))
            }
        }
    }

    private fun refund(amount: String) {
        showLoading()

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                dismissLoading()
                Log.e("hm----data", data.toString())
                ToastUtils.showShort("提现申请已提交, 等待平台处理")
                input_money.setText("")
                LiveBus.getInstance().post(EventBean(Configs.REFRESH_BALANCE, false, ""))
                nav().navigateUp()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.refund(RechargeReq(amount = amount))
            }
        }
    }

}