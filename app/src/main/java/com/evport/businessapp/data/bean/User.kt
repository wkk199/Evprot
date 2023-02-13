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
package com.evport.businessapp.data.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * Create by KunMinX at 20/04/26
 */
@Keep
@Parcelize
data class User(
    var userPk: String? = null,
    var homeMemberPk: String? = null,
    var name: String? = null,
    var userName: String? = null,
    var account: String? = null,
    var password: String? = null,
    var email: String? = null,
    var ssoticket: String? = null,//token
    var sex: String? = null,//female ,male
    var birthDay: String? = null,
    var birthday: String? = null,//-
    var phone: String? = null,
    var areaCode: String? = null,
    var avatarUrl: String? = null,//-
    var note //验证码 - 注册使用
    : String? = null,
    var code //验证码 - 忘记密码使用
    : String? = null,
    var loginWay: String? = null

) : Parcelable {
    fun birthDayStr(): String {
        return ""
//        return DateUtil.longToStringEN(DateUtil.stringToLong(birthday))
    }
}

@Keep
data class ChangePassword(
    var oldPassword: String? = null,
    var newPassword: String? = null
)

@Keep
data class RemoteData(
    val api: String? = null,
    val message: String? = null,

    var connectorId: String? = null,
    val chargeboxId: String? = null,
    val percent: String? = null,

    val reason: String? = null
)

@Keep
data class RemoteResp(

    val code: String? = null,
    val data: RemoteData? = null,
    val success: Boolean? = null,
    val message: String? = null
)

@Keep
data class Code(

    val name: String? = null,
    val dial_code: String,
    val code: String? = null,
    val format: String? = null
)

@Keep
data class Image(

    val img: String? = null
)

@Keep
data class MessageInfo(
    val systemMessage: String? = null,
    val systemMessageUnreadCount: Int = 0,
    val feedbackMessage: String? = null,
    val feedbackMessageUnreadCount: Int = 0,
    val myCommentMessage: String? = null,
    val replyToMineMessage: String? = null,
    val replyToMineUnreadCount: Int = 0
)

@Keep
data class MessageData(

    val systemMessagePk: String? = null,
    val commentsReplyPk: String? = null,
    val feedbackPk: String? = null
)

@Keep
data class UpdateInfoReq(
    var sex: String? = null,
    var birthday: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var userName: String? = null,
    var name: String? = null,
)