package com.powercore.toast_box.strategy

import android.view.View
import com.powercore.toast_box.ToastClickItf
import com.powercore.toast_box.toast.xToast
import com.powercore.toast_box.style.ToastStyle

/**
 * @ClassName ToastD
 * @Description TODO
 * @Author AlexLu_1406496344@qq.com
 * @Date 2021/6/18 15:45
 */
interface ToastStrategy {

    fun createToast(): xToast?

    fun getIToast():xToast?

    fun setStyle(style: ToastStyle)

    fun setListener(clickItf: ToastClickItf)

    fun setView(view: View)

    fun getView():View?

    fun show(text:String)

    fun cancle()

}