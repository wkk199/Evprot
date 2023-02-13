/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evport.businessapp.ui.page

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.kunminx.architecture.utils.SPUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.ResetPasswordViewModel
import com.evport.businessapp.utils.LiveBus
import kotlinx.android.synthetic.main.fragment_charge_whitelist_edit.*


/**
 * Create by KunMinX at 20/04/26
 */

const val ConnectWIFI = "ConnectWIFI"
const val ConnectWIFICallBack = "ConnectWIFICallBack"

class ChargeWhiteListEditFragment : BaseFragment() {


    //    private lateinit var mChargeSetViewModel: DrawerViewModel
    private lateinit var mResetPwdViewModel: ResetPasswordViewModel
//    private val item by lazy {
//        arguments?.getParcelable<ChargeSetting>(CHARGE_SETTING)
//    }
//    private val str by lazy {
//        arguments?.getString(WHITELIST, "")
//    }

    override fun initViewModel() {
        mResetPwdViewModel = getFragmentViewModel(ResetPasswordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_charge_whitelist_edit, mResetPwdViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mBinding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_login);
//        getLifecycle().addObserver(DrawerCoordinateHelper.getInstance());
    }

//    override fun onNetworkStateChanged(netState: NetState?) {
//        super.onNetworkStateChanged(netState)
//    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == ConnectWIFICallBack) {
                if (it.value == ConnectWIFICallBack) {
                    try {
                        if (rember) {
                            SPUtils.getInstance().put(ConnectWIFICallBack, wifiName)
                            SPUtils.getInstance().put(wifiName, mResetPwdViewModel.password.get().toString())
                        } else {
                            SPUtils.getInstance().put(wifiName, "")
                            SPUtils.getInstance().put(ConnectWIFICallBack, "")
                        }
                        dismissLoading()
                        nav().navigateUp()
                    } catch (e: Exception) {
                        dismissLoading()

                    }
                } else {
                    dismissLoading()
                }
            }
        })
        wifiName = SPUtils.getInstance().getString(ConnectWIFICallBack)
        val pas = SPUtils.getInstance().getString(wifiName)
        mResetPwdViewModel.emailCode.set(wifiName)
        mResetPwdViewModel.password.set(pas)
        rember=wifiName.isNotBlank()&&pas.isNotBlank()

        emailCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wifiName = s.toString()
                val p = SPUtils.getInstance().getString(wifiName)
                if (!p.isNullOrBlank()) {
                    mResetPwdViewModel.password.set(p)
                    remeber.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_select_wifi,
                        0,
                        0,
                        0
                    )
                } else {
                    remeber.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_unselect,
                        0,
                        0,
                        0
                    )
                    mResetPwdViewModel.password.set("")

                }
//                if (rember){
//                    SPUtils.getInstance().put(wifiName,mResetPwdViewModel.password.get().toString())
//                }else{
//                    SPUtils.getInstance().put(wifiName,"")
//                }
            }

        })

    }

    var rember = false
    var wifiName = ""

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun toSetPassword() {
//
//            if (TextUtils.isEmpty(mChargeSetViewModel.password.get())) {
//                showLongToast("email is empty")
//                return
//            }
//            if (mChargeSetViewModel.password.get()?.trim() == SPUtils.getInstance().getString(Configs.EMAIL).trim()) {
//                showLongToast("can not add yourself")
//                return
//            }
//            if (str.isNullOrEmpty()) {
//                if (item?.whiteList.isNullOrEmpty()) {
//                    item?.whiteList = mChargeSetViewModel.password.get().toString()
//                } else {
//                    item?.whiteList = item?.whiteList.plus(",")
//                        .plus(mChargeSetViewModel.password.get().toString())
//                }
//            } else {
//                item?.whiteList = item?.whiteList?.replace(
//                    str.toString(),
//                    mChargeSetViewModel.password.get().toString()
//                ).toString()
//            }
//            showLoading()
//            mChargeSetViewModel.requestChargeSet(item)


        }

        fun sendEmailCode() {
            rember = !rember
            if (rember) {
//                SPUtils.getInstance().put(wifiName,mResetPwdViewModel.password.get().toString())
                remeber.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_select_wifi,
                    0,
                    0,
                    0
                )
            } else {
                remeber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_unselect, 0, 0, 0)
//                SPUtils.getInstance().put(wifiName,"")
            }
        }

        fun toConfirm() {

            if (TextUtils.isEmpty(mResetPwdViewModel.emailCode.get())
            ) {
                showLongToast("wifi name is empty")
                return
            }
//            if (TextUtils.isEmpty(mResetPwdViewModel.password.get())
//            ) {
//                showLongToast("password is empty")
//                return
//            }

            showLoading()
            LiveBus.getInstance().post(
                EventBean(
                    ConnectWIFI, User(
                        email = mResetPwdViewModel.emailCode.get(),
                        password = mResetPwdViewModel.password.get().toString(),

                    ),""
                )
            )
//            mResetPwdViewModel.resetPassword(
//                User(
//                    email = mResetPwdViewModel.email.get(),
//                    code = mResetPwdViewModel.emailCode.get(),
//                    password = mResetPwdViewModel.password.get().toString()
//                )
//            )

        }


        fun toLockPassword() {
            mResetPwdViewModel.passwordVisible.set(!mResetPwdViewModel.passwordVisible.get())
        }


    }
}