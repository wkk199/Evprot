package com.evport.businessapp.data.bean

import androidx.annotation.Keep

@Keep
data class InvoiceHistoryBean(
    var createdTime: String? = "",
    var orderId: String? = "",
    var invoiceType: String? = "",
    var invoiceName: String? = "",
    var totalAmount: String? = "",
    var chargingFee: String? = "",
    var parkingFee: String? = "",
    var serviceFee: String? = "",
    var isSel: Boolean? = false,
    var isShowMore: Boolean? = false

){
    fun chargingFeeStr(): String {
        return chargingFee.toString()+"元"
    }
    fun parkingFeeStr(): String {
        return parkingFee.toString()+"元"
    }
    fun serviceFeeStr(): String {
        return serviceFee.toString()+"元"
    }
}
