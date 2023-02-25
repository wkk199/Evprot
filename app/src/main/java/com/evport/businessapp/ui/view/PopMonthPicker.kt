package com.evport.businessapp.ui.view

import android.content.Context
import android.view.View
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopDataPickerBinding
import com.evport.businessapp.databinding.PopMonthPickerBinding
import com.evport.businessapp.utils.DateUtil
import com.lxj.xpopup.core.BottomPopupView


class PopMonthPicker(context: Context, val selectYear: Boolean) : BottomPopupView(context) {

    private lateinit var bind: PopDataPickerBinding
    private var mCurrYear: Int = DateUtil.getCurrDate("yyyy").toInt()
    private var mCurrMonth: Int = DateUtil.getCurrDate("MM").toInt()
    private var mOkBlock: (String) -> Unit = {}
    private var mFinalYearMonth: Array<String?> = arrayOfNulls(2)
    private var mCurrYearNow: Int = DateUtil.getCurrDate("yyyy").toInt()
    private var mCurrMonthNow: Int = DateUtil.getCurrDate("MM").toInt()
    override fun getImplLayoutId(): Int {
        return R.layout.pop_data_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    private fun initView() {
        if (selectYear) {
            bind.wheelMonth.visibility = View.GONE
        } else {
            bind.wheelMonth.visibility = View.VISIBLE
        }


        bind.apply {
            wheelYear.apply {
                val max = mCurrYear
                val min = 1991
                wrapSelectorWheel =true
                refreshByNewDisplayedValues(arrayOfNulls<String>(max - min + 1).apply {
                    for (i in 0..max - min) {
                        this[i] = "${1991 + i}"
                    }
                })

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->

                    mFinalYearMonth[0] = displayedValues[newVal]
                    mFinalYearMonth[1] = "01"
                    if (mFinalYearMonth[0]!!.toInt() == mCurrYear) {
                        wheelMonth.refreshByNewDisplayedValues(arrayOfNulls<String>(mCurrMonth).apply {
                            for (i in 0..mCurrMonth - 1) {
                                this[i] = if (i < 9) "0${i + 1}" else "${i + 1}"
                            }
                        })
                    } else {
                        wheelMonth.refreshByNewDisplayedValues(arrayOfNulls<String>(12).apply {
                            for (i in 0..11) {
                                this[i] = if (i < 9) "0${i + 1}" else "${i + 1}"
                            }
                        })
                    }
                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrYearNow }
            }

            wheelMonth.apply {
                wrapSelectorWheel =true
                refreshByNewDisplayedValues(arrayOfNulls<String>(mCurrMonth).apply {
                    for (i in 0 until mCurrMonth) {
                        this[i] = if (i < 9) "0${i + 1}" else "${i + 1}"
                    }
                })
                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    mFinalYearMonth[1] = displayedValues[newVal]
                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrMonthNow }
            }

            tvOk.setOnClickListener {
                if (mFinalYearMonth[0]!!.toInt() == mCurrYear && mFinalYearMonth[1]!!.toInt() > mCurrMonth) {
                    ToastUtils.showShort("The time cannot be later than the current time")
                    return@setOnClickListener
                }
                mOkBlock.invoke("${mFinalYearMonth[0]}-${mFinalYearMonth[1]}")
                dismiss()
            }
            tvCancel.setOnClickListener { dismiss() }
        }
    }

    fun setCallBack(currYear: String, currMonth: String, okBlock: (String) -> Unit = {}) {
        mCurrYearNow = currYear.toInt()
        if (!currMonth.isNullOrBlank()){
            mCurrMonthNow = currMonth.toInt()
        }

        mOkBlock = okBlock

        mFinalYearMonth[0] = currYear
        mFinalYearMonth[1] = currMonth
    }
}