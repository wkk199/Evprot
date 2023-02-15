package com.evport.businessapp.ui.page

import android.util.Log
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.NameVo
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.MyWalletViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_my_wallet.*
import kotlinx.coroutines.*

class MyWalletFragment : BaseFragment() {
    private var mViewModel: MyWalletViewModel? = null
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(MyWalletViewModel::class.java)
    }

    var banlanceCunt = 0.00
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_my_wallet, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }


    override fun loadInitData() {
        super.loadInitData()
        initView()
        showLoading()
        getAccountBalance()
    }

    fun initView() {
        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == Configs.REFRESH_BALANCE) {
                if (it.value as Boolean) {
                    showLoading()
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(3000)
                        withContext(Dispatchers.Main) {
                            getAccountBalance()
                        }
                    }
                } else {
                    showLoading()
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(500)
                        withContext(Dispatchers.Main) {
                            getAccountBalance()
                        }
                    }
                }


            }
        })
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun myCard() {
            nav().navigate(R.id.action_global_myCardListFragment)
        }

        fun recharge() {
            nav().navigate(R.id.action_global_accountRechargeFragment)
        }

        fun withdrawal() {
           // nav().navigate(R.id.action_global_withdrawalFragment)
            if (banlanceCunt > 0) {
                nav().navigate(R.id.action_global_withdrawalFragment)
            } else {
                ToastUtils.showShort("可提现金额为0")
            }

        }
    }

    private fun getAccountBalance() {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                Log.e("hm----data", data.toString())
                GlobalScope.launch(Dispatchers.IO) {
                    delay(500)
                    withContext(Dispatchers.Main) {
                        dismissLoading()
                    }
                }

                banlance.text = data
                banlanceCunt = data!!.toDouble()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getAccountBalance(NameVo(name = "aa"))
            }
        }
    }
}