package com.evport.businessapp.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.core.BottomPopupView
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopNamePickerBinding
import com.evport.businessapp.databinding.PopSexPickerBinding
import com.evport.businessapp.utils.toast


class SexPicker(context: Context, private val sex: String) : BottomPopupView(context) {
    private var mOkBlock: (String) -> Unit = {}
    private lateinit var bind: PopSexPickerBinding


    var sexString = ""

    override fun getImplLayoutId(): Int {
        return R.layout.pop_sex_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    fun initView() {

        val sexList: MutableList<String> = mutableListOf()
        sexList.add("Male")
        sexList.add("Female")
        sexList.add("Gender diverse")
        sexList.add("Prefer not to say")

        bind.apply {


            wheelSex.apply {

                type = true
                wrapSelectorWheel = true
                refreshByNewDisplayedValues(sexList.toTypedArray())

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    sexString = displayedValues[newVal]
                }
                value = displayedValues.indexOfFirst { it == sex }
            }
        }


        bind.cancel.setOnClickListener {
            dismiss()
        }
        bind.tvOk.setOnClickListener {
            if (sexString == "") {
                sexString = sex
            }

            mOkBlock.invoke(sexString)
            dismiss()
        }
    }

    fun setCallBack(okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock
    }
}