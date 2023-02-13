package com.evport.businessapp.data.bean

import androidx.annotation.Keep

@Keep
data class InvoiceBean(
    var homePk: String,
    var isSel: Boolean ? = false
)
