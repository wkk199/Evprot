package com.powercore.toast_box


/*toast位置*/
enum class Location {
    TOP,
    CENTER,
    BOTTOM
}

/*toast风格*/
enum class ToastTextStyle {
    Black,
    White,
    GRAY
}

/*事件监听*/
interface ToastClickItf {
    fun setOnToastDismissed()
}