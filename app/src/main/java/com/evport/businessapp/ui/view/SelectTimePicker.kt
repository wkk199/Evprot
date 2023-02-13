package com.evport.businessapp.ui.view

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.evport.businessapp.R
import com.evport.businessapp.databinding.PopSelectTimePickerBinding
import com.evport.businessapp.utils.DateUtil
import com.lxj.xpopup.core.BottomPopupView

class SelectTimePicker(context: Context) : BottomPopupView(context) {
    private lateinit var bind: PopSelectTimePickerBinding
    private var mCurrHours: Int = DateUtil.getCurrDate("HH").toInt()
    private var mCurrMinute: Int = DateUtil.getCurrDate("mm").toInt()
    private var mCurrSecond: Int = DateUtil.getCurrDate("ss").toInt()
    private var mOkBlock: (String) -> Unit = {}
    private var mFinalHoursMinuteSecond: Array<String?> = arrayOfNulls(3)
    override fun getImplLayoutId(): Int {
        return R.layout.pop_select_time_picker
    }

    override fun onCreate() {
        super.onCreate()
        bind = DataBindingUtil.bind(popupImplView)!!
        initView()
    }

    fun initView() {
        bind.apply {
            wheelHour.apply {

                refreshByNewDisplayedValues(arrayOfNulls<String>(24).apply {
                    for (i in 0..23) {
                        this[i] = if (i < 10) "0${i }" else "${i }"
                    }
                })

                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->

                    mFinalHoursMinuteSecond[0] = displayedValues[newVal]

                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrHours }
            }

            wheelMinute.apply {
                refreshByNewDisplayedValues(arrayOfNulls<String>(60).apply {
                    for (i in 0..59) {
                        this[i] = if (i < 10) "0${i }" else "${i}"
                    }
                })
                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    mFinalHoursMinuteSecond[1] = displayedValues[newVal]
                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrMinute }
            }
            wheelSecond.apply {
                refreshByNewDisplayedValues(arrayOfNulls<String>(60).apply {
                    for (i in 0..59) {
                        this[i] = if (i < 10) "0${i}" else "${i}"
                    }
                })
                setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                    mFinalHoursMinuteSecond[2] = displayedValues[newVal]
                }

                value = displayedValues.indexOfFirst { it.toInt() == mCurrSecond }
            }
            tvOk.setOnClickListener {
                mOkBlock.invoke("${mFinalHoursMinuteSecond[0]}:${mFinalHoursMinuteSecond[1]}:${mFinalHoursMinuteSecond[2]}")
                dismiss()
            }
            tvCancel.setOnClickListener { dismiss() }
        }

    }

    fun setCallBack(
        currHours: String,
        currMinute: String,
        currSecond: String,
        okBlock: (String) -> Unit = {}
    ) {
        mCurrHours = currHours.toInt()
        mCurrMinute = currMinute.toInt()
        mCurrSecond = currSecond.toInt()
        mOkBlock = okBlock

        mFinalHoursMinuteSecond[0] = currHours
        mFinalHoursMinuteSecond[1] = currMinute
        mFinalHoursMinuteSecond[2] = currSecond
    }
}