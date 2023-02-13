package com.evport.businessapp.ui.page

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.ResetPasswordViewModel
import com.evport.businessapp.utils.isPassword
import com.evport.businessapp.utils.toMD5
import kotlinx.android.synthetic.main.fragment_setting_new_password.*
import kotlinx.android.synthetic.main.fragment_setting_new_password.et_pwd
import kotlinx.coroutines.*


class SettingNewPasswordFragment  : BaseFragment(){

    private lateinit var mViewModel: ResetPasswordViewModel

    val phone by lazy {
        arguments?.getString("phone", "")
    }
    val smsCode by lazy {
        arguments?.getString("smsCode", "")
    }
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(ResetPasswordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_setting_new_password, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }
    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
        fun toLockPassword(){
            mViewModel.passwordVisible.set(!mViewModel.passwordVisible.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = et_pwd.text.toString().length
                    et_pwd.setSelection(index)
                }
            }

        }
        fun toLockPassword1(){
            mViewModel.passwordVisible1.set(!mViewModel.passwordVisible1.get())
            GlobalScope.launch(Dispatchers.IO) {
                delay(10)
                withContext(Dispatchers.Main) {
                    val index: Int = et_pwd1.text.toString().length
                    et_pwd1.setSelection(index)
                }
            }

        }
        fun toConfirm(){

            if (TextUtils.isEmpty(mViewModel.password.get())
            ) {
                showLongToast("密码不能为空")
                return
            }
            if (!isPassword(mViewModel.password.get())){
                showLongToast("密码长度为8-16位，建议包含数字和大小写字母")
                return
            }
            if (TextUtils.isEmpty(mViewModel.password1.get())
            ) {
                showLongToast("确认密码不能为空")
                return
            }
            if (!mViewModel.password.get().equals(mViewModel.password1.get())){
                showLongToast("两次密码输入不一致")
                return
            }

            showLoading()
            mViewModel.resetPassword(
                User(
                    phone = phone,
                    code =smsCode,
                    password = mViewModel.password.get().toString().toMD5()
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.userLiveData.observe(
            viewLifecycleOwner,
            Observer { user: User ->
                dismissLoading()
                sharedViewModel.resetPasswordSuccess.postValue(true)
                nav().navigateUp()

            }
        )
        initView()
    }
    private fun initView() {
        et_pwd.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格

            change()
        }
        et_pwd1.doOnTextChanged { charSequence, start, _, _ ->
            // 禁止EditText输入空格

            change()
        }
    }

    fun change() {
        btn_netx.isEnabled =
            !mViewModel.password.get().isNullOrBlank() && !mViewModel.password1.get()
                .isNullOrBlank()
    }
}