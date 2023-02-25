package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopAvatarPikerBinding
import com.evport.businessapp.databinding.PopLogoutPikerBinding
import com.lxj.xpopup.core.BottomPopupView

class PopLogoutPicker constructor(context: Context) : BottomPopupView(context) {

    private lateinit var bind: PopLogoutPikerBinding

    private var mCallBack: CallBack? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_logout_piker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        bind.apply {
            tvPhoto.setOnClickListener {
                mCallBack?.clickLogout()
            }
            tvCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setCallBack(callBack: CallBack) {
        this.mCallBack = callBack
    }

    interface CallBack {
        fun clickLogout()
    }
}