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
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.ChangeAPIActivity
import com.evport.businessapp.ui.state.LoginViewModel
import com.evport.businessapp.utils.*
import com.kunminx.architecture.domain.manager.NetState
import com.kunminx.architecture.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.startActivity


/**
 *
 *  登录
 */
class LoginFragment : BaseFragment() {
    private var mLoginViewModel: LoginViewModel? = null

    //    private FragmentLoginBinding mBinding;
    var etPwd: EditText? = null
    var ivView: ImageView? = null

    override fun initViewModel() {
        mLoginViewModel = getFragmentViewModel(LoginViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_login, mLoginViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        etPwd = view.findViewById(R.id.et_pwd)
        ivView = view.findViewById(R.id.iv_view)
//        var fastT = aCache!!.getAsString("fastT")
//        if (fastT.isNullOrBlank()) {
//            XPopup.Builder(activity).asCustom(
//                PrivacyPolicyPicker(
//                    activity!!
//                ).apply {
//                    setCallBack {
//                        aCache!!.put("fastT", "fastT")
//                        mLoginViewModel!!.isCheck.set(true)
//                        aCache!!.put("isPrivacy", "isPrivacy")
//                        // 地图
//                        MapsInitializer.updatePrivacyShow(activity, true, true)
//                        MapsInitializer.updatePrivacyAgree(activity, true)
//                        // 搜索
//                        ServiceSettings.updatePrivacyShow(activity, true, true)
//                        ServiceSettings.updatePrivacyAgree(activity, true)
//
//                    }
//                }
//            ).show()
//        }
        initView()
        mLoginViewModel!!.userLiveData.observe(
            viewLifecycleOwner,
            Observer { user: User? ->
                dismissLoading()
                if (user != null) {
                    SPUtils.getInstance()
                        .put(Configs.TOKEN, user.ssoticket)
                    context?.saveUser(user)
                    SPUtils.getInstance()
                        .put(Configs.EMAIL, user.email)
                    SPUtils.getInstance()
                        .put(Configs.PHONE, user.phone)
                    SPUtils.getInstance()
                        .put(Configs.NAME, user.name)
                    mLoginViewModel!!.loadingVisible.set(false)
                    //登录成功
                    sharedViewModel.isLoginSuccess.postValue(true)
                }
            }
        )
        clicktoapi.setOnLongClickListener {
            startActivity<ChangeAPIActivity>()
            false
        }
        et_name.setText(SPUtils.getInstance()
            .getString(Configs.EMAIL))
    }

    private fun initView() {
        et_name.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            spaceDetection(et_name, charSequence, start)
            mLoginViewModel!!.email.set(et_name.text.toString())
            change()
        }
        et_pwd.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格
            spaceDetection(et_pwd, charSequence, start)
            mLoginViewModel!!.password.set(et_pwd.text.toString())
            change()
        }
    }
    // 禁止EditText输入空格
    private fun spaceDetection(edit: EditText, charSequence: CharSequence?, start: Int) {
        if (charSequence.toString().contains(" ")) {
            val str = charSequence.toString().split(" ").toTypedArray()
            val sb = StringBuffer()
            for (i in str.indices) {
                sb.append(str[i])
            }
            edit.setText(sb.toString())
            edit.setSelection(start)
        }
    }


    //TODO tip: 网络状态的通知统一埋在 base 页面，有需要就在子类页面中重写
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    inner class ClickProxy {
        fun login() {
            showLoading()
            val result = mLoginViewModel!!.password.get()!!.toMD5()
            val user =
                User()
            user.account = mLoginViewModel!!.email.get()

            user.loginWay = "password"
            user.password = result
            mLoginViewModel!!.requestLogin(user)
            mLoginViewModel!!.loadingVisible.set(true)
        }

        fun toResetPassword() {
            if (NoFastClickUtils.isFastClick())
            //关闭软键盘
                closeKeyWord()
                nav().navigate(R.id.action_loginFragment_to_ResetPasswordFragment)
        }

        fun toLockPassword() {
            if (NoFastClickUtils.isFastClick())
                mLoginViewModel!!.passwordVisible.set(!mLoginViewModel!!.passwordVisible.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = et_pwd.text.toString().length
                    et_pwd.setSelection(index)
                }
            }

        }

        fun toSignUp() {
            if (NoFastClickUtils.isFastClick())
            //关闭软键盘
                closeKeyWord()
                nav().navigate(R.id.action_loginFragment_to_signCodeFragment)

        }


    }



    fun change() {
        btn_login.isEnabled = !(mLoginViewModel!!.email.get().isNullOrBlank() || mLoginViewModel!!.password.get().isNullOrBlank())
    }


}