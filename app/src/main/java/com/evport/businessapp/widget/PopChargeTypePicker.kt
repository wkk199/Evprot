package com.evport.businessapp.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.evport.businessapp.App.Companion.appContext
import com.evport.businessapp.R
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.fragment_drawer.view.*


class PopChargeTypePicker constructor(context: Context) : BottomPopupView(context) {


    override fun getImplLayoutId() = R.layout.fragment_drawer

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate() {
        super.onCreate()


        hideSoftInput(et_energy)
    }

    override fun onAttachedToWindow() {
        hideSoftInput(et_energy)
        super.onAttachedToWindow()
    }


    override fun onShow() {

        hideSoftInput(et_energy)
        super.onShow()
    }

    private fun hideSoftInput(editText: View) {
        val imm: InputMethodManager? =
            appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)


    }


    override fun beforeShow() {
        super.beforeShow()
        hideSoftInput(et_energy)
    }


}