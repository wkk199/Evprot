package com.evport.businessapp.widget

import android.content.Context
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.evport.businessapp.App.Companion.appContext
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.HomeFilter
import com.evport.businessapp.data.bean.SocketType
import com.evport.businessapp.ui.page.adapter.FilterHomeListAdapter
import com.evport.businessapp.utils.getHomeFilterData
import com.evport.businessapp.utils.saveHomeFilterData
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.fragment_drawer.view.*

class PopFilterPicker constructor(context: Context) : BottomPopupView(context) {
    private var adapter1 = FilterHomeListAdapter(context)
    private var adapter2 = FilterHomeListAdapter(context)
    private var mOkBlock: (Boolean) -> Unit = {}
    override fun getImplLayoutId(): Int {
        return R.layout.fragment_drawer
    }

    override fun onCreate() {
        super.onCreate()
        et_energy.inputType = InputType.TYPE_NULL
        et_energy.isEnabled = false
//        et_energy.showSoftInputOnFocus = false;

        initView()
    }


    private fun initView() {

        adapter1.submitList(
            arrayListOf(
//                SocketType("GBT AC", "慢充"),
//                SocketType("GBT DC", "快充"),

                SocketType("Type1", "Type1"),
                SocketType("Type2", "Type2"),
                SocketType("CHAdeMO", "CHAdeMO"),
                SocketType("CCS1", "CCS1"),
                SocketType("CCS2", "CCS2")
            )
        )
        adapter2.submitList(arrayListOf<SocketType>(SocketType("Idle", "Idle")))


        delivery_recycler_view.adapter = adapter1
        recycler_view_3.adapter = adapter2


        //提交
        confirm_btn.setOnClickListener {
            setData()
            dismiss()
        }

        //重置
        clear_filter_tv.setOnClickListener {
            reSetData()

        }
    }


    override fun onShow() {
        reSetData()
        val homeFilter = context.getHomeFilterData()
        homeFilter?.apply {
            adapter1.selectItem = operators
            adapter2.selectItem = socketTypes
            if (minPower.isEmpty()) {
                et_energy.setText("7")
            } else {
                et_energy.setText(minPower)
            }
            et_energy.setSelection(minPower.toString().length)
        }

        et_energy.inputType = InputType.TYPE_CLASS_PHONE;
        et_energy.isEnabled = true
    }


    override fun onDismiss() {
        reSetData()
        super.onDismiss()
    }

    fun reSetData() {
        adapter1.selectItem = ArrayList()
        adapter2.selectItem = ArrayList()
    }

    override fun beforeShow() {
        super.beforeShow()
        hideSoftInput(et_energy)
    }

    override fun beforeDismiss() {
        super.beforeDismiss()
        hideSoftInput(et_energy)
    }

    fun setData() {
        val homeFilter = context.getHomeFilterData() ?: HomeFilter()
        homeFilter.apply {
            operators = adapter1.selectItem!!
            socketTypes = adapter2.selectItem!!
            statusIsSelected = socketTypes.isNotEmpty()
            minPower = et_energy.text.toString()
//            发送通知给map页面
        }
        context.saveHomeFilterData(homeFilter)
        if (homeFilter.operators.isNotEmpty() || homeFilter.socketTypes.isNotEmpty()) {
            mOkBlock.invoke(true)
        }
    }

    fun setCallBack(okBlock: (Boolean) -> Unit = {}) {
        mOkBlock = okBlock

    }


    private fun hideSoftInput(editText: EditText) {
        editText.clearFocus()
        val imm: InputMethodManager? =
            appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}