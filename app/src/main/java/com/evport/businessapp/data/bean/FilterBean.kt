package com.evport.businessapp.data.bean

import androidx.annotation.Keep


@Keep
data class FilterBean(
    val type: String,
    val value: Any
)