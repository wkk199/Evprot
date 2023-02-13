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
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.ChangePassword
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.ResetPasswordViewModel
import com.evport.businessapp.utils.isPassword
import com.evport.businessapp.utils.toMD5
import kotlinx.android.synthetic.main.fragment_changepwd.*
import kotlinx.coroutines.*

/**
 * Create by KunMinX at 20/04/26
 */
class ChangePasswordFragment : BaseFragment() {


    private lateinit var mResetPwdViewModel: ResetPasswordViewModel
    override fun initViewModel() {
        mResetPwdViewModel = getFragmentViewModel(ResetPasswordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_changepwd, mResetPwdViewModel)
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mResetPwdViewModel.changePwdLiveData.observe(
            viewLifecycleOwner,
            Observer {
//              修改密码成功，退出登陆
                sharedViewModel.isLoginSuccess.postValue(false)
                dismissLoading()
                nav().navigateUp()
            }
        )
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun toSetPassword() {
//
            if (TextUtils.isEmpty(mResetPwdViewModel.email.get())) {
                showLongToast("请输入旧密码")
                return
            }
            if (TextUtils.isEmpty(mResetPwdViewModel.emailCode.get())) {
                showLongToast("请输入新密码")
                return
            }
            if (TextUtils.isEmpty(mResetPwdViewModel.confirmPassword.get())) {
                showLongToast("请输入确认新密码")
                return
            }
            if (!isPassword(mResetPwdViewModel!!.emailCode.get())) {
                showLongToast("密码长度为8-16位，建议包含数字和大小写字母")
                return
            }
            if (mResetPwdViewModel.emailCode.get().equals(mResetPwdViewModel.email.get())) {
                showLongToast("新密码与旧密码相同,请重新设置")
                return
            }
            if (!mResetPwdViewModel.emailCode.get().equals(mResetPwdViewModel.confirmPassword.get())) {
                showLongToast("两次输入密码不一致")
                return
            }
            showLoading()
            mResetPwdViewModel.changePassword(
                ChangePassword(
                    mResetPwdViewModel.email.get().toString().toMD5(),
                    mResetPwdViewModel.emailCode.get().toString().toMD5()
                )
            )

        }

        fun toLockPassword() {
            mResetPwdViewModel.passwordVisible.set(!mResetPwdViewModel.passwordVisible.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = oldPwd.text.toString().length
                    oldPwd.setSelection(index)
                }
            }

        }

        fun toLockPassword2() {
            mResetPwdViewModel.passwordVisible2.set(!mResetPwdViewModel.passwordVisible2.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = new_pwd.text.toString().length
                    new_pwd.setSelection(index)
                }
            }

        }

        fun toLockPassword3() {
            mResetPwdViewModel.passwordVisible3.set(!mResetPwdViewModel.passwordVisible3.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = confirm_new_pwd.text.toString().length
                    confirm_new_pwd.setSelection(index)
                }
            }

        }

    }
}