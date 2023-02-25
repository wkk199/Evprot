package com.evport.businessapp.data.bean

import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.evport.businessapp.utils.*
import kotlinx.android.parcel.Parcelize


//data class Charge(val chargeBoxId:String,val chargeBoxPk:String,val whiteList: String)

/**
 * whiteList - 逗号分割的邮箱
 */

@Keep
@Parcelize
data class ChargeSetting(
    var password: String,
    var chargeBoxPk: String,
    var whiteList: String,
    var online: String,
    val chargeBoxId: String
) :
    Parcelable {

}

@Keep
data class RequestStats(
    var type: String,
    var need: Boolean,
    var month: String? = null,
    var year: String? = null
)

@Keep
data class NameValue(
    var name: String,
    var value: String
) {

    fun nameToUnit(): String {
        var s = name.toUnit()
        if (s.length > 1)
            s = s.substring(0, 1)
        return s
    }
}

@Keep
data class StatsData(
    val data: List<StatsDataY>,
    val amount: String? = null ?: "0.0",
    val xvalue: String,
    val energy: String? = null ?: "0.0"//电量
) {
    fun amountFloat(): Float {
        return if (amount.isNullOrBlank())
            0f
        else
            amount.toFloat()
    }
}

@Keep
data class StatsDataY(
    val currency: String? = null ?: "-",
    val amount: String? = null ?: "0.0"
) {
    fun amountFloat(): Float {
        return if (amount.isNullOrBlank())
            0f
        else
            amount.toFloat()
    }
}

//"time" : "177107803",
//"money" : "{\"HKD\":\"825.72\",\"EUR\":\"1.86\",\"unknown\":\"3.67\",\"CNY\":\"47614.13\"}",
//"power" : "-15.159",
//"times" : "113",
@Keep
data class StatsDataResp(
    val ek: MutableList<StatsData>? = null ?: arrayListOf<StatsData>(),
    val currencies: List<String>,
    val times: String,
    val time: String,
    val money: String,
    val power: String
) {
    fun moneySize(): Int {
        return try {
            val s = Gson().fromJson(money, HashMap::class.java)
            s.size
        } catch (e: Exception) {
            0
        }

    }
}


@Keep
data class RecordResp(

    /**
     *
     * startTime	String	充电开始时间
    chargeBoxId	String	充电桩id
    email	String	App用户邮箱
    chargingTime	String	充电用时
    energy	String	耗电量: kWh

    "chargingDate" : "2020-09-28 17:02:08",
    "chargeBoxId" : "lijk",
    "unit" : "CNY",
    "amount" : "355.52",
    "transactionPk" : "363007483356622848"

     */
    var unit: String? = "￥",
    val chargingDate: String,
    var amount: String,
    val transactionPk: String,//详情页需要
    //
    var energy: String = "0",
    val chargingTime: String,
    var startTime: String,
    val chargeBoxId: String,
    var so2: String = energy.toSo2kg(),
    var co2: String = energy.toCo2kg(),
    var dust: String = energy.toDustkg(),
    val email: String? = null,
    var flag: Boolean = true,
    var stationName: String? = null,
    val chargingState: String? = null,
) {
    fun dateString(): String {
        return chargingDate
    }

    fun unitString(): String {
        return if (amount.isNullOrBlank())
        //unit.toUnit().plus(amount)
            "0"
        else
            "$amount"
    }
//
    fun chargingStateString(): String {
        return if (flag) {
            "Settled"
        } else {
            "Unsettled"
        }
    }
//
//    fun isChargingState(): Boolean {
//        return chargingState == "over"
//    }
}

@Keep
data class RecordDetailResp(

    /**
    "chargingFee" : "0.00",
    "parkingFee" : "0.04",
    "servericeFee" : "0.00",
    "startTime" : "2020-11-11 06:36:36",
    "status" : "over",
    "appCommentsPk" : null,
    "stationName" : "station 1",
    "payment" : "Charging card",
    "amount" : "0.04",
    "totalFee" : "0.04",
    "connector" : "zcj0220080001_1",
    "stopTime" : "2020-11-11 06:37:23",
    "currency" : "EUR",
    "orderId" : "378870661382234112",
    "energy" : "0.000",
    "stationPk" : "291965733292736512",
    "balance" : "1063932.01"
     */
    var chargingFee: String = "0",
    var parkingFee: String = "0",
    var servericeFee: String = "0",
    var startTime: String = "",
    var status: String = " ",
    var appCommentsPk: String? = null ?: "",
    var stationName: String = " ",
    var payment: String = " ",
    var amount: String = "0",//详情页需要
    //
    var totalFee: String = "0",
    var connector: String = " ",
    var stopTime: String = " ",
    var currency: String = "￥",
    var orderId: String? = null ?: "",
    var energy: String = " ",
    var stationPk: String? = null ?: "",
    var balance: String? = null
) {
    fun unitString(): String {
        return currency.toUnit()
    }

    fun totalFeeString(): String {
        return currency.toUnit().plus(totalFee)
    }

    fun totalFeeString1(): String {
        return currency.toUnit().plus(totalFee)
    }

    fun balanceString(): String {
        return currency.toUnit().plus(balance)
    }

    fun eleFeeString(): String {
        return currency.toUnit().plus(chargingFee)
    }

    fun parkFeeString(): String {
        return currency.toUnit().plus(parkingFee)
    }

    fun serveFeeString(): String {
        return currency.toUnit().plus(servericeFee)
    }

    fun chargeTimeString(): String {
        return DateUtil.stringToLong(stopTime).minus(DateUtil.stringToLong(startTime)).toString()
            .toDayFrendly2()
    }

    fun startTimeString(): String {
        return startTime
    }

    fun stopTimeString(): String {
        return stopTime
    }

    //    去评论按钮是否显示
    fun commentShow(): Boolean {
        return appCommentsPk.isNullOrBlank() && !stationPk.isNullOrBlank() && !orderId.isNullOrBlank()
    }

//    fun paymentString(): String {
//        return if (payment == "Charging card") {
//            "Charging card"
//        } else {
//            "账户余额"
//        }
//    }

    fun statusString(): String {
        return if (status == "over") {
            "Payment completed"
        } else {
            ""
        }
    }


}

@Keep
data class RequestRecord(
    var order: String = "",//充电记录的类型
    var pageNum: Int = 1,
    var desc: Boolean? = null ?: true,
    var transactionPk: String? = "",
    var orderField: String? = "startTimestamp"
)

@Keep
data class RequestRecordDetail(
    var transactionPk: String? = null
)

@Keep
data class RequestScan(
    var QRCode: String? = null,
    var chargeBoxPk: String? = null,
    var password: String? = null
)


@Keep
data class GunResp(
    val connectorPk: String? = null,
    val connectorId: String? = null,
    var itemSelect: Boolean? = false,
    var colorBg: Int,
    var colorBgStatu: Int,
    val status: String? = null
)

/**
 * 参数格式
字段名	类型	说明
chargingSchedulePk	String	必须;定时任务主键
powerWay	int	必须; 优先启动的充电方式; 1: 优先使用光伏发电, 0: 使用电网.
beginChargingDatetime	String	必须; yyyy-MM-dd HH:mm:ss, 精确到分钟即可.
auto	int	必须; 是否自动切换能源,在使用电网的时候该字段必须为空. 1: 自动切换, 2: 非自动切换

chargeBoxPk	String	必须
connectorId	int	必须



 */
@Keep
data class RequestCharge(
//    通用
    var powerWay: String? = null,
    val beginChargingDatetime: String? = null,
//    修改定时需要
    var auto: Int? = null,
    val chargingSchedulePk: String? = null, // 立即启动，删除需要
//    设置定时需要
    var connectorId: String? = null,
    val chargeBoxPk: String? = null
)

/**
 * chargeBoxPk	String	必须;充电桩主键
connectorId	int	必须;充电枪id
flag	boolean	可选;是否自动切换, 如果是电网充电, 该字段为空.
powerWay	boolean	必须;是否使用光伏, true:光伏充电, false:电网

 */
@Keep
data class RequestChargeChange(
    var transactionPk: String? = null,//remoteStop need
    var powerWay: String? = null,
    var flag: String? = null,
    var connectorId: String? = null,
    val chargeBoxPk: String? = null,
//
    val connectorPk: String? = null,
    val intentId: String? = null,
    val methodId: String? = null,
    val value: String? = null,
    val setting: String? = null,

    val paymentMethod: String? = null,
    val card: String? = null,
    val year: String? = null,
    val month: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val cvc: String? = null,
    var payWay: String? = null
)

@Keep
data class RemoteStartRep(
    var status: String? = null,
    var clientSecret: String? = null,
    var intentId: String? = null
)

@Keep
data class GetEphemeralKey(
    var apiVersion: Any? = null,
    var connectorPk: Any? = null
)

@Keep
data class GetEphemeralKeyRep(
    var apiVersion: String? = null,
    var connectorPk: String? = null
)
//
//@Keep
//data class ChargePercent(
//)

//data class ScanResp(
//    var s:String
//)

/**
 * 字段	类型	说明

 *字段	类型	说明
 *strategy	List<JSON>	充电策略

 *usedEnergy	String	充电量, 单位kWh
 *soc	String	电池百分比(不带%符合),只有直流桩才有
 * location	String	地址;
 * voltage	String	电压;不带单位
 * current	String	电流;不带单位
 * power	String	功率;不带单位
 * startTime	String	开始充电时间, 0时区时间
 * fee	String	当前充电的费用
 * currencyCode	String	货币的编码, 如: CNY
 * transactionPk	String	交易主键
 * type	String	充电枪类型
 * chargingSetting	String	启动充电设置
 * settingValue	String	设置的值

 */
@Keep
data class CheckTransaction(
    var strategy: List<Strategy>? = null,
    var usedEnergy: String? = "0",
    var soc: String? = null,
    var location: String? = null,
    var voltage: String? = null ?: "--",
    var current: String? = null ?: "--",
    var power: String? = null ?: "--",
    var startTime: String? = null,
    var fee: String? = null,
    var progress: String? = null ?: "0",
    var currencyCode: String? = "$",
    var transactionPk: String? = null,
    var type: String? = null,
    var chargingSetting: String? = null ?: "",
    var meterTimeStamp: String? = null,
    var status: String? = null,
    var settingValue: String? = null

) {

    fun isTypeCCS(): Boolean {
        return type != null && (type == "CCS1" || type == "CCS2")
    }


    fun usedEnergyString(): String {
        return if (usedEnergy == null)
            "--"
        else
            usedEnergy.toString()
    }

    fun powerString(): String {
        return power.toString().plus("kW")
    }

    fun voltageString(): String {
        return voltage.toString().plus("V")
    }

    fun currentString(): String {
        return current.toString().plus("A")
    }

    fun feeString(): String {
        return currencyCode.toString().toUnit().plus(fee ?: "0")
    }

    fun startTimeString(): String {
        return DateUtil.UTCToString(startTime.toString().dateUTCToString())
    }
//   fun getCurrent():String{
//       return this.current?.toCurrent().toString()
//   }
//   fun getCurrentHomeRemainingPower():String{
//       return
//   }

}

@Keep
data class ReqDefault(
    val default: String = "default"

) {
}

@Keep
data class ReqDefaultPk(
    val transactionPk: String = ""

) {
}

@Keep
data class Strategy(
    val service: String? = null ?: "--",
    val park: String? = null ?: "--",
    val energy: String? = null ?: "--",
    val time: String? = null ?: "--"

) {
//    fun getParkingString():String{
//        return parking?:"--"
//    }
}

/***
 *
sockets	List<JSON>	满足条件的充电枪接口集合

"stationAvatarUrl":null,
"sockets":[
{
"socketType":"CCS1",
"total":"1",
"idle":"0"
}
],
},

 */

@Keep
@Parcelize
data class StationListBean(
    val stationPk: String? = null,
    val stationAvatarUrl: String? = null,
    val stationName: String? = null,
    var distance: String? = null,
    val street: String? = null,
    val operatorName: String? = null,
    val longitude: String? = null,
    val latitude: String? = null,
    val rating: String? = null,
    val sockets: List<Sockets>? = null,
    var minPower: String? = "0",
    var maxPower: String? = "360",
    var connectorStatus: List<Status>? = null

) : Parcelable {
    fun getDistanceString(): String {
        return String.format("%.2f", distance)
    }

    fun getSocketsNow(): List<Sockets> {
//        var sockets1 = arrayListOf<Sockets>()
//        sockets1.add(Sockets(socketType = "GBT DC", total = "0", idle = "0"))
//        sockets1.add(Sockets(socketType = "GBT AC", total = "0", idle = "0"))
//        sockets!!.forEach {
//            if (it.isAC()) {
//                sockets1[1].total = (sockets1[1].total!!.toInt() + it.total!!.toInt()).toString()
//                sockets1[1].idle = (sockets1[1].idle!!.toInt() + it.idle!!.toInt()).toString()
//            } else {
//                sockets1[0].total = (sockets1[0].total!!.toInt() + it.total!!.toInt()).toString()
//                sockets1[0].idle = (sockets1[0].idle!!.toInt() + it.idle!!.toInt()).toString()
//            }
//        }
//        if (sockets1[1].idle.equals("0") && sockets1[1].total.equals("0")) {
//            sockets1.removeAt(1)
//        }
//        if (sockets1[0].idle.equals("0") && sockets1[0].total.equals("0")) {
//            sockets1.removeAt(0)
//        }
        return sockets!!
    }

    fun ratingShow(): Boolean {
        return !(rating.isNullOrBlank() || rating.toFloat() <= 0)
    }

    fun ratingString(): String {
        return rating.RemainOne()
    }
}

/**
 * socketType	String	接口类型名称
idle	String	空闲的数量
total	String	总共的数量

 */
@Parcelize
@Keep
data class Sockets(
    val socketType: String? = null,
    var idle: String? = null,
    var total: String? = null
) : Parcelable {
    fun getNameIdle(): String {
        return "Idle $idle"
    }

    fun getAllIdle(): String {
        return "/All $total"
    }

    fun socketTypeIcon(): Int? {
        return socketType.toString().socketTypeIcon()
    }



}

@Parcelize
@Keep
data class Status(
    val count: String? = null,
    var status: String? = null,
    var total: String? = null
) : Parcelable {




}

/**
 *
 *
pageNum	int	必须;第几页; 从1开始取值, 一页默认十条;
socket	String[]	可选;充电枪接口: “type1”,”type2”,”ccs1”,”ccs2”,” CHAdeMO”
minPower	String	可选; 最小功率
maxPower	String	可选; 最大功率
operatorPk	String[]	可选;运营商主键
distance	int	必须;距离以内(米)
longitude	String	必须;app经度
latitude	String	必须;app维度

status	String	可选;充电枪状态, app上显示Idle, 传” Available”
 *
 */

@Keep
data class HomeSearch(
    var pageNum: Int = 1,
    var minPower: String? = "0",
    var maxPower: String? = "360",
    var distance: String? = null,
    var longitude: Double? = 0.0,
    var latitude: Double? = 0.0,
    var status: String? = null,
    var socket: List<String>? = null,
    var operatorPk: List<String>? = null
)

@Keep
data class StationCommentReq(
    var pageNum: Int? = null,
    var stationPk: String? = null,
    var feedbackPk: String? = null
)

data class SysDetailReq(
    var sysMessagePk: String? = null
)

data class RechargeReq(
    var amount: String? = null
)

@Keep
data class getCollectReq(
    var pageNum: Int? = null,
//可选;查看收藏的桩或站点, 如果不传则查询收藏的充电桩和充电站.可选的值: charger或station
    var type: String? = null,
    var longitude: Float? = 0.0f,
    var latitude: Float? = 0.0f
)


@Keep
data class CollectList(
    var stations: StationsCollect? = null,
    var devices: DeviceCollect? = null
)

@Keep
data class StationsCollect(
    var counts: Int,
    var data: List<StationListBean>? = null
)

@Keep
data class DeviceCollect(
    var counts: Int,
    var data: List<Device>? = null
)

@Keep
data class InvoiceVo(
    var pageNumber: Int? = 0
)

@Keep
data class CompanyNameVO(
    var companyName: String? = ""
)


@Keep
data class CreateInvoiceVo(
    var chargingOrderPks: ArrayList<String>? = null,
    var invoiceType: Int? = 0,
    var name: String? = "",
    var email: String? = "",
    var sendChargingOrderDetail: Boolean? = false,
    var isAllChargingOrder: Boolean? = false,
    var companyAddress: String? = "",
    var companyPhone: String? = "",
    var companyBankName: String? = "",
    var companyBankAccount: String? = "",
    var companyInvoiceNumber: String? = ""
)

@Keep
data class InvoiceHistoryVo(
    var pageNumber: Int? = 0,
    var status: Int? = 0
)

@Keep
data class DeleteUserReq(var name: String? = null)

