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
package com.evport.businessapp.data.repository

import androidx.lifecycle.MutableLiveData
import com.evport.businessapp.data.bean.*

/**
 * Create by KunMinX at 19/10/29
 */
interface IRemoteSource {
    fun getFreeMusic(liveData: MutableLiveData<TestAlbum>)
    fun getLibraryInfo(liveData: MutableLiveData<List<LibraryInfo>>)
    fun downloadFile(liveData: MutableLiveData<DownloadFile>)
    //    my
//    account
    fun login(
        user: User,
        liveData: MutableLiveData<User>
    )

    fun signUp(
        user: User,
        liveData: MutableLiveData<User>
    )

    fun getCode(
        email: String,
        liveData: MutableLiveData<User>
    )

    fun getCodeResetPwd(
        email: String,
        liveData: MutableLiveData<User>
    )

    fun forgetPwd(
        user: User,
        liveData: MutableLiveData<User>
    )

    fun changePwd(
        changePassword: ChangePassword,
        liveData: MutableLiveData<ChangePassword>
    )

    fun checkName(
        name: String,
        liveData: MutableLiveData<User>
    )

    //    chargeSetting
    fun getChargeList(liveData: MutableLiveData<MutableList<ChargeSetting>>)

    fun bindFamily(body: RequestScan, liveData: MutableLiveData<Boolean>)

    fun unbingfamily(body: RequestScan, liveData: MutableLiveData<Boolean>)

    fun chargeSetting(
        chargeSetting: ChargeSetting,
        liveData: MutableLiveData<ChargeSetting>
    )

    //    stats
    fun getStats(
        requestStats: RequestStats,
        liveData: MutableLiveData<StatsDataResp>
    )

    //    record
    fun getRecords(
        requestRecord: RequestRecord,
        liveData: MutableLiveData<MutableList<RecordResp>>
    )


    //    scan
    fun getScan(
        requestScan: RequestScan,
        liveData: MutableLiveData<Boolean>
    )

    //    chargePoint
    fun getChargePoints(liveData: MutableLiveData<MutableList<ChargeSetting>>)

    //    chargeGun
    fun getChargeGuns(requestScan: RequestScan, liveData: MutableLiveData<MutableList<GunResp>>)


    //
    fun getCheckTransactions(liveData: MutableLiveData<MutableList<CheckTransaction>>)

    //
    fun setChargingSchedule(requestCharge: RequestCharge, liveData: MutableLiveData<String>)

    //
    fun setChargingScheduleStart(requestCharge: RequestCharge, liveData: MutableLiveData<String>)

    //
    fun setChargingScheduleUpdate(requestCharge: RequestCharge, liveData: MutableLiveData<String>)

    //
    fun setChargingScheduleDelete(requestCharge: RequestCharge, liveData: MutableLiveData<String>)

    //
    fun setAutoSwitch(requestChargeChange: RequestChargeChange, liveData: MutableLiveData<String>)

    //
    fun remoteStart(requestChargeChange: RequestChargeChange, liveData: MutableLiveData<String>)

    //
    fun remoteStop(requestChargeChange: RequestChargeChange, liveData: MutableLiveData<String>)


}