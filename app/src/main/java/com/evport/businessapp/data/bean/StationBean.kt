package com.evport.businessapp.data.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.evport.businessapp.App
import com.evport.businessapp.utils.*
import com.evport.businessapp.R
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

/**
Create by ljx at 2020/10/13
 **/

@Keep
data class StationDetailBean(
    val totalComment: String? = null,
    val comments: List<Comment>? = null,
    val sockets: List<Socket>? = null,
    val device: List<Device>? = null,
    var isCollect: Boolean = false,
    val info: Info? = null,
    var images: String? = null

)

@Keep
@Parcelize
data class Comment(
    val replyCounts: String? = null,
    val avatarUrl: String? = null,
    val replyAvatarUrl: String? = null,
    val appCommentsPk: String? = null,
    val commentDate: String? = null,
    val stationAvatarUrl: String? = null,
    val stationStreet: String? = null,
    val stationName: String? = null,
    val rating: String? = null,
    val userName: String? = null,
    val appUserPk: String? = null,
    var delFlag: String? = null,
    val content: String? = null,
    val commentsPk: String? = null,
    val commentPk: String? = null,
    val commentsReplyPk: String? = null,
    val commentsReplyRootPk: String? = null,
    val isNewRecord: String? = null,
    val replyDatetime: String? = null,
    val replyName: String? = null,
    val replySourcePk: String? = null,
    val replySourceUserName: String? = null,
    val replyContent: String? = null,
    val replyUserPk: String? = null,
    var check: Boolean? = false,
    var hasMoreReplyComment: Boolean? = null,
    var replyComment: List<ReplyDetail>? = null,
    val replySourceUserPk: String? = null

) : Parcelable {

    fun hasDelFlag(): Boolean {
        var b = false
        if ("1" == delFlag || delFlag == "true")
            b = true
        return b
    }


    fun commentDateString(): String {
        var date = ""
        commentDate?.apply {
            date = DateUtil.getFriendlyTimeSpanByNow(this).toString()
        }
        if (commentDate.isNullOrBlank())
            date = replyDatetimeString()
        return date
    }

    fun commentTime(): String {
        var date = ""
        if (!commentDate.isNullOrBlank()) {
            var times = commentDate!!.split(" ")
            var years = times[0]!!.split("-")
            date = if (DateUtil.getNowDay() == times[0]) {
                times[1].substring(0, 5)
            } else {
                if (DateUtil.getNowYear() == years[0]) {
                    years[1] + "-" + years[2] + " " + times[1].substring(0, 5)
                } else {
                    commentDate.toString().substring(0, 15)
                }

            }
        } else if (!replyDatetime.isNullOrBlank()) {
            var times = replyDatetime!!.split(" ")
            var years = times[0]!!.split("-")
            date = if (DateUtil.getNowDay() == times[0]) {
                times[1].substring(0, 5)
            } else {
                if (DateUtil.getNowYear() == years[0]) {
                    years[1] + "-" + years[2] + " " + times[1].substring(0, 5)
                } else {
                    replyDatetime.toString().substring(0, 15)
                }

            }
        }

        return date
    }


    fun usernameString(): String {
        return userName ?: replyName ?: ""
    }


    fun contentString(): String {
        return if (hasDelFlag())
            "The comment has been deleted"
        else
            content ?: replyContent.toString()
    }


    fun contentIsdelString(): String {
        return if (hasDelFlag()) {
            "The comment has been deleted"
        } else {
            if (content!!.length > 45) {
                content.substring(0, 45) + "..."
            } else {
                content.toString()
            }

        }

    }

    private fun replyDatetimeString(): String {
        return DateUtil.getFriendlyTimeSpanByNow(replyDatetime.toString().dateUTCToString())
            .toString()
    }

    fun replySourceUserNameString(): String {
        return replySourceUserName.plus(": ")
    }

    fun replyToReply(): Boolean {
        return !commentsReplyRootPk.isNullOrBlank()
    }

    fun ratingString(): String {
        return rating.RemainOne()
    }

}

//{
//    "replyAvatarUrl":null,
//    "replyUserPk":"327513968576876544",
//    "replyName":"sss",
//    "replyDatetime":"2020-11-26 03:31:22",
//    "replySourceUserName":"李俊柯",
//    "replyContent":"Test rep",
//    "commentsReplyPk":"384305127412269056",
//    "replySourceUserPk":"267298403065528320"
//    "delFlag":"false",

//{
//    "replyContent" : "Dads",
//    "replySourcePk" : "377063185779306496",
//    "rootReply" : null,
//    "stationStreet" : "中国江苏省南京市栖霞区文四路",
//    "stationAvatarUrl" : "http:\/\/cloud.cnpowercore.com:8090\/ftp\/image\/download\/2020_11\/f6c0d8f77f7d3952cf5d247760a80719.png",
//    "commentsReplyPk" : "385899607080960000",
//    "stationPk" : "291965733292736512",
//    "delFlag" : "true",
//    "check" : "false",
//    "replyName" : "Chinghoi",
//    "replyUserPk" : "328899049182838784",
//    "rootComment" : {}
//    "stationName" : "station 1",
//    "replyDatetime" : "2020-11-30 13:07:16",
//    "replyAvatarUrl" : "http:\/\/cloud.cnpowercore.com:8090\/ftp\/image\/download\/2020_10\/06d26d546943421c04e3bdbf89a04bcc.png",
//    "commentsReplyRootPk" : null
//}
//}
@Keep
@Parcelize
data class ReplyDetail(
//    val replyUserAvatarUrl: String? = null,
    val replyUserPk: String? = null,
    val replyName: String? = null,
    val replyDatetime: String? = null,
    val replySourceUserName: String? = null,
    val replyContent: String? = null,
    val replySourceUserPk: String? = null,
    val replySourcePk: String? = null,
    val commentsReplyPk: String? = null,
    val commentsPk: String? = null,
    var delFlag: String? = null,
    //
    val rootReply: ReplyDetail? = null,
    val stationStreet: String? = null,
    val stationAvatarUrl: String? = null,
    val stationPk: String? = null,
    val check: String? = null,
    val rootComment: Comment? = null,
    val stationName: String? = null,
    val commentsReplyRootPk: String? = null,
    var replyAvatarUrl: String? = null

) : Parcelable {

    fun hasDelFlag(): Boolean {
        var b = false
        if ("1" == delFlag || delFlag == "true")
            b = true
        return b
    }
fun replyContent(): String {
        return ": "+replyContent!!
    }
fun replyReplySourceUserNamet(): String {
        return replySourceUserName!!
    }

    fun conetntIsDelString(): String {
        return if (hasDelFlag()) {
            "The comment has been deleted"
        } else {
            if (replyContent!!.length > 45) {
                replyContent.substring(0, 45) + "..."
            } else {
                replyContent ?: ""
            }
        }


    }

    fun commentDateString(): String {
        var date = ""
        replyDatetime?.apply {
            date = this
        }
//        if (replyDatetime == null)
//            date = replyDatetimeString()
        return DateUtil.getFriendlyTimeSpanByNow(date).toString()
    }

    fun commentTime(): String {
        var date = ""
        if (!replyDatetime.isNullOrBlank()) {
            var times = replyDatetime!!.split(" ")
            var years = times[0]!!.split("-")
            if (DateUtil.getNowDay() == times[0]) {
                date = times[1].substring(0, 5)
            } else {
                if (DateUtil.getNowYear() == years[0]) {
                    date = years[1] + "-" + years[2] + " " + times[1].substring(0, 5)
                } else {
                    date = replyDatetime.toString().substring(0, 15)
                }

            }
        }
        return date
    }

    fun isCheck(): Boolean {
        var b = false
        b = check != "1"
        return b
    }
}

/**
 *  {
"createrName" : "",
"createTime" : null,
"creater" : "",
"feedbackPk" : "385804632267816960",
"page" : null,
"idx" : "",
"updatorName" : "",
"userName" : "Chinghoi",
"feedbackContent" : "Adsfasfasdfahfgd sgh fdhsdfh shed dfsh",
"feedbackDatetime" : "2020-11-30 06:49:52",
"updator" : "",
"isNewRecord" : "true",
"updateTime" : null,
"sqlMap" : null,
"app" : null,
"imgDir" : "http:\/\/cloud.cnpowercore.com:8090\/ftp\/image\/download\/2020_11\/a30630d35111ad27391b0a6bc48899e0.png,http:\/\/cloud.cnpowercore.com:8090\/ftp\/image\/download\/2020_11\/c6a7de416bca1309a197d421202d9061.png",
"userPk" : "328899049182838784",
"feedbackTag" : "problem encountered in payment",
"sortNo" : null,
"myUser" : null
 */
@Keep
@Parcelize
data class Feedback(
    val createrName: String? = null,
    val createTime: String? = null,
    val creater: String? = null,
    val feedbackPk: String? = null,
    val page: String? = null,
    val idx: String? = null,
    val updatorName: String? = null,
    var userName: String? = null,
    val feedbackContent: String? = null,
    val feedbackDatetime: String? = null,
    val updator: String? = null,
    val updateTime: String? = null,
    val sqlMap: String? = null,
    val app: Comment? = null,
    val userPk: String? = null,
    val sortNo: String? = null,
    val myUser: String? = null,
    var bgColor: Int? = R.color.colorTheme,
    var feedbackTag: String? = null,
    var replyFeedbackPk: String? = null,
    var isShowTime: Boolean? = false,
    var checked: Boolean = true

) : Parcelable {

    var imgDir: String? = ""
        set(value) {
            field = value
            value?.apply {
                if (this.isNotEmpty()) {
                    val s = value.split(",")
                    imgList.addAll(s)
                }
            }
        }
    var imgList: ArrayList<String> = arrayListOf()

    fun getImgJson() = Gson().toJson(imageList())

    fun imageList(): List<String> {
        //然后这个方法  得出得list 也是大小=1
        var list = ArrayList<String>()
        imgDir?.apply {
            if (this.isNotEmpty()) {
                val s = imgDir?.split(",")
                list.addAll(s!!)
            }
        }
        return list
    }

    fun commentDateString(): String {
        var date = ""
        if (!feedbackDatetime.isNullOrBlank()) {
            var times = feedbackDatetime!!.split(" ")
            var years = times[0]!!.split("-")
            date = if (DateUtil.getNowDay() == times[0]) {
                times[1].substring(0, 5)
            } else {
                if (DateUtil.getNowYear() == years[0]) {
                    years[1] + "-" + years[2] + " " + times[1].substring(0, 5)
                } else {
                    feedbackDatetime.toString().substring(0, 15)
                }

            }
        }
        return date
    }

    fun commentTime(): String {
        var date = ""
        feedbackDatetime?.apply {
            date = feedbackDatetime.dateUTCToString()

        }

        return date
    }

    fun showType(myUserPk: String?): Int {
        return when {
            userPk == null && feedbackPk == null -> FeedbackType
            userPk == myUserPk -> FeedbackTypeMy
            else -> FeedbackTypeRB
        }

    }
}

const val FeedbackType = 0
const val FeedbackTypeMy = 1
const val FeedbackTypeRB = 2

//"commentsReplyPk":"377104046139432960",
//"delFlag":"false",
//"replyContent":"测试一把。",
//"replyDatetime":"2020-11-06 06:36:51",
//"replyName":"李俊柯",
//"replyUserPk":"267298403065528320"
@Keep
data class ReplyListItem(
    val commentsReplyPk: String? = null,
    val replyDatetime: String? = null,
    val replyName: String? = null,
    val replyAvatarUrl: String? = null,
    val replyContent: String? = null,
    val replyUserPk: String? = null,
    var delFlag: String? = null,
    val detail: List<ReplyDetail>? = null

) {

    fun commentDateString(): String {
        var date = ""
        if (!replyDatetime.isNullOrBlank()) {
            var times = replyDatetime!!.split(" ")
            var years = times[0]!!.split("-")
            date = if (DateUtil.getNowDay() == times[0]) {
                times[1].substring(0, 5)
            } else {
                if (DateUtil.getNowYear() == years[0]) {
                    years[1] + "-" + years[2] + " " + times[1].substring(0, 5)
                } else {
                    replyDatetime.toString().substring(0, 15)
                }

            }
        }

        return date
    }

    fun hasDelFlag(): Boolean {
        var b = false
        if ("1" == delFlag || delFlag == "true")
            b = true
        return b
    }
}
/**
 *replyUserAvatartUrl	String	回复用户的头像地址
replyUserPk	String	回复用户的主键
replyUserName	String	回复用户的名称
replyDatetime	String	回复的时间
replyToUsername	String	被回复的用户名
replyToUserPk	String	被回复的用户主键
replyContent	String	回复的内容
replyPk	String	该回复消息的主键
delFlag	String	是否被删除

}

"app":null,
"check":"0",
"commentsPk":"369824352109764608",
"commentsReplyPk":"369824622671732736",
"commentsReplyRootPk":null,
"createTime":null,
"creater":"",
"createrName":"",
"delFlag":"0",
"idx":"",
"isNewRecord":"true",
"myUser":null,
"page":null,
"replyContent":"1",
"replyDatetime":"2020-10-17 04:31:00",
"replyName":"Chinghoi",
"replySourcePk":"369824352109764608",
"replySourceUserName":"李俊柯",
"replySourceUserPk":"267298403065528320",
"replyUserPk":"328899049182838784",
"sortNo":null,
"sqlMap":null,
"updateTime":null,
"updator":"",
"updatorName":""
}
 */

/**
 * {
"socketType":"CCS1",
"total":"1",
"idle":"0"
}
 */

@Keep
data class Socket(
    val socketType: String? = null,
    val total: String? = null,
    val idle: String? = null

)

@Keep
@Parcelize
data class Device(
    val connectors: List<Connector>? = null,
    val chargerId: String? = null,
    val chargerPk: String? = null,
    var isCollect: Boolean = false

) : Parcelable

@Keep
@Parcelize
data class Connector(
    val connectorPk: String? = null,
    val socket: String? = null,
    val power: String? = null,
    val status: String? = null,
    val connectorType: String? = null,
    var connectorStatus: String? = null

) : Parcelable {
    fun bgColor(): Int {
        return App.appContext.resources.getColor(status.diviceTypebgcolor())
    }

    fun statusText(): String {
        return App.appContext.resources.getString(status.statusStrRes())
    }

    fun textColor(): Int {
        return App.appContext.resources.getColor(status.diviceTypeTextcolor())
    }

    fun bgColor1(): Int {
        return App.appContext.resources.getColor(connectorStatus.diviceTypebgcolor())
    }

    fun statusText1(): String {
        return App.appContext.resources.getString(connectorStatus.statusStrRes())
    }

    fun textColor1(): Int {
        return App.appContext.resources.getColor(connectorStatus.diviceTypeTextcolor())
    }
    /*GBT AC = 慢充 GBT DC = 快充*/
    fun isAC(): Boolean {
        return socket.equals("GBT AC")
    }
    fun isAC1(): Boolean {
        return connectorType.equals("GBT AC")
    }

//    fun socketTypeStr(): String {
//        return if (socket.equals("GBT AC")) {
//            "慢充"
//        } else {
//            "快充"
//        }
//    }
    fun socketTypeStr1(): String {
        return if (connectorType.equals("GBT AC")) {
            "慢充"
        } else {
            "快充"
        }
    }
}

@Keep
@Parcelize
data class Fee(
    val service: String? = null,
    val park: String? = null,
    val electricity: String? = null,
    val time: String? = null,
    val currency: String? = null

) : Parcelable {
    fun eleString(): String {
        return electricity.plus(" per kWh")
    }

    fun serviceString(): String {
        return service.plus(" per kWh")
    }

    fun parkString(): String {
        return park.plus(" per h")
//        return currency.toUnit().plus(park).plus(" per h")
    }
}

@Keep
@Parcelize
data class ConnectorDetail(
    val fee: List<Fee>? = null,
    val name: String? = null,
    val pk: String? = null,
    val connectorPk: String? = null,
    val socket: String? = null,
    val power: String? = null,
    val status: String? = null,
    val paymentMethod: List<String>? = null,
    val vendor: String? = null,
    val connectors: List<Connector>? = null

) : Parcelable {

    fun bgColor(): Int {
        return App.appContext.resources.getColor(status.diviceTypebgcolor())
    }

    fun statusText(): String {
        return App.appContext.resources.getString(status.statusStrRes())
    }

    fun textColor(): Int {
        return App.appContext.resources.getColor(status.diviceTypeTextcolor())
    }

    /*GBT AC = 慢充 GBT DC = 快充*/
    fun isAC(): Boolean {
        return socket.equals("GBT AC")
    }

    fun socketTypeStr(): String {
        return if (socket.equals("GBT AC")) {
            "慢充"
        } else {
            "快充"
        }
    }
}


@Keep
data class Info(
    val hotLine: String? = null,
    val openTime: String? = null

)

/**
 * type	String	必须;回复的类型: “comment” “reply”
sourcePk	String	必须;被回复的消息主键
content	String	必须;回复的内容
 */
@Keep
data class CommitComment(
    var type: String,
    var sourcePk: String,
    var content: String

)

/**
 * objectPk	String	必须;收藏对象的主键:stationPk 或者 chargerPk
collectTime	String	必须; 收藏的时间: yyyy-MM-dd hh:mm:ss

 */
@Keep
data class CollectionAdd(
    var objectPk: String? = "",
    var collectTime: String? = ""

)

@Keep
data class CommentAdd(
    var content: String,
    var rating: String,
    var transactionPk: String

)
//sysMessageTitle	String	系统消息标题
//sysMessageSummary	String	系统消息摘要
//sysMessageDate	String	新增时间
//sysMessageContent	String	系统消息内容
//sysMessagePk	String	系统消息主键

@Parcelize
@Keep
data class NotiSys(
    var sysMessageTitle: String? = "",
    var sysMessageSummary: String? = "",
    var sysMessageDate: String? = "",
    var sysMessageContent: String? = "",
    var sysMessagePk: String? = "",
    var checked: Boolean = true

) : Parcelable {
    //    todo date
    fun commentDateString(): String {
        var date = ""
        var times = sysMessageDate!!.split(" ")
        var years = times[0]!!.split("-")
        if (DateUtil.getNowDay() == times[0]) {
            date = times[1]
        } else {
            if (DateUtil.getNowYear() == years[0]) {
                date = years[1] + "-" + years[2] + " " + times[1]
            } else {
                date = sysMessageDate.toString()
            }

        }
        return date
    }
    fun sysMessageSummaryStr(): String {
        return if (sysMessageSummary.isNullOrBlank()) {
            ""
        } else {
            "[Summary]$sysMessageSummary"
        }
    }

}

@Keep
data class MyCard(
    var currency: String? = "",
    var operatorName: String? = "",
    var balance: String? = "",
    var cardNumer: String? = "",
    var status: String? = "",
    val expiryDate: String? = ""

) {
    fun balanceCurrency(): String {

        return if (currency.isNullOrBlank())
            ""
        else
            currency?.toUnit().toString()
    }

    fun expiryDateStr(): String {
//        return expiryDate!!.split(" ")[0]
        return expiryDate!!
    }

    fun statusStr(): String {
        return when (status) {
            "lose" -> Utils.getApp().resources.getString(R.string.lose)
            "unbound" -> Utils.getApp().resources.getString(R.string.unbound)
            "expired" -> Utils.getApp().resources.getString(R.string.expired)
            else -> Utils.getApp().resources.getString(R.string.using)
        }
    }
}
