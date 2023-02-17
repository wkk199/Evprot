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
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.ResetPasswordViewModel
import com.evport.businessapp.utils.ACache
import com.evport.businessapp.utils.NoFastClickUtils
import com.evport.businessapp.utils.toMD5
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_resetpwd.*
import kotlinx.android.synthetic.main.fragment_resetpwd.email
import kotlinx.android.synthetic.main.fragment_resetpwd.emailCode
import kotlinx.android.synthetic.main.fragment_resetpwd.et_pwd
import kotlinx.android.synthetic.main.fragment_resetpwd.sendCode
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.*
import kotlin.math.roundToInt

/**
 *
 * 修改密码
 */
class ResetPasswordFragment : BaseFragment() {


    private lateinit var mResetPwdViewModel: ResetPasswordViewModel
    var aCache: ACache? = null
    var millisInFuture = 60000

    var timer: CountDownTimer? = null
    private fun initCountDownTimer() {
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
                    }
                }
            }

            override fun onFinish() {
                if (activity == null || activity!!.isDestroyed) {
                    return
                }
                runOnUiThread {
                    millisInFuture = 60 * 1000
                    sendCode.setTextColor(resources.getColor(R.color.white))
                    sendCode.isEnabled = true
                    sendCode.text = resources.getString(R.string.send)
                }
            }


        }
    }

    override fun initViewModel() {
        mResetPwdViewModel = getFragmentViewModel(ResetPasswordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_resetpwd, mResetPwdViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        aCache = ACache.get(activity)

        mResetPwdViewModel.userLiveData.observe(
            viewLifecycleOwner,
            Observer { user: User ->
                dismissLoading()
                sharedViewModel.resetPasswordSuccess.postValue(true)
                nav().navigateUp()

            }
        )
        val resetSmsTime = aCache!!.getAsString("resetSmsTime")
        val resetSmsPhone = aCache!!.getAsString("resetSmsPhone")
        val nowTime = Date().time
        if (!resetSmsTime.isNullOrBlank()) {

            val startTime = resetSmsTime.toLong()
            val chaTime = nowTime - startTime
            if (chaTime < 60 * 1000) {
                millisInFuture = ((60 * 1000) - chaTime).toInt()
                mResetPwdViewModel.email.set(resetSmsPhone)
            } else {
                millisInFuture = 60 * 1000
            }
        }
        initCountDownTimer()
        if (!resetSmsTime.isNullOrBlank()) {
            val startTime = resetSmsTime.toLong()
            val chaTime = nowTime - startTime
            if (chaTime < 60 * 1000) {
                timer!!.start()
            }
        }
        initView()
    }

    private fun initView() {
        email.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            mResetPwdViewModel!!.email.set(email.text.toString())
            sendCode.isEnabled = RegexUtils.isEmail(email.text.toString())
            change()
        }
        emailCode.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            mResetPwdViewModel!!.emailCode.set(emailCode.text.toString())
            change()
        }
        et_pwd.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            mResetPwdViewModel.password.set(et_pwd.text.toString())
            change()
        }
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun toSetPassword() {
//
//            if (TextUtils.isEmpty(mResetPwdViewModel.email.get()) || TextUtils.isEmpty(
//                    mResetPwdViewModel.emailCode.get()
//                )
//            ) {
//                showLongToast("email or emailCode  is empty")
//                return
//            }
//
//            nav().navigate(R.id.action_ResetPasswordFragment_to_ResetPasswordFragment2
//                ,Bundle().also {
//                it.putString(EMAIL,mResetPwdViewModel.email.get())
//                it.putString(EMAILCODE,mResetPwdViewModel.emailCode.get())
//            })

        }

        fun sendEmailCode() {
            if (TextUtils.isEmpty(mResetPwdViewModel.email.get())) {
                ToastUtils.showShort("email address is empty")
                return
            }
            showLoading()
            getCodeResetPwd(mResetPwdViewModel.email.get().toString())


        }

        fun toConfirm() {

            setForgetPassWordCode();


        }


        fun toLockPassword() {

            if (NoFastClickUtils.isFastClick())
                mResetPwdViewModel.passwordVisible.set(!mResetPwdViewModel.passwordVisible.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = et_pwd.text.toString().length
                    et_pwd.setSelection(index)
                }
            }
        }

    }

    fun getCodeResetPwd(email: String) {
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                dismissLoading()
                timer!!.start()
                aCache!!.put("resetSmsTime", Date().time.toString())
                aCache!!.put("resetSmsPhone", email)
                sharedViewModel.resetPasswordSuccess.postValue(true)
                ToastUtils.showShort("Verification code has been sent")
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }
                timer!!.cancel()
                timer!!.onFinish()
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                Log.e("hm---email1", Gson().toJson(mapOf(Pair("email", email))))
                return SingletonFactory.apiService.getCodeResetpwd(mapOf(Pair("email", email)))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        timer!!.onFinish()
        timer!!.cancel()
        sendCode.isEnabled = RegexUtils.isEmail(email.text.toString())
    }

    fun change() {
        btn_netx.isEnabled =
            !mResetPwdViewModel.email.get().isNullOrBlank() && !mResetPwdViewModel.emailCode.get()
                .isNullOrBlank() && !mResetPwdViewModel.password.get()
                .isNullOrBlank()
    }

    fun setForgetPassWordCode() {
        object : NetworkBoundResource<User>(object : NetworkStatusCallback<User> {
            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }
                dismissLoading()

            }

            override fun onSuccess(data: User?) {
                nav().navigateUp()
//                nav().navigate(
//                    R.id.action_resetPasswordFragment_to_settingNewPasswordFragment,
//                    Bundle().also {
//                        it.putString("email", mResetPwdViewModel.email.get())
//                        it.putString("smsCode", mResetPwdViewModel.emailCode.get())
//                    })
                dismissLoading()
            }

        }) {

            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.forgetPassword(
                    User(
                        email = mResetPwdViewModel.email.get(),
                        code = mResetPwdViewModel.emailCode.get(),
                        password = mResetPwdViewModel.password.get().toString().toMD5()
                    )
                )
            }

        }
    }
}