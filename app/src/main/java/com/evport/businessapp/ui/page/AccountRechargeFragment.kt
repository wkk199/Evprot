package com.evport.businessapp.ui.page

import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.alipay.sdk.app.AuthTask
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.AccountRechargeViewModel
import com.evport.businessapp.utils.LiveBus
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_account_recharge.*
import kotlinx.coroutines.*
import com.evport.businessapp.utils.MoneyValueFilter
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.mm.opensdk.modelpay.PayReq


class AccountRechargeFragment : BaseFragment() {

    private var mViewModel: AccountRechargeViewModel? = null
    private var api: IWXAPI? = null


    override fun initViewModel() {
        mViewModel = getFragmentViewModel(AccountRechargeViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.fragment_account_recharge, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//沙箱环境需要的代码
        super.onCreate(savedInstanceState)
        regToWx()

    }

    private fun regToWx() {
        api = WXAPIFactory.createWXAPI(activity, Configs.APP_ID, true)
        api!!.registerApp(Configs.APP_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        input_money.setFilters(arrayOf<InputFilter>(MoneyValueFilter().setDigits(2)))
        input_money.doOnTextChanged { text, start, before, count ->

            if (!input_money.text.toString().isNullOrBlank()) {
                if (input_money.text.toString().toDouble() < 0.01 || input_money.text.toString()
                        .toDouble() > 1000
                ) {
                    money_hint.visibility = View.VISIBLE
                } else {
                    money_hint.visibility = View.INVISIBLE
                }
            } else {
                money_hint.visibility = View.INVISIBLE
            }
        }

        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == Configs.WX_CHAT_PAY) {
                dismissLoading()
                if (it.value as Boolean){
                    input_money.setText("")
                    LiveBus.getInstance().post(EventBean(Configs.REFRESH_BALANCE, true, ""))
                    nav().navigateUp()
                }

            }
        })
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun selWeChat() {
            mViewModel!!.isWeChat.set(true)
            closeKeyWord()
        }

        fun selAlipay() {
            mViewModel!!.isWeChat.set(false)
            closeKeyWord()
        }

        fun confrim() {
            var amount = input_money.text.toString()
            if (amount.isNullOrBlank()) {
                ToastUtils.showShort("请输入充值金额")
                return
            }
            if (amount.toDouble() < 0.01 || amount.toDouble() > 1000) {
                ToastUtils.showShort("请输入正确的充值金额")
                return

            }
            if (mViewModel!!.isWeChat.get()) {
                // ToastUtils.showShort("微信支付暂未开放")
                wxRecharge(amount)
            } else {
                recharge(amount)

            }

        }

    }

    fun recharge(amount: String) {
        showLoading()
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {

                alipay(data.toString())
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showShort(message)
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.recharge(
                    RechargeReq(amount = amount)
                )
            }
        }
    }

    fun wxRecharge(amount: String) {
        showLoading()
        object : NetworkBoundResource<WxPayBean>(networkStatusCallback = object :
            NetworkStatusCallback<WxPayBean> {

            override fun onSuccess(data: WxPayBean?) {

                // alipay(data.toString())
                doWXPay(data!!)
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<WxPayBean>> {
                return SingletonFactory.apiService.wxRecharge(
                    RechargeReq(amount = amount)
                )
            }
        }
    }

    fun alipay(authInfo: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val authTask = AuthTask(activity)
            // 调用授权接口，获取授权结果
            val result = authTask.authV2(authInfo, true)
            withContext(Dispatchers.Main) {
                val payResult = PayResult(result as Map<String?, String?>)

                val resultInfo: String = payResult.result
                val resultStatus: String = payResult.resultStatus
                dismissLoading()
                delay(100)
                if (TextUtils.equals(resultStatus, "9000")) {
                    ToastUtils.showShort("支付成功")
                    input_money.setText("")
                    LiveBus.getInstance().post(EventBean(Configs.REFRESH_BALANCE, true, ""))
                    nav().navigateUp()
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    ToastUtils.showShort("支付失败")
                }
            }

        }


    }

    private fun doWXPay(data: WxPayBean) {
        val request = PayReq()
        request.appId = data.appId //应用ID
        request.partnerId = data.partnerId //商户号
        request.prepayId = data.prepayId //预支付交易会话ID
        request.packageValue = data.packages //扩展字段 暂填写固定值Sign=WXPay
        request.nonceStr = data.nonceStr //随机字符串
        request.timeStamp = data.timestamp //时间戳
        request.sign = data.sign //签名
        api!!.sendReq(request)
    }
}