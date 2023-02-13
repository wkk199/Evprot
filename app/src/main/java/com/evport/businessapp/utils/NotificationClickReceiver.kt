package com.evport.businessapp.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal
import com.evport.businessapp.MainActivity
import com.evport.businessapp.data.bean.EventBean


class NotificationClickOpenedHandler(val mContext: Context) :
    OneSignal.OSNotificationOpenedHandler {


//    lateinit var mContext :Context
//
//    constructor(context: Context) : this() {
//        mContext =context
//    }

    override fun notificationOpened(result: OSNotificationOpenedResult?) {
        result?.apply {
            notification.additionalData?.let {
                try {


                    val data = notification.additionalData.toString()
                    Log.e("notificationClick,data:", data)
                    val str = notification.additionalData.get("type").toString()

                    var pk = when (str) {
                        "sysMessage" -> notification.additionalData.get("sourcePk").toString()
                        "stopCharging" -> notification.additionalData.get("paramPk").toString()
                        "comment", "reply" -> ""
                        else -> {
                            return@let
                        }
                    }
                    val intent = Intent(mContext, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.putExtra("desc", str)
//                    intent.putExtra("pk", pk)
                    mContext.startActivity(intent)
                    LiveBus.getInstance().post(EventBean(str,pk,data))
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

}