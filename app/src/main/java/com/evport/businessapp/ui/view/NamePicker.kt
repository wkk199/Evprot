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


class NamePicker(context: Context, private val phone: String) : BottomPopupView(context) {
    private var mOkBlock: (String) -> Unit = {}
    private lateinit var bind: PopNamePickerBinding
    override fun getImplLayoutId(): Int {
        return R.layout.pop_name_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    fun initView() {

        bind.inputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    bind.close.visibility = View.VISIBLE
                } else {
                    bind.close.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        bind.inputName.setText(phone)
        //重置光标位置
        val index: Int = bind.inputName.text.toString().length
        bind.inputName.setSelection(index)
        bind.close.setOnClickListener {
            bind.inputName.setText("")
        }
        bind.cancel.setOnClickListener {
            dismiss()
        }
        bind.tvOk.setOnClickListener {
            var name = bind.inputName.text.toString()

            if (!TextUtils.isEmpty(name.trim()) && !name.trim().contains(" ")) {
                mOkBlock.invoke(name.trim())
                dismiss()
            } else {
                ToastUtils.showShort("Please enter your name")
            }
        }
    }

    fun setCallBack(okBlock: (String) -> Unit = {}) {
        mOkBlock = okBlock
    }
}