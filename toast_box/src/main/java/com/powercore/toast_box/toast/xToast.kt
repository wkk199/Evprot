package com.powercore.toast_box.toast

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.powercore.toast_box.Location
import com.powercore.toast_box.ToastClickItf

/**
 * @ClassName Toast
 * @Description TODO
 * @Author AlexLu_1406496344@qq.com
 * @Date 2021/6/18 16:42
 */
interface xToast {

    var x:Int
    var y:Int
    var duration:Long
    var isCustomView:Boolean

    /**
     * TODO 显示
     */
    fun show()

    /**
     * TODO 取消
     */
    fun cancel()

    /**
     * TODO 设置文本
     */
    fun setText(text:String)

    /**
     * TODO 设置布局
     */
    fun setView(view: View?)

    /**
     * TODO 获取布局
     */
    fun getView():View?

    /**
     * TODO 设置重心偏移
     */
    fun setGravity(location: Location)

    /**
     * TODO 获取显示重心
     */
    fun getGravity(): Int

    /**
     * TODO 设置动画样式
     */
    fun setAnimStyle(style:Int?)
    fun getAnimStyle():Int?

    /**
     * TODO 设置点击事件
     */
    fun setListener(clickItf: ToastClickItf?)
    fun getListener():ToastClickItf?

    /**
     * TODO 设置背景样式
     */
    fun setBackDrawable(@DrawableRes drawable: Int?)
    fun setBackDrawable(drawable: Drawable?)
    fun getBackDrawable():Drawable?

    /**
     * TODO 设置字体样式
     */
    fun setTextStyle(@StyleRes style:Int?)

    /**
     * TODO 设置默认显示图标
     * int left, int top, int right, int bottom
     */
    fun setIcon(@DrawableRes drawable: Int?,left:Int, top:Int, right:Int, bottom:Int)

    /**
     * TODO 设置透明度
     */
    fun setAlpha(i:Float)

    fun clear()

    val TAG:String?
}