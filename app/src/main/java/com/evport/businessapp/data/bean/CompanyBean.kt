package com.evport.businessapp.data.bean

import androidx.annotation.Keep

@Keep
data class CompanyBean(
    var companyName: String? = "",
    var companyNumber: String? = ""
)
