package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopAvatarPikerBinding
import com.lxj.xpopup.core.BottomPopupView

class PopAvatarPicker constructor(context: Context) : BottomPopupView(context) {

    private lateinit var bind: PopAvatarPikerBinding

    private var mCallBack: CallBack? = null

    override fun getImplLayoutId(): Int {
        return R.layout.pop_avatar_piker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!

        bind.apply {
            tvCamera.setOnClickListener {
                mCallBack?.clickCamera()
            }
            tvPhoto.setOnClickListener {
                mCallBack?.clickPhoto()
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
        fun clickCamera()
        fun clickPhoto()
    }
}