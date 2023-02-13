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
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.SignUpViewModel
import com.evport.businessapp.utils.*
import com.kunminx.architecture.domain.manager.NetState
import com.kunminx.architecture.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_signup.btn_login
import kotlinx.android.synthetic.main.fragment_signup1.*
import kotlinx.coroutines.*


/**
 * 注册
 */
class SignUpFragment : BaseFragment() {
    private var mSignUpViewModel: SignUpViewModel? = null



    val email by lazy {
        arguments?.getString("email")?:""
    }
    val emailCode by lazy {
        arguments?.getString("emailCode")?:""
    }

    override fun initViewModel() {
        mSignUpViewModel = getFragmentViewModel(SignUpViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_signup1, mSignUpViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }


    private fun initView() {
        et_name.doOnTextChanged { charSequence, start, _, _ ->
            mSignUpViewModel!!.name.set(et_name.text.toString())
            change()
        }
        et_pwd.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            mSignUpViewModel!!.password.set(et_pwd.text.toString())
            change()
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        mSignUpViewModel!!.userLiveData.observe(
            viewLifecycleOwner
        ) { user: User? ->
            if (user != null) {
                SPUtils.getInstance()
                    .put(Configs.TOKEN, user.ssoticket)
                context?.saveUser(user)
                SPUtils.getInstance()
                    .put(Configs.EMAIL, user.email)
                SPUtils.getInstance()
                    .put(Configs.NAME, user.name)
                SPUtils.getInstance()
                    .put(Configs.PHONE, user.phone)
                mSignUpViewModel!!.loadingVisible.set(false)
                ToastUtils.showLong("Registration successful")
                //注册成功--直接登录
                sharedViewModel.isLoginSuccess.postValue(true)
            }
            dismissLoading()
        }

        initView()
    }

    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    inner class ClickProxy {

        fun signUp() {
            if (!isPassword(mSignUpViewModel!!.password.get())) {
                showLongToast("Password must contain 8 to 16 digits and uppercase and lowercase letters")
                return
            }

            showLoading()
            val result = mSignUpViewModel!!.password.get()?.toMD5()
            val user = User()
            user.email =email
            user.name =mSignUpViewModel!!.name.get()
            user.note = emailCode
            user.password = result

            mSignUpViewModel!!.requestSignUp(
                user
            )
        }

        fun toLogin() {
            nav().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        fun toSignUp() {
            nav().navigate(R.id.action_signUpFragment_to_signCodeFragment)
        }


        fun privacyPolicy() {
            toPrivacyPolicy(activity!!)
        }

        fun userAgreement() {
            toUserAgreement(activity!!)
        }

        fun toLockPassword() {
            mSignUpViewModel!!.passwordVisible.set(!mSignUpViewModel!!.passwordVisible.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = et_pwd.text.toString().length
                    et_pwd.setSelection(index)
                }
            }

        }
    }


    fun change() {
        btn_login.isEnabled = !(mSignUpViewModel!!.name.get().isNullOrBlank() || mSignUpViewModel!!.password.get().isNullOrBlank())

    }
}