package com.evport.businessapp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.blankj.utilcode.util.NetworkUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.evport.businessapp.App
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.FamilyList
import com.evport.businessapp.data.bean.HomeFilter
import com.evport.businessapp.data.bean.StationListBean
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.page.activity.PdfviewActivity
import com.evport.businessapp.ui.page.activity.WebViewActivity
import com.evport.businessapp.utils.loader.GlideApp
import java.math.BigDecimal
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.collections.ArrayList


fun Context.put(key: String, value: Any) {
    SPUtils.put(this, key, value)
}

fun Context.get(key: String, default: Any): Any {
    return SPUtils.get(this, key, default)
}

fun Context.saveHomeFilterData(setting: HomeFilter) {
    SPUtils.saveObjectToJsonStr(applicationContext, "FilterBean", setting)
}

fun Context.getHomeFilterData(): HomeFilter? {
    return SPUtils.readObjectFromJson(this, "FilterBean", HomeFilter::class.java)
}

fun Context.saveUser(setting: User) {
    SPUtils.saveObjectToJsonStr(applicationContext, "userBean", setting)
}

fun Context.getUser(): User? {
    return SPUtils.readObjectFromJson(this, "userBean", User::class.java)
}

fun Context.putDefaultFamilyItem(setting: FamilyList?) {
    SPUtils.saveObjectToJsonStr(applicationContext, "DefaultFamilyItem", setting)
}

fun Context.getDefaultFamilyItem(): FamilyList? {
    return SPUtils.readObjectFromJson(this, "DefaultFamilyItem", FamilyList::class.java)
}


// false == only wifi load
fun Context.saveIsWifiLoad(setting: Boolean) {
    put("IsWifiLoad", setting)
    com.blankj.utilcode.util.SPUtils.getInstance().put("IsWifiLoadb", setting)
}

fun Context.getIsWifiLoad(): Boolean {
    return get("IsWifiLoad", true) as Boolean
}

fun Context.saveHomeDistance(setting: Int) {
    put("SAVEDISTANCE", setting)
}

fun Context.getHomeDistance(): Int {
    return get("SAVEDISTANCE", 0) as Int
}

fun Context.saveLat(setting: Float) {
    put("SAVELAT", setting)
}

fun Context.getLat(): Float {
    return get("SAVELAT", 0.0f) as Float
}

fun Context.saveLng(setting: Float) {
    put("SAVELNG", setting)
}

fun Context.getLng(): Float {
    return get("SAVELNG", 0.0f) as Float
}


fun Context.getDistanceShow(str: String?): String {
    if (str == null)
        return ""

    try {

        return if (Configs.DISTANCE_MI == getHomeDistance()) {
//        if (str.toDouble()>1000){
            String.format("%.2f", str.toDouble().div(1609.344)).plus("mi")
//        }else{
//            String.format("%.2f", str).plus("mi")
//        }
        } else {
            if (str.toDouble() > 1000) {
                String.format("%.1f", str.toDouble().div(1000)).plus("km")
            } else {
                if (str.contains(".")) {
                    var s = str.split(".")
                    s[0].plus("m")
                } else {
                    str.plus("m")
                }

            }
        }
    } catch (e: Exception) {
        return str
    }
}

// kg
fun String.toSo2(): String {
    var s = "0"
    try {
        val d = this.toDouble().times(0.3619).times(17.79).times(10).div(1000)
        s = if (d > 100000000) {
            d.toBigDecimal().toPlainString()
        } else
            String.format("%.2f", d)
    } catch (e: Exception) {

    }
    return s
}

// kg
fun String.toCo2(): String {
    var s = "0"
    try {
        val d = this.toDouble() * 0.9590
        s = if (d > 100000000) {
            d.toBigDecimal().toPlainString()
        } else
            String.format("%.2f", d)
    } catch (e: Exception) {

    }
    return s
}

// kg
fun String.toDust(): String {
    var s = "0"
    try {
        val d = this.toDouble() * 0.3619 * 4.84 * 10
        s = if (d > 100000000) {
            d.toBigDecimal().toPlainString()
        } else
            String.format("%.2f", d)
    } catch (e: Exception) {

    }
    return s
}

//
fun String.toDayFrendly(): String {
    var s = ""
    try {
        val all = this.toLong().div(1)
        val d = all.div(24 * 60 * 60)
        val h = all.minus(d * 24 * 60 * 60).div(60 * 60)
        val min = all.minus(d * 24 * 60 * 60).minus(h * 60 * 60).div(60)
        val sec = all.minus(d * 24 * 60 * 60).minus(h * 60 * 60).minus(min * 60)
        s = when {
            d > 0 -> "${d}天 ${h}时${min}分${sec}秒"
            h > 0 -> "${h}时${min}分${sec}秒"
            min > 0 -> "${min}分${sec}秒"
            sec > 0 -> "${sec}秒"
            else -> "0"
        }
    } catch (e: Exception) {

    }
    return s
}

//
fun String.toDayFrendly2(): String {
    var s = ""
    try {
        val all = this.toLong().div(1000)
        val h = all.div(60 * 60)
        val min = all.minus(h * 60 * 60).div(60)
        val sec = all.minus(h * 60 * 60).minus(min * 60)
        s = "${String.format("%1$02d", h)}:${String.format("%1$02d", min)}:${
            String.format(
                "%1$02d",
                sec
            )
        }"
    } catch (e: Exception) {

    }
    return s
}

// kg
fun String.toSo2kg(): String {
    var s = "0.0kg"
    try {
        val d = this.toDouble().times(0.3619).times(17.79).times(10).div(1000)
        s = if (d > 100000000) {
            d.toBigDecimal().toPlainString().plus("kg")
        } else
            String.format("%.2f", d).plus("kg")
    } catch (e: Exception) {

    }
    return s
}

// kg
fun String.toCo2kg(): String {
    var s = "0.0kg"
    try {
        val d = this.toDouble() * 0.9590
        s = if (d > 100000000) {
            d.toBigDecimal().toPlainString().plus("kg")
        } else
            String.format("%.2f", d).plus("kg")
    } catch (e: Exception) {

    }
    return s
}

// kg
fun String.toDustkg(): String {
    var s = "0kg"
    try {
        val d = this.toDouble() * 0.3619 * 4.84 * 10
        s = if (d > 100000000) {
            d.toBigDecimal().toPlainString().plus("kg")
        } else
            String.format("%.2f", d).plus("kg")
    } catch (e: Exception) {

    }
    return s
}

// unit
fun String?.toUnit(): String {
    var s = ""
    this?.apply {
        s = CurrencyUtils.getCurrencySymbol(this)
//        if (s.isBlank()) {
//            s = this
//        }
    }
//   s = NumberFormat.getCurrencyInstance(Locale.US).format()

    return s.plus(" ")
}


internal object CurrencyUtils {
    var locales = arrayOf(
        Locale.CANADA,
        Locale.CANADA_FRENCH,
        Locale.CHINA,
        Locale.FRANCE,
        Locale.GERMANY,
        Locale.ITALY,
        Locale.JAPAN,
        Locale.KOREA,
        Locale.PRC,
        Locale.SIMPLIFIED_CHINESE,
        Locale.TAIWAN,
        Locale.TRADITIONAL_CHINESE,
        Locale.UK,
        Locale.US,
        Locale("en", "IN"),
        Locale("en", "CA"),
        Locale("zh", "HK"),
        Locale("zh", "MO"),
        Locale("zh", "SG"),
        Locale("en", "SG"),
        Locale("th", "TH"),
        Locale("vi", "VN"),
        Locale("ja", "JP"),
        Locale("ga", "IE"),
        Locale("ms", "MY"),
        Locale("fil", "PH"),
        Locale("en", "AU"),
        Locale("km", "KH"),
        Locale("id", "ID"),
//        Locale("BIF", "BIF"),
        Locale("es", "ES")
    )

    fun getCurrencySymbol(currencyCode: String): String {
        return try {
            val currency = Currency.getInstance(currencyCode)
            val currencyLocaleMap = TreeMap<Currency, Locale>(
                Comparator<Currency>
                { c1, c2 ->
                    c1.currencyCode.compareTo(c2.currencyCode)
                }
            )
            for (locale in locales) {

                val currencys = Currency.getInstance(locale)
                currencyLocaleMap[currencys] = locale

            }
            println(currencyCode + ":-" + currency.getSymbol(currencyLocaleMap[currency]))
            currency.getSymbol(currencyLocaleMap[currency])
        } catch (e: java.lang.Exception) {
            currencyCode
        }
    }

}

fun Int.todp(): Int {
    return this * Resources.getSystem().displayMetrics.density.toInt()
}

fun Float?.toVoltage(): String {
    if (this == null || this.isNaN() || this == 0f)
        return "--"
    return this.toString().plus("V")
}

fun Float?.toCurrent(): String {

    if (this == null || this.isNaN() || this == 0f)
        return "--"
    return this.toString().plus("A")
}

fun Float?.toEngery(): String {

    if (this == null || this.isNaN() || this == 0f)
        return "--"
    return this.div(1000).toString().plus("kWh")
}


/**
 *
 * 密码加密
 *
 */


fun String.toMD5(): String {

//           加密
    //           加密
    val builder = StringBuilder()
    var md: MessageDigest? = null
    try {
        md = MessageDigest.getInstance("MD5")
        md.update(this.toByteArray())
        val bytes = md.digest()
        for (b in bytes) builder.append(String.format("%02x", b))
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    val result = builder.toString()
    return result
}

fun String?.RemainOne(): String {
    return try {
        val f = this?.toDouble()!!
        val format = BigDecimal(f)
        format.setScale(1, BigDecimal.ROUND_HALF_UP).toString()
    } catch (e: java.lang.Exception) {
        ""
    }
}

fun String.toDateUTC(): String {
    return DateUtil.stringToUTC(this)
}

fun String.dateUTCToString(): String {
    return DateUtil.UTCToString(this)
}

/**
 *
//Available, Finishing; Cannot select: Charging ,Unavailable ,Faulted, Offline
/// 可以选： 空闲，已插入，完成, 准备中, 有效, 完成中；不可选择：正在充电，不可用，故障, 离线
/// 如果是远程启动，只有 Plugged 才可以点击启动

 */
enum class gunStatus(var s: String) {
    Idle("Idle"),
    Plugged("Plugged"),
    Finished("Finished"),
    Preparing("Preparing"),
    Charging("Charging"),
    Unavailable("Unavailable"),
    Finishing("Finishing"),
    Faulted("Faulted"),
    Available("Available"),
    Offline("Offline");

    fun canSelect(str: String?): Boolean {
        if (str == null)
            return false
        return str == Idle.name || str == Plugged.name
                || str == Finished.name || str == Preparing.name
                || str == Available.name || str == Finishing.name
    }

    fun canRemoteSelect(str: String?): Boolean {
        if (str == null)
            return false
        return str == Plugged.name
    }

}


/**
 *

s == 单位设置 （米，英里）

 */
enum class Distance(var s: Int) {
    km20(0),
    km50(1),
    km100(2),
    km200(3);


}


fun Context.getDistanceList(): ArrayList<String> {
    val list = ArrayList<String>()
    if (Configs.DISTANCE_MI == this.getHomeDistance()) {
        list.add("20Mi nearby")
        list.add("50Mi nearby")
        list.add("100Mi nearby")
        list.add("200Mi nearby")

    } else {
        list.add("20Km nearby")
        list.add("50Km nearby")
        list.add("100Km nearby")
        list.add("200Km nearby")
    }
    return list
}

fun Context.getDistance(p: Float): String {
    return if (
        Configs.DISTANCE_MI == this.getHomeDistance()
    ) {
        p.div(1000).toString().plus("Mi nearby")


    } else {
        p.div(1000).toString().plus("Km nearby")

    }

}

fun Context.getDistanceString(p: Int): String {
    val km = when (p) {
        0 -> {
            20
        }
        1 -> {
            50
        }
        2 -> {
            100
        }
        3 -> {
            200
        }
        else -> {
            p.div(1000)
        }
    }
    return if (Configs.DISTANCE_MI == getHomeDistance()) {

//            英里
        "${km * 1000 * 1.6}"

    } else {
        "${km * 1000}"

    }
}


fun ImageView.loadImg(img: Any?) {
    GlideApp.with(context).load(img).fitCenter().placeholder(R.color.grey).into(this)
}


fun String.socketTypeIcon(): Int? {
    return when (this) {
        "Type1", "AC" -> R.drawable.icon_socket_type1
        "Type2" -> R.drawable.icon_socket_type2
        "CCS1" -> R.drawable.icon_socket_ccs1
        "CCS2" -> R.drawable.icon_socket_ccs2
        "CHAdeMO" -> R.drawable.icon_socket_chademo
        else -> null
    }
}
fun String.socketTypeIconFalse(): Int? {
    return when (this) {
        "Type1", "AC" -> R.drawable.icon_socket_type1_1
        "Type2" -> R.drawable.icon_socket_type2_1
        "CCS1" -> R.drawable.icon_socket_ccs1_1
        "CCS2" -> R.drawable.icon_socket_ccs2_1
        "CHAdeMO" -> R.drawable.icon_socket_chademo_1
        else -> null
    }
}

fun String.socketTypeIsAc(): Boolean {
    return when (this) {
        "GBT AC" -> true
        else -> false
    }
}

fun String?.diviceTypeTextcolor(): Int {

    return when (this?.toLowerCase(Locale.ENGLISH)) {
        "idle" -> R.color.idle
        "plugged" -> R.color.plugged
        "finished" -> R.color.finished
        "charging" -> R.color.charging
        "unavailable" -> R.color.unavailable
        "faulted" -> R.color.faulted
        "reserved" -> R.color.reserved
        else -> R.color.color_8F9293
    }
}

fun String?.statusStrRes(): Int {

    return when (this?.toLowerCase(Locale.ENGLISH)) {
        "idle" -> R.string.status_type_idle
        "plugged" -> R.string.status_type_Plugged
        "finished" -> R.string.status_type_Finished
        "charging" -> R.string.status_type_Charging
        "unavailable" -> R.string.status_type_Unavailable
        "faulted" -> R.string.status_type_Faulted
        "reserved" -> R.string.status_type_Reserved
        else -> R.string.status_type_Offline
    }
}

fun String?.diviceTypebgcolor(): Int {
    return when (this?.toLowerCase(Locale.ENGLISH)) {
        "idle" -> R.color.idle20
        "plugged" -> R.color.plugged20
        "finished" -> R.color.finished20
        "charging" -> R.color.charging20
        "unavailable" -> R.color.unavailable20
        "faulted" -> R.color.faulted20
        "reserved" -> R.color.reserved20
        else -> R.color.station_color_8F9293
    }
}

fun String?.homeDiviceTypebgcolor(): Int {
    return when (this?.toLowerCase(Locale.ENGLISH)) {
        "idle", "faulted", "unavailable", "offline", "charging" -> R.color.colorTheme_a20
        else -> R.color.colorTheme
    }
}

fun String?.homeDiviceTypeCanStart(): Boolean {
    return when (this?.toLowerCase(Locale.ENGLISH)) {
        "idle", "faulted", "unavailable", "offline", "charging" -> false
        else -> true
    }
}

fun String?.homeDiviceTypeCanStartShow(): Boolean {
    return when (this?.toLowerCase(Locale.ENGLISH)) {
        "charging" -> false
        else -> true
    }
}

fun Context.jumpSystemWebview(link: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun String.dateFormat(): String {
    return this
}

fun Context.jumpMap(item: StationListBean) {
    if (item != null) {
        var url: String = if (item!!.latitude != null)
            "https://www.google.com/maps/search/?api=1&query=${item?.latitude},${item?.longitude}"
        else {
            val name = URLEncoder.encode(item?.operatorName, "utf-8")
            val address = URLEncoder.encode(item?.street, "utf8")
            "https://www.google.com/maps/search/?api=1&query=$name,$address"

        }
//                    url=URLEncoder.encode(url,"UTF-8")
        val gmmIntentUri = Uri.parse(url)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"
        try {
//            var hasMap = false
//                AppUtils.getAppsInfo().forEach {
//                    hasMap = it.packageName == "com.google.android.apps.maps"
//                }
//            val s=1/0
//            if (hasMap)
//            ContextCompat.startActivity(this, mapIntent, null)
//            else
            startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "something error", Toast.LENGTH_SHORT).show()
        }
    }
}


fun SimpleDraweeView.setImageIsWifi(str: String?) {
    try {
        if (!App.appContext.getIsWifiLoad()) {
            if (NetworkUtils.isWifiAvailable()) {
                this.setImageURI(str)
            }
        } else {
            this.setImageURI(str)
        }
    } catch (e: java.lang.Exception) {
        this.setImageURI(str)
    }

}

fun SimpleDraweeView.setImageIsWifi(str: Uri?) {
    try {
        if (!App.appContext.getIsWifiLoad()) {
            if (NetworkUtils.isWifiAvailable()) {
                this.setImageURI(str)
            }
        } else {
            this.setImageURI(str)
        }
    } catch (e: java.lang.Exception) {
        this.setImageURI(str)
    }
}

fun SimpleDraweeView.setImageIsWifi(str: Int) {
    try {
        if (!App.appContext.getIsWifiLoad()) {
            if (NetworkUtils.isWifiAvailable()) {
                this.setImageResource(str)
            }
        } else {
            this.setImageResource(str)
        }
    } catch (e: java.lang.Exception) {
        this.setImageResource(str)
    }
}

fun randomColor(alpha: Int): Int {
    var alpha = alpha
    val rnd = Random()
    alpha = Math.min(Math.max(1, alpha), 255)
    return Color.argb(alpha, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255))
}

@Throws(PatternSyntaxException::class)
fun isChinaPhoneLegal(str: String?): Boolean {
    val regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$"
    val p: Pattern = Pattern.compile(regExp)
    val m: Matcher = p.matcher(str)
    return m.matches()
}

fun isPassword(password: String?): Boolean {
    val regex = "^.*(?=.{8,})(?=.*\\d)(?=.*[A-Z])(?=.*[a-z]).*\$"
    val p: Pattern = Pattern.compile(regex)
    val m: Matcher = p.matcher(password)
    val isMatch: Boolean = m.matches()
    return isMatch
}

fun isEmail(email: String?): Boolean {
    if (null == email || "" == email) return false
//    val p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
    val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*") //复杂匹配
    val m = p.matcher(email)
    return m.matches()
}
//fun  toUserAgreement(context: Context){
//    var intent=Intent(context, WebViewActivity::class.java)
//    intent.putExtra("title","User Agreement")
//    intent.putExtra("url",Configs.BASE_URL1+"terms/userAgreement.html")
//    context.startActivity(intent)
//}

fun  toUserAgreement(context: Context){
    var intent=Intent(context, PdfviewActivity::class.java)
    intent.putExtra("title","User Agreement")
    intent.putExtra("url","UserTerms.pdf")
    context.startActivity(intent)
}
fun  toPrivacyPolicy(context: Context){
    var intent=Intent(context, PdfviewActivity::class.java)
    intent.putExtra("title","Privacy Policy")
    intent.putExtra("url","PrivacyPolicy.pdf")
    context.startActivity(intent)
}

