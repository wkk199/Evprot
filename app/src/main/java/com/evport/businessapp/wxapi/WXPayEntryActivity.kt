package com.evport.businessapp.wxapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.evport.businessapp.R
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.WXPayEntryViewModel
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.toast
import com.tencent.mm.opensdk.constants.ConstantsAPI


class WXPayEntryActivity : BaseActivity(), IWXAPIEventHandler {
    private lateinit var mViewModel: WXPayEntryViewModel
    private var api: IWXAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, Configs.APP_ID);
        api!!.handleIntent(intent, this);
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }

    override fun initViewModel() {
        mViewModel = getActivityViewModel(WXPayEntryViewModel::class.java)

    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_wxpay_entry, mViewModel)
    }

    override fun onReq(req: BaseReq?) {

    }

    override fun onResp(resp: BaseResp?) {
        val errCode: Int = resp!!.errCode
        Log.e("hm---weChat", Gson().toJson(resp))
        when (resp.type) {
            ConstantsAPI.COMMAND_PAY_BY_WX -> {

                when (resp.errCode) {
                    0 -> {
                        "充值成功".toast()
                        LiveBus.getInstance().post(EventBean(Configs.WX_CHAT_PAY, true, ""))
                    }
                    -1 -> {
                        "支付异常".toast()
                        LiveBus.getInstance().post(EventBean(Configs.WX_CHAT_PAY, false, ""))
                    }
                    -2 -> {
                        "支付取消".toast()
                        LiveBus.getInstance().post(EventBean(Configs.WX_CHAT_PAY, false, ""))
                    }
                }
                finish()
            }
        }
    }
}