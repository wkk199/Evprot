package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopPrivacyPolicyPickerBinding
import com.evport.businessapp.utils.toPrivacyPolicy
import com.evport.businessapp.utils.toUserAgreement
import com.lxj.xpopup.core.CenterPopupView
import kotlin.system.exitProcess

class PrivacyPolicyPicker(context: Context) : CenterPopupView(context) {
    private lateinit var bind: PopPrivacyPolicyPickerBinding
    private var mOkBlock: (String) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_privacy_policy_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        initView()
    }

    fun initView() {
        bind.userAgreement.setOnClickListener {
            toUserAgreement(context)

        }
        bind.privacyPolicy.setOnClickListener {
            toPrivacyPolicy(context)

        }
        bind.confirm.setOnClickListener {
            mOkBlock.invoke("")
            dismiss()

        }
        bind.cancel.setOnClickListener {
            exitProcess(0)
        }

    }

    fun setCallBack(okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock

    }
}