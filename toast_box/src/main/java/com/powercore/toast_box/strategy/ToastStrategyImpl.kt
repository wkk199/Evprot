package com.powercore.toast_box.strategy

import android.view.View
import com.powercore.toast_box.ToastClickItf
import com.powercore.toast_box.ToastLifecycle
import com.powercore.toast_box.ToastLifecycle.getActivity
import com.powercore.toast_box.toast.ActivityToast
import com.powercore.toast_box.toast.xToast
import com.powercore.toast_box.style.ToastStyle
import com.powercore.toast_box.style.NormalStyle

/**
 * @ClassName ToastStrategyImpl
 * @Description toast显示策略
 * @Author AlexLu_1406496344@qq.com
 * @Date 2021/6/18 16:40
 */
class ToastStrategyImpl : ToastStrategy{

    private var view:View ?= null
    private var toast:xToast ?= null

    private var style : ToastStyle = NormalStyle()
    private var clickListener: ToastClickItf?= null


    override fun setStyle(style: ToastStyle) {
        this.style = style
    }

    override fun setListener(clickItf: ToastClickItf) {
        clickListener = clickItf
    }

    private var useCustomView:Boolean = false

    @Synchronized
    override fun setView(view: View) {
        useCustomView = true
        this.view = view
    }

    override fun getView(): View? {
        return this.view
    }

    @Synchronized
    override fun createToast(): xToast? {
        val activity = getActivity()
        if (activity == null || activity.isDestroyed || activity.isFinishing){
            return null
        }else{
            val toast = ActivityToast()
            if (useCustomView && view!=null){
                //自定义View
                toast.setView(view)
                useCustomView = false
                toast.isCustomView = true
            }else{
                toast.setView(style.view)
                toast.setTextStyle(style.textTheme)
                toast.setBackDrawable(style.backDrawable)
                toast.isCustomView = false
            }
            toast.x = style.x
            toast.y = style.y
            toast.duration = style.duration
            toast.setGravity(style.location)
            toast.setAnimStyle(style.anim)
            toast.setAlpha(style.alpha)
            toast.setListener(clickListener)
            return toast
        }
    }

    override fun getIToast(): xToast? {
        return toast
    }

    override fun show(text: String) {
        toast = createToast()
        toast?.setText(text)
        toast?.show()
    }

    override fun cancle() {
        toast?.cancel()
        view = null
        toast = null
    }


}