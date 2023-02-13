/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evport.businessapp.data.config

import android.os.Environment
import androidx.annotation.Keep
import com.kunminx.architecture.utils.Utils

/**
 * Create by KunMinX at 18/9/28
 */
@Keep
object Configs {
    @JvmField
    val COVER_PATH = Utils.getApp()
        .getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath

//      测试 宝鑫地址
//       var BASE_URL = "https://170224d7l7.zicp.fun/"
//       var WEB_BASE_URL = "https://170224d7l7.zicp.fun/"

    //测试core 地址
    var BASE_URL = "https://170s2247n7.51mypc.cn/"
    var WEB_BASE_URL = "wss://170s2247n7.51mypc.cn/"

    //正式
//    var BASE_URL = "http://111.38.221.233:9080/"
//    var WEB_BASE_URL = "http://111.38.221.233:9080/"
    var BASE_URL1 = "http://111.38.221.233:81/"

//    var BASE_URL = "http://192.168.3.47:8090/"
//    var WEB_BASE_URL = "wss://192.168.3.47:8090/"

    //    var BASE_URL = "http://47.99.89.175:8090/"
//    var WEB_BASE_URL = "ws://47.99.89.175:8090/"
    const val TOKEN = "token"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val NAME = "name"
    const val HOST = "host"
    const val LANGUAGE = "LANGUAGE"
    const val EVENT_LOGOUT = "EVENT_LOGOUT"
    const val EVENT_WHITELIST = "EVENT_WHITELIST"
    const val CODE_SINGUP = 1
    const val CODE_RESETPWD = 2

    //   距离单位
    const val DISTANCE_MI = 1
    const val DISTANCE_MITER = 0

    const val BUNDLE_CS = "BUNDLE_CS"

    const val NOTIFICATION_MSG = "notification_msg"

    const val REFRESH_BALANCE = "REFRESH_BALANCE"
    const val WX_CHAT_PAY = "WX_CHAT_PAY"
    const val RECODE = "recode"
    const val APP_ID = "wxc5d62e543ba85693"
    const val COMPANY_INFO = "COMPANY_INFO"
    const val INVOICUNG_REFRESH = "INVOICUNG_REFRESH"
    const val INVOICUNG_HISTORY = "INVOICUNG_HISTORY"
}