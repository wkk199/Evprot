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

package com.evport.businessapp.ui.state;


import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kunminx.architecture.ui.callback.EventLiveData;
import com.evport.businessapp.data.bean.ChargeSetting;
import com.evport.businessapp.data.bean.CheckTransaction;
import com.evport.businessapp.data.bean.GunResp;
import com.evport.businessapp.data.bean.RequestCharge;
import com.evport.businessapp.data.bean.RequestChargeChange;
import com.evport.businessapp.data.bean.RequestScan;
import com.evport.businessapp.domain.request.Request;
import com.evport.businessapp.domain.request.ScanRequest;

import java.util.List;

/**
 * TODO tip：每个页面都要单独准备一个 state-ViewModel，
 * 来托管 DataBinding 绑定的临时状态，以及视图控制器重建时状态的恢复。
 * <p>
 * 此外，state-ViewModel 的职责仅限于 状态托管，不建议在此处理 UI 逻辑，
 * UI 逻辑只适合在 Activity/Fragment 等视图控制器中完成，是 “数据驱动” 的一部分，
 * 将来升级到 Jetpack Compose 更是如此。
 * <p>
 * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350
 * <p>
 * Create by KunMinX at 19/10/29
 */
public class ScanViewModel extends ViewModel implements Request.IScanRequest {

    //TODO 此处用于绑定的状态，使用 LiveData 而不是 ObservableField，主要是考虑到 ObservableField 具有防抖的特性，不适合该场景。

    public final LiveData<Boolean> liveData = new MutableLiveData<>();

    public final EventLiveData<Boolean> isConnectSocket = new EventLiveData<Boolean>();



//    绑定数据

    public final ObservableBoolean passwordVisible = new ObservableBoolean();


    public final ObservableBoolean selectSolarPower = new ObservableBoolean();

    public final ObservableField<String> password = new ObservableField<>();


//    请求

    public RequestScan requestScan = new RequestScan(null, null,null);
    private ScanRequest mInfoRequest = new ScanRequest();


//    列表数据

    public final MutableLiveData<List<ChargeSetting>> list = new MutableLiveData<>();

    public final MutableLiveData<List<GunResp>> listGuns = new MutableLiveData<>();

    public final MutableLiveData<List<CheckTransaction>> listCheckTransaction = new MutableLiveData<>();





//    初始化
    {
//        默认电网
        selectSolarPower.set(false);
    }


    public LiveData<List<ChargeSetting>> getChargePointsLiveData() {
        return mInfoRequest.getChargePointsLiveData();
    }

    public LiveData<List<GunResp>> getChargeGunsLiveData() {
        return mInfoRequest.getChargeGunLiveData();
    }

    public LiveData<List<CheckTransaction>> getCheckTransLiveData() {
        return mInfoRequest.getCheckTransLiveData();
    }
    //------------------


    public LiveData<String> getChargingSchedule() {
        return mInfoRequest.getChargingSchedule();
    }

    public LiveData<String> getChargingScheduleUpdate() {
        return mInfoRequest.getChargingScheduleUpdate();
    }

    public LiveData<String> getChargingScheduleDelete() {
        return mInfoRequest.getChargingScheduleDelete();
    }

    public LiveData<String> getAutoSwitch() {
        return mInfoRequest.getAutoSwitch();
    }

    public LiveData<String> getRemoteStart() {
        return mInfoRequest.getRemoteStart();
    }

    public LiveData<String> getRemoteStop() {
        return mInfoRequest.getRemoteStop();
    }


//    --------

    public LiveData<Boolean> getBindLiveData() {
        return mInfoRequest.getBindLiveData();
    }

    @Override
    public LiveData<Boolean> getScanLiveData() {
        return mInfoRequest.getScanLiveData();
    }

    @Override
    public void requestScan(RequestScan requestScan) {
        mInfoRequest.requestScan(requestScan);
    }

    @Override
    public void requestChargeGun(RequestScan requestScan) {
//        主要是用到 chargeBoxPk 参数
        mInfoRequest.requestChargeGun(requestScan);
    }

    @Override
    public void requestChargePoint() {

        mInfoRequest.requestChargePoint();
    }

    @Override
    public void getCheckTransactions() {
        mInfoRequest.getCheckTransactions();
    }

    public void getCheckTransactionsPK(String pk) {
        mInfoRequest.getCheckTransactions(pk);
    }

    @Override
    public void setChargingSchedule(RequestCharge requestCharge) {
            mInfoRequest.setChargingSchedule(requestCharge);
    }

    @Override
    public void setChargingScheduleStart(RequestCharge requestCharge) {
            mInfoRequest.setChargingScheduleStart(requestCharge);
    }

    @Override
    public void setChargingScheduleUpdate(RequestCharge requestCharge) {
        mInfoRequest.setChargingScheduleUpdate(requestCharge);
    }

    @Override
    public void setChargingScheduleDelete(RequestCharge requestCharge) {
        mInfoRequest.setChargingScheduleDelete(requestCharge);
    }

    @Override
    public void setAutoSwitch(RequestChargeChange requestChargeChange) {

        mInfoRequest.setAutoSwitch(requestChargeChange);
    }

    @Override
    public void remoteStart(RequestChargeChange requestChargeChange) {

        mInfoRequest.remoteStart(requestChargeChange);
    }

    @Override
    public void remoteStop(RequestChargeChange requestChargeChange) {

        mInfoRequest.remoteStop(requestChargeChange);
    }

    @Override
    public void bindFamily(RequestScan requestScan) {

        mInfoRequest.bindFamily(requestScan);
    }
}
