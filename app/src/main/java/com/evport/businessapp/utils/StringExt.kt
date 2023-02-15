package com.evport.businessapp.utils

import android.widget.Toast
import com.evport.businessapp.App.Companion.appContext
import com.evport.businessapp.R
import com.powercore.toast_box.BuildConfig
import com.powercore.toast_box.Location
import com.powercore.toast_box.ToastBox
import java.security.MessageDigest
import java.time.Duration


fun String.toast() {
    if (isBlank()) return
    ToastBox.setParams(
        layout = R.layout.toast_default,
        location = Location.CENTER,
        duration = 1500,
        anim = R.style.ToastAnim_ALPHA
    ).showToast(this)

}

fun String.toast(duration: Long) {
    if (isBlank()) return
    ToastBox.setParams(
        layout = R.layout.toast_default,
        location = Location.CENTER,
        duration = duration,
        anim = R.style.ToastAnim_ALPHA
    ).showToast(this)

}

fun getString(idRes: Int): String {
    return appContext.getString(idRes)
}