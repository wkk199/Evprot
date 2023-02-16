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
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.RegexUtils.isEmail
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.SignUpViewModel
import com.evport.businessapp.utils.*
import com.kunminx.architecture.domain.manager.NetState
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_signup.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import java.util.*
import kotlin.math.roundToInt


/**
 * 发送验证码
 */
class SignCodeFragment : BaseFragment() {
    private var mSignUpViewModel: SignUpViewModel? = null

    var aCache: ACache? = null
    var millisInFuture = 60000


    var timer: CountDownTimer? = null
    fun initCountDownTimer() {
        timer = object : CountDownTimer(millisInFuture.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (activity == null || activity!!.isDestroyed) {
                    return
                }
                runOnUiThread {
                    sendCode.setTextColor(resources.getColor(R.color.white))
                    sendCode.isEnabled = false
                    sendCode.text =
                        ((millisUntilFinished.toDouble().div(1000)).roundToInt() - 1).toString()
                    if (millisUntilFinished <= 0L) {
                        sendCode.text = resources.getString(R.string.send)
                        cancel()
                        onFinish()
                    }
                }
            }

            override fun onFinish() {
                if (activity == null || activity!!.isDestroyed) {
                    return
                }
                runOnUiThread {
                    millisInFuture = 60000
                    sendCode.setTextColor(resources.getColor(R.color.white))
                    sendCode.isEnabled = true
                    sendCode.text = resources.getString(R.string.send)
                }
            }
        }
    }


    override fun initViewModel() {
        mSignUpViewModel = getFragmentViewModel(SignUpViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_signup, mSignUpViewModel).addBindingParam(
            BR.click, ClickProxy()
        )
    }


    private fun initView() {
        email.doOnTextChanged { charSequence, start, _, _ ->
            mSignUpViewModel!!.email.set(email.text.toString())

            sendCode.isEnabled = isEmail(email.text.toString())

            change()
        }
        emailCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                mSignUpViewModel!!.emailCode.set(emailCode.text.toString())
                change()
            }

        })

    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)


        aCache = ACache.get(activity)
        initView()
    }


    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    inner class ClickProxy {


        fun signUp() {
            if (NoFastClickUtils.isFastClick())
            //关闭软键盘
            closeKeyWord()
//            showLoading()
//            nav().navigate(R.id.action_signCodeFragment_to_signUpFragment, Bundle().also {
//                it.putString("email", email.text.toString())
//                it.putString("emailCode", emailCode.text.toString())
//            }
//            )

            getValidRegistrationCode(email.text.toString(),emailCode.text.toString())
        }

        fun toLogin() {
            nav().navigate(R.id.action_SignCodeFragment_to_loginFragment)
        }


        fun sendEmailCode() {
            if (!isEmail(mSignUpViewModel!!.email.get())) {
                ToastUtils.showShort("email address is empty")
                return
            }
            showLoading()
            //关闭软键盘
            closeKeyWord()
            getCode(mSignUpViewModel!!.email.get().toString())
        }


        fun privacyPolicy() {
            toPrivacyPolicy(activity!!)
        }

        fun userAgreement() {
            toUserAgreement(activity!!)
        }


    }


    override fun onResume() {
        super.onResume()
        val signUpSmsTime = aCache!!.getAsString("signUpSmsTime")
        val signUpSmsPhone = aCache!!.getAsString("signUpSmsPhone")
        val nowTime = Date().time
        if (!signUpSmsTime.isNullOrBlank()) {
            val startTime = signUpSmsTime.toLong()
            val chaTime = nowTime - startTime
            if (chaTime < 60 * 1000) {
                millisInFuture = ((60 * 1000) - chaTime).toInt()
                mSignUpViewModel!!.email.set(signUpSmsPhone)
            } else {
                millisInFuture = 60 * 1000
            }
        }
        initCountDownTimer()
        if (!signUpSmsTime.isNullOrBlank()) {
            val startTime = signUpSmsTime.toLong()
            val chaTime = nowTime - startTime
            if (chaTime < 60 * 1000) {
                timer!!.start()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        timer!!.onFinish()
        timer!!.cancel()
        sendCode.isEnabled = isEmail(email.text.toString())
    }


    fun getCode(email: String) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {
            override fun onSuccess(data: String?) {

                if (timer!=null){
                    timer!!.onFinish()
                    timer!!.cancel()
                }
                initCountDownTimer()
                timer!!.start()

                aCache!!.put("signUpSmsTime", Date().time.toString())
                aCache!!.put("signUpSmsPhone", email)

                dismissLoading()
                "The registration code has been sent, if you didn't find it in your inbox, it may be in your spam".toast(5000)
//                ToastUtils.showLong("The registration code has been sent, if you didn't find it in your inbox, it may be in your spam.",2222)
            }

            override fun onFailure(message: String) {
                message.toast()
                timer!!.cancel()
                timer!!.onFinish()
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getCode(User(email = email))
            }
        }

    }


    fun getValidRegistrationCode(email: String,emailCode :String) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {
            override fun onSuccess(data: String?) {



                if (data!=null) {
                    nav().navigate(R.id.action_signCodeFragment_to_signUpFragment, Bundle().also {
                        it.putString("email", email)
                        it.putString("emailCode", emailCode)
                    }
                    )
                }


            }

            override fun onFailure(message: String) {
                message.toast()
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getValidRegistrationCode(User(email = email, note = emailCode))
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer!!.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer!!.cancel()
    }

    fun change() {
        btn_login.isEnabled =
            !(mSignUpViewModel!!.email.get().isNullOrBlank() || mSignUpViewModel!!.emailCode.get()
                .isNullOrBlank())

    }
}