package com.evport.businessapp.data.bean

import androidx.annotation.Keep
import java.math.BigDecimal

@Keep
data class InvoicListBean(
    var chargingOrderPk: String? = "",
    var startChargingTime: String? = "",
    var amount: BigDecimal? = null,
    var stationName: String? = "0",
    var chargingFee: BigDecimal? = null,
    var parkingFee: BigDecimal? = null,
    var serviceFee: BigDecimal? = null,
    var isSel: Boolean? = false,
    var isShowMore:Boolean?=false
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
