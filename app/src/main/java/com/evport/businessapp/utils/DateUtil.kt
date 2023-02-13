package com.evport.businessapp.utils


import android.util.Log
import com.blankj.utilcode.constant.TimeConstants
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


object DateUtil {

    fun stringToLong(date: String?): Long {
        if (date.isNullOrBlank())
            return 0

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.parse(date)

        return tempDate.time
    }

    fun stringToDate(date: String?): Date {
        if (date.isNullOrBlank())
            return Date()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.parse(date)

        return tempDate
    }

    fun stringToLongHMS(date: String): Long {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val tempDate = simpleDateFormat.parse(date)
        return tempDate.time
    }


    fun formatTimeStamp(t: String?): Long {
        if (t.isNullOrEmpty()) {
            return 0
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val date = simpleDateFormat.parse(t)
        return date.time
    }

    fun formatTimeStampStr(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        val long = formatTimeStamp(t)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val date = simpleDateFormat.format(Date(long))
        return date
    }


    fun formatTimeDef(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
//        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val simpleDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.DEFAULT)
        val date = simpleDateFormat.format(Date(stringToLong(t)))
        return formatTime12H(t)
    }

    fun formatTime12H(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }

        val lo = Locale.getDefault()

        val simpleDateFormatTime = SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH)
        val simpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)


        val date = simpleDateFormat.format(Date(stringToLong(t)))
        val dateT = simpleDateFormatTime.format(Date(stringToLong(t)))
        return date.plus(" ").plus(dateT)
    }


    fun stringToUTC(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val simpleDateFormatIn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val simpleDateFormatIn = DateFormat.getDateTimeInstance()
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val timeZone = TimeZone.getTimeZone("UTC")
        simpleDateFormat.timeZone = timeZone

        val dateIn = simpleDateFormatIn.parse(t)
        val date = simpleDateFormat.format(dateIn)
        return date
    }

    fun stringToString(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val simpleDateFormatIn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateIn = simpleDateFormatIn.parse(t)
        val date = simpleDateFormat.format(dateIn)
        return date
    }

    //    utc 转当地时间 用于展示
    fun UTCToString(t: String?): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        try {

            val simpleDateFormatU = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            val simpleDateFormatU = DateFormat.getDateTimeInstance()

            val timeZoneU = TimeZone.getTimeZone("UTC")
            simpleDateFormatU.timeZone = timeZoneU
            val dateU = simpleDateFormatU.parse(t)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            val simpleDateFormat = DateFormat.getDateTimeInstance()
            simpleDateFormat.timeZone = TimeZone.getDefault()
            val date = simpleDateFormat.format(dateU)
            return date
        } catch (e: Exception) {
            return ""
        }
    }

    fun longToString(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }


    fun longToStringSlash(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }


    fun longToStringYM(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }

    fun longToStringY(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }


    fun longToStringEN(long: Long): String {

        val simpleDateFormat = SimpleDateFormat("MMM dd,yyyy", Locale.getDefault())
//        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate
    }

    fun longToStringDetail(long: Long): String {
        if (long == 0L) {
            return ""
        }
        val simpleDateFormat = DateFormat.getDateTimeInstance()
        val tempDate = simpleDateFormat.format(Date(long))
        return tempDate

    }


    /**
     * 获取前n天日期、后n天日期
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    fun getOldDate(distanceDay: Int): String {
        val dft = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
        val beginDate = Date()
        val date = Calendar.getInstance()
        date.time = beginDate
        date[Calendar.DATE] = date[Calendar.DATE] + distanceDay
        var endDate: Date = Date()
        try {
            endDate = dft.parse(dft.format(date.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dft.format(endDate)
    }

    /**
     * 判断是否是今天以前
     */
    fun isBeforeToday(t1: Long): Boolean {
        val sf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d1 = sf.format(Date(t1))
        val d2 = sf.format(Date())
        return d1 < d2

    }

    fun isBeforeNow(t1: Long): Boolean {
        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        val d1 = sf.format(Date(t1))
        val d2 = sf.format(Date())
        Log.e("d1:", d1)
        Log.e("d2:", d2)
        return d1 < d2
    }

    fun isAbsNow2Min(t1: String): Boolean {
        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        val timeZone = TimeZone.getTimeZone("UTC")
        sf.timeZone = timeZone
        val d1 = stringToLong(t1)
        val d2 = stringToLong(sf.format(Date()))
        Log.e("d1:", "" + d1 + "  " + t1)
        Log.e("d2:", d2.toString() + "  " + sf.format(Date()))
        Log.e("d2-d1", abs(d2.minus(d1)).toString())
        return abs(d2.minus(d1)) > 120 * 1000
    }

    fun getNow(): String {
//        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        val sf = DateFormat.getDateTimeInstance()
        val d2 = sf.format(Date(System.currentTimeMillis()))
        Log.e("d2:", d2)
        return d2

    }

    fun getDay(t: String): String {
        if (t.isNullOrEmpty()) {
            return ""
        }
        val lo = Locale.getDefault()
        val simpleDateFormatTime = SimpleDateFormat(" a", Locale.ENGLISH)
        val simpleDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)


        val date = simpleDateFormat.format(Date(stringToLong(t)))
        val dateT = simpleDateFormatTime.format(Date(stringToLong(t)))
        return date.plus(" ").plus(dateT)
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */

    fun containNowDate(startTime: String?, endTime: String?): Boolean {
        if (startTime.isNullOrEmpty())
            return false
        if (endTime.isNullOrEmpty())
            return false
        val sf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = sf.format(Date(System.currentTimeMillis()))
        return stringToLongHMS(now) <= stringToLongHMS(endTime) && stringToLongHMS(now) > stringToLongHMS(
            startTime
        )
    }

    fun getFriendlyTimeSpanByNow(millis: String?): String? {
        if (millis == null)
            return ""
        val now = System.currentTimeMillis()
        var span = 0L
        try {
            span = now - stringToLong(UTCToString(millis))
        } catch (e: Exception) {
            return millis
        }
        val mon = 1000 * 60 * 60 * 24 * 30L
        val year = mon * 12L
//        if (span < 0) //
//            return String.format("%t", millis)
        if (span < 1000) {
            return "just recently"
        } else if (span < TimeConstants.MIN) {
            return String.format(
                Locale.getDefault(),
                "%d seconds ago",
                span / TimeConstants.SEC
            )
        } else if (span < TimeConstants.HOUR) {
            return String.format(
                Locale.getDefault(),
                "%d minutes ago",
                span / TimeConstants.MIN
            )
        } else if (span < TimeConstants.DAY) {
            return if ((span / TimeConstants.HOUR).toInt() == 1)
                "a hour ago"
            else
                String.format(
                    Locale.getDefault(),
                    "%d hours ago",
                    span / TimeConstants.HOUR
                )
        } else if (span < mon) {
            return if ((span / TimeConstants.DAY).toInt() == 1)
                "a day ago"
            else String.format(
                Locale.getDefault(),
                "%d days ago",
                span / TimeConstants.DAY
            )
        } else if (span < year) {
            return if ((span / mon).toInt() == 1)
                "a month ago"
            else String.format(
                Locale.getDefault(),
                "%d months ago",
                span / mon
            )

        } else {
            return if ((span / year).toInt() == 1)
                "a year ago"
            else
                String.format(
                    Locale.getDefault(),
                    "%d years ago",
                    span / year
                )
        }
    }

    fun getNowDay(): String {
        val sf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sf.format(Date().time)
    }
    fun getNowYear(): String {
        val sf = SimpleDateFormat("yyyy", Locale.getDefault())
        return sf.format(Date().time)
    }

    fun getCurrDate(pattern: String): String {
        val sf = SimpleDateFormat(pattern, Locale.getDefault())
        return sf.format(Date(System.currentTimeMillis()))
    }
    fun getNowTime(): String {
        val sf = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault())
        return sf.format(Date().time)
    }
}