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

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.kunminx.architecture.domain.manager.NetState
import com.kunminx.architecture.domain.manager.NetworkStateManager
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.R
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import io.reactivex.Observable
import java.util.*


/**
 * Create by KunMinX at 19/10/29
 */
class DataRepository : ILocalSource, IRemoteSource {

//    todo 添加 progress


    companion object {
        val instance = DataRepository()
    }

    override fun getFreeMusic(liveData: MutableLiveData<TestAlbum>) {
//        val gson = Gson()
//        val type = object : TypeToken<TestAlbum?>() {}.type
//        val testAlbum = gson.fromJson<TestAlbum>(
//            Utils.getApp().getString(R.string.free_music_json),
//            type
//        )
//        liveData.value = testAlbum
    }

    override fun getLibraryInfo(liveData: MutableLiveData<List<LibraryInfo>>) {
//        val gson = Gson()
//        val type =
//            object : TypeToken<List<LibraryInfo?>?>() {}.type
//        val list =
//            gson.fromJson<List<LibraryInfo>>(
//                Utils.getApp().getString(R.string.library_json), type
//            )

    }

    /**
     * TODO：模拟下载任务:
     * 可分别用于 普通的请求，和可跟随页面生命周期叫停的请求，
     * 具体可见 ViewModel 和 UseCase 中的使用。
     *
     * @param liveData 从 Request-ViewModel 或 UseCase 注入 LiveData，用于 控制流程、回传进度、回传文件
     */
    override fun downloadFile(liveData: MutableLiveData<DownloadFile>) {
        val timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() { //模拟下载，假设下载一个文件要 10秒、每 100 毫秒下载 1% 并通知 UI 层
                var downloadFile = liveData.value
                if (downloadFile == null) {
                    downloadFile = DownloadFile()
                }
                if (downloadFile.progress < 100) {
                    downloadFile.progress = downloadFile.progress + 1
                    Log.d("TAG", "下载进度 " + downloadFile.progress + "%")
                } else {
                    timer.cancel()
                    downloadFile.progress = 0
                    return
                }
                if (downloadFile.isForgive) {
                    timer.cancel()
                    downloadFile.progress = 0
                    downloadFile.isForgive = false
                    return
                }
                liveData.postValue(downloadFile)
                downloadFile(liveData)
            }
        }
        timer.schedule(task, 100)
    }

    /**
     * TODO 模拟登录的网络请求
     *
     */
    override fun login(
        user: User,
        liveData: MutableLiveData<User>
    ) {
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {
                liveData.postValue(data)
            }

            override fun onFailure(message: String) {

                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance().networkStateCallback.postValue(NetState("login"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
//                return SingletonFactory.apiService.usersAuth(mapOf(Pair("user", user)))
                return SingletonFactory.apiService.usersAuth(user)
            }
        }
    }


    override fun signUp(
        user: User,
        liveData: MutableLiveData<User>
    ) {

        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {
                liveData.postValue(data)
            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance().networkStateCallback.postValue(NetState("singup"))

                if(message.isNotBlank())
                    ToastUtils.showLong(message)

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
//                return SingletonFactory.apiService.usersAuth(mapOf(Pair("user", user)))
                return SingletonFactory.apiService.sinUp(user)
            }
        }

    }

    override fun chargeSetting(
        chargeSetting: ChargeSetting,
        liveData: MutableLiveData<ChargeSetting>
    ) {

        object : NetworkBoundResource<ChargeSetting>(networkStatusCallback = object :
            NetworkStatusCallback<ChargeSetting> {

            override fun onSuccess(data: ChargeSetting?) {
                if (data == null) {
                    liveData.postValue(ChargeSetting("", "", "", "", ""))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("chargeSetting"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ChargeSetting>> {
                return SingletonFactory.apiService.chargeSet(chargeSetting)
            }
        }
    }

    override fun getChargeList(liveData: MutableLiveData<MutableList<ChargeSetting>>) {

        object : NetworkBoundResource<MutableList<ChargeSetting>>(networkStatusCallback = object :
            NetworkStatusCallback<MutableList<ChargeSetting>> {

            override fun onSuccess(data: MutableList<ChargeSetting>?) {
                if (data == null) {
                    liveData.postValue(arrayListOf())
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getChargeList"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<ChargeSetting>>> {
                return SingletonFactory.apiService.getChargeSettingList()
            }
        }
    }

    override fun bindFamily(body: RequestScan, liveData: MutableLiveData<Boolean>) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                if (data == null) {
                    liveData.postValue(true)
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(true)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getChargeList"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.bindFamily(body)
            }
        }
    }

    override fun unbingfamily(body: RequestScan, liveData: MutableLiveData<Boolean>) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                if (data == null) {
                    liveData.postValue(true)
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(true)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getChargeList"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.unBindFamily(body)
            }
        }

    }


    override fun getCode(email: String, liveData: MutableLiveData<User>) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
//                liveData.postValue(User())
                ToastUtils.showLong(data)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCode"))

            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCode"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getCode(User(email = email))
            }
        }

    }


    override fun getCodeResetPwd(email: String, liveData: MutableLiveData<User>) {
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                liveData.postValue(User())
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCode"))

            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCode"))
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getCodeResetpwd(mapOf(Pair("email", email)))
            }
        }
    }

    override fun checkName(name: String, liveData: MutableLiveData<User>) {
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {
                liveData.postValue(data)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("checkName"))

            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("checkName"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.checkName(mapOf(Pair("username", name)))
            }
        }

    }

    override fun forgetPwd(user: User, liveData: MutableLiveData<User>) {
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {
                if (data == null) {
                    liveData.postValue(User())
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {

                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCodeResetPwd"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.forgetPassword(user)
            }
        }
    }

    override fun changePwd(
        changePassword: ChangePassword,
        liveData: MutableLiveData<ChangePassword>
    ) {

        object : NetworkBoundResource<ChangePassword>(networkStatusCallback = object :
            NetworkStatusCallback<ChangePassword> {

            override fun onSuccess(data: ChangePassword?) {
                if (data == null) {
                    liveData.postValue(ChangePassword())
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCodeResetPwd"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ChangePassword>> {
                return SingletonFactory.apiService.changePassword(changePassword)
            }
        }
    }

    override fun getStats(
        requestStats: RequestStats,
        liveData: MutableLiveData<StatsDataResp>
    ) {

        object : NetworkBoundResource<StatsDataResp>(networkStatusCallback = object :
            NetworkStatusCallback<StatsDataResp> {

            override fun onSuccess(data: StatsDataResp?) {
                if (data == null) {
                    liveData.postValue(StatsDataResp(mutableListOf(), mutableListOf(),"0","0","","0"))
//                    ToastUtils.showLong("no data")
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCodeResetPwd"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<StatsDataResp>> {
                return SingletonFactory.apiService.stats(requestStats)
            }
        }
    }

    override fun getRecords(
        requestRecord: RequestRecord,
        liveData: MutableLiveData<MutableList<RecordResp>>
    ) {

        object : NetworkBoundResource<MutableList<RecordResp>>(networkStatusCallback = object :
            NetworkStatusCallback<MutableList<RecordResp>> {

            override fun onSuccess(data: MutableList<RecordResp>?) {
                if (data.isNullOrEmpty()) {
                    liveData.postValue(mutableListOf())
//                    ToastUtils.showLong("no data")
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCodeResetPwd"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<RecordResp>>> {
                return SingletonFactory.apiService.records(requestRecord)
            }
        }

    }

    override fun getScan(requestScan: RequestScan, liveData: MutableLiveData<Boolean>) {
        object : NetworkBoundResource<Boolean>(networkStatusCallback = object :
            NetworkStatusCallback<Boolean> {

            override fun onSuccess(data: Boolean?) {
                if (data == null) {
                    liveData.postValue(null)
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCodeResetPwd"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Boolean>> {
                return SingletonFactory.apiService.scan(requestScan)
            }
        }

    }

    override fun getChargePoints(liveData: MutableLiveData<MutableList<ChargeSetting>>) {


        object : NetworkBoundResource<MutableList<ChargeSetting>>(networkStatusCallback = object :
            NetworkStatusCallback<MutableList<ChargeSetting>> {

            override fun onSuccess(data: MutableList<ChargeSetting>?) {
                if (data == null) {
                    liveData.postValue(arrayListOf())
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getChargePoints"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<ChargeSetting>>> {
                return SingletonFactory.apiService.getChargePoint()
            }
        }

    }

    override fun getChargeGuns(
        requestScan: RequestScan,
        liveData: MutableLiveData<MutableList<GunResp>>
    ) {

        object : NetworkBoundResource<MutableList<GunResp>>(networkStatusCallback = object :
            NetworkStatusCallback<MutableList<GunResp>> {

            override fun onSuccess(data: MutableList<GunResp>?) {
                if (data == null) {
                    liveData.postValue(arrayListOf())
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("getCodeResetPwd"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<GunResp>>> {
                return SingletonFactory.apiService.getChargegun(requestScan)
            }
        }

    }

    override fun getCheckTransactions(liveData: MutableLiveData<MutableList<CheckTransaction>>) {

        object :
            NetworkBoundResource<MutableList<CheckTransaction>>(networkStatusCallback = object :
                NetworkStatusCallback<MutableList<CheckTransaction>> {

                override fun onSuccess(data: MutableList<CheckTransaction>?) {
                    if (data == null) {
                        liveData.postValue(arrayListOf())
                        ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                    } else {
                        if (data!=null){
                          //  Log.e("hm----charging",Gson().toJson(data)+"FFF");
                            liveData.postValue(data)
                        }

                    }
                }

                override fun onFailure(message: String) {
                    if(message.isNotBlank())
                        ToastUtils.showLong(message)
                    NetworkStateManager.getInstance()
                        .networkStateCallback.postValue(NetState("getCodeResetPwd"))

                }

            }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<CheckTransaction>>> {
                return SingletonFactory.apiService.getCheckTransactions(ReqDefault())
            }
        }
    }

     fun getCheckTransactions(pk:String,liveData: MutableLiveData<MutableList<CheckTransaction>>) {

        object :
            NetworkBoundResource<MutableList<CheckTransaction>>(networkStatusCallback = object :
                NetworkStatusCallback<MutableList<CheckTransaction>> {

                override fun onSuccess(data: MutableList<CheckTransaction>?) {
                    if (data == null) {
                        liveData.postValue(arrayListOf())
                        ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                    } else {
                        liveData.postValue(data)
                    }
                }

                override fun onFailure(message: String) {
                    if(message.isNotBlank())
                    ToastUtils.showLong(message)
                    NetworkStateManager.getInstance()
                        .networkStateCallback.postValue(NetState("getCodeResetPwd"))

                }

            }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<CheckTransaction>>> {
                return SingletonFactory.apiService.getCheckTransactions(ReqDefaultPk(transactionPk = pk))
            }
        }
    }


    override fun setChargingSchedule(
        requestCharge: RequestCharge,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("setChargingSchedule"))


            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.setChargingSchedule(requestCharge)
            }
        }
    }

    override fun setChargingScheduleStart(
        requestCharge: RequestCharge,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("setChargingScheduleStart"))

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.setChargingScheduleStart(requestCharge)
            }
        }
    }

    override fun setChargingScheduleUpdate(
        requestCharge: RequestCharge,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("setChargingScheduleUpdate"))
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.setChargingScheduleUpdate(requestCharge)
            }
        }
    }

    override fun setChargingScheduleDelete(
        requestCharge: RequestCharge,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("setChargingScheduleDelete"))
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.setChargingScheduleDelete(requestCharge)
            }
        }
    }

    override fun setAutoSwitch(
        requestChargeChange: RequestChargeChange,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("setAutoSwitch"))
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.setAutoSwitch(requestChargeChange)
            }
        }
    }

    override fun remoteStart(
        requestChargeChange: RequestChargeChange,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }

            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("remoteStart"))
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.remoteStart(requestChargeChange)
            }
        }
    }

    override fun remoteStop(
        requestChargeChange: RequestChargeChange,
        liveData: MutableLiveData<String>
    ) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                if (data == null) {
                    liveData.postValue(Utils.getApp().resources.getString(R.string.success))
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("remoteStop"))
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.remoteStop(requestChargeChange)
            }
        }
    }

    fun getOperator(

        liveData: MutableLiveData<ArrayList<SocketType>>
    ) {

        object : NetworkBoundResource<ArrayList<SocketType>>(networkStatusCallback = object :
            NetworkStatusCallback<ArrayList<SocketType>> {

            override fun onSuccess(data: ArrayList<SocketType>?) {
                if (data == null) {
                    liveData.postValue(arrayListOf())
                    ToastUtils.showLong(Utils.getApp().resources.getString(R.string.success))
                } else {
                    liveData.postValue(data)
                }
            }

            override fun onFailure(message: String) {
                NetworkStateManager.getInstance()
                    .networkStateCallback.postValue(NetState("remoteStop"))
                if(message.isNotBlank())
                    ToastUtils.showLong(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<SocketType>>> {
                return SingletonFactory.apiService.getOperator()
            }
        }
    }
}
