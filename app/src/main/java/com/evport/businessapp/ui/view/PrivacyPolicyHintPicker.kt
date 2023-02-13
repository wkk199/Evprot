package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopPrivacyPolicyHintPickerBinding
import com.evport.businessapp.utils.toPrivacyPolicy
import com.evport.businessapp.utils.toUserAgreement
import com.lxj.xpopup.core.CenterPopupView

class PrivacyPolicyHintPicker(context: Context) : CenterPopupView(context) {
    private lateinit var bind: PopPrivacyPolicyHintPickerBinding
    private var mOkBlock: (String) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.pop_privacy_policy_hint_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        initView()
    }

    fun initView() {
        bind.confirm.setOnClickListener {
            mOkBlock.invoke("")
            dismiss()
        }
        bind.cancel.setOnClickListener {
            dismiss()
        }
        bind.userAgreement.setOnClickListener {
            toUserAgreement(context)

        }
        bind.privacyPolicy.setOnClickListener {
            toPrivacyPolicy(context)

        }
    }
    fun setCallBack( okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock

    }
}