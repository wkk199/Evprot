package com.evport.businessapp.data.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.evport.businessapp.R
import kotlinx.android.parcel.Parcelize


/**
 *
 *
 * struct HomeFilter: Codable {

/// 米
var distance: Nearby = Constants.defaultNearby
var operators: [Operator] = []
var socketTypes = [SocketType(value: "type1", name: "J1772(Type 1)"),
SocketType(value: "type2", name: "Mennekes(Type 2)"),
SocketType(value: "ccs1", name: "CCS1"),
SocketType(value: "ccs2", name: "CCS2"),
SocketType(value: "CHAdeMO", name: "CHAdeMO")]
/// kW
var minPower: Int = 0
var maxPower: Int = 360

var statusIsSelected: Bool = false

}
 */
@Keep
@Parcelize
data class HomeFilter(

    /// 米
//    var distance: String = "20000.1",

    var operators: ArrayList<SocketType> = ArrayList<SocketType>(),

    var socketTypes: ArrayList<SocketType> = ArrayList(),
    var commonlyUsed: ArrayList<SocketType> = ArrayList(),
    /// kW
    var minPower: String = "",
    var maxPower: String = "",

    var statusIsSelected: Boolean = false,
    var isDefault: Boolean = false

) : Parcelable {
}

@Keep
@Parcelize
data class SocketType(
    val operatorPk: String,
    var name: String,
    var colorBgStatu: Int = R.color.light_green
) : Parcelable

@Keep
data class FeedbackCommit(
    var files: List<String>,
    var feedbackContent: String,
    var feedbackTag: String
)

@Keep
data class FeedbackReplyCommit(
    var img: List<String>,
    var content: String,
    var feedbackPk: String
)
