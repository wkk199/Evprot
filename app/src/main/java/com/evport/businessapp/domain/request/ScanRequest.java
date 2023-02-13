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

package com.evport.businessapp.domain.request;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.evport.businessapp.data.bean.ChargeSetting;
import com.evport.businessapp.data.bean.CheckTransaction;
import com.evport.businessapp.data.bean.GunResp;
import com.evport.businessapp.data.bean.RequestCharge;
import com.evport.businessapp.data.bean.RequestChargeChange;
import com.evport.businessapp.data.bean.RequestScan;
import com.evport.businessapp.data.repository.DataRepository;

import java.util.List;

/**
 * 信息列表 Request
 * <p>
 * TODO tip：Request 通常按业务划分
 * 一个项目中通常存在多个 Request
 * <p>
 * request 的职责仅限于 数据请求，不建议在此处理 UI 逻辑，
 * UI 逻辑只适合在 Activity/Fragment 等视图控制器中完成，是 “数据驱动” 的一部分，
 * 将来升级到 Jetpack Compose 更是如此。
 * <p>
 * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840
 * <p>
 * <p>
 * Create by KunMinX at 19/11/2
 */
public class ScanRequest implements Request.IScanRequest {

    private MutableLiveData<Boolean> mutableLiveData;

    private MutableLiveData<List<ChargeSetting>> mChargeSettingLiveData;

    private MutableLiveData<List<GunResp>> mChargeGunsLiveData;

    private MutableLiveData<List<CheckTransaction>> mCheckTransLiveData;

    //    ----------------
    private MutableLiveData<String> mChargingSchedule;
    private MutableLiveData<String> mChargingScheduleUpdate;
    private MutableLiveData<String> mChargingScheduleDelete;


    private MutableLiveData<String> mAutoSwitch;
    private MutableLiveData<String> mRemoteStart;
    private MutableLiveData<String> mRemoteStop;

    private MutableLiveData<Boolean> mBindLiveData;


    public LiveData<String> getChargingSchedule() {
        if (mChargingSchedule == null)
            mChargingSchedule = new MutableLiveData<>();
        return mChargingSchedule;
    }

    public LiveData<String> getChargingScheduleUpdate() {
        if (mChargingScheduleUpdate == null)
            mChargingScheduleUpdate = new MutableLiveData<>();
        return mChargingScheduleUpdate;
    }

    public LiveData<String> getChargingScheduleDelete() {
        if (mChargingScheduleDelete == null)
            mChargingScheduleDelete = new MutableLiveData<>();
        return mChargingScheduleDelete;
    }

    public LiveData<String> getAutoSwitch() {
        if (mAutoSwitch == null)
            mAutoSwitch = new MutableLiveData<>();
        return mAutoSwitch;
    }

    public LiveData<String> getRemoteStart() {
        if (mRemoteStart == null)
            mRemoteStart = new MutableLiveData<>();
        return mRemoteStart;
    }

    public LiveData<String> getRemoteStop() {
        if (mRemoteStop == null)
            mRemoteStop = new MutableLiveData<>();
        return mRemoteStop;
    }


//    ------------
    //TODO tip 向 ui 层提供的 request LiveData，使用抽象的 LiveData 而不是 MutableLiveData
    // 如此是为了来自数据层的数据，在 ui 层中只读，以避免团队新手不可预期的误用

    public LiveData<List<ChargeSetting>> getChargePointsLiveData() {
        if (mChargeSettingLiveData == null) {
            mChargeSettingLiveData = new MutableLiveData<>();
        }
        return mChargeSettingLiveData;
    }

    public LiveData<List<GunResp>> getChargeGunLiveData() {
        if (mChargeGunsLiveData == null) {
            mChargeGunsLiveData = new MutableLiveData<>();
        }
        return mChargeGunsLiveData;
    }

    public LiveData<List<CheckTransaction>> getCheckTransLiveData() {
        if (mCheckTransLiveData == null) {
            mCheckTransLiveData = new MutableLiveData<>();
        }
        return mCheckTransLiveData;
    }

    @Override
    public LiveData<Boolean> getScanLiveData() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    public LiveData<Boolean> getBindLiveData() {
        if (mBindLiveData == null) {
            mBindLiveData = new MutableLiveData<>();
        }
        return mBindLiveData;
    }
    @Override
    public void requestScan(RequestScan requestScan) {
        DataRepository.Companion.getInstance().getScan(requestScan, mutableLiveData);
    }

    @Override
    public void requestChargePoint() {
        DataRepository.Companion.getInstance().getChargePoints(mChargeSettingLiveData);

    }

    @Override
    public void requestChargeGun(RequestScan requestScan) {
        DataRepository.Companion.getInstance().getChargeGuns(requestScan, mChargeGunsLiveData);

    }

    @Override
    public void getCheckTransactions() {
        DataRepository.Companion.getInstance().getCheckTransactions(mCheckTransLiveData);
    }
    public void getCheckTransactions(String pk) {
        DataRepository.Companion.getInstance().getCheckTransactions( pk,mCheckTransLiveData);
    }

    @Override
    public void setChargingSchedule(RequestCharge requestCharge) {
        DataRepository.Companion.getInstance().setChargingSchedule(requestCharge, mChargingSchedule);
    }


    @Override
    public void setChargingScheduleStart(RequestCharge requestCharge) {
        DataRepository.Companion.getInstance().setChargingScheduleStart(requestCharge, mChargingSchedule);
    }

    @Override
    public void setChargingScheduleUpdate(RequestCharge requestCharge) {
        DataRepository.Companion.getInstance().setChargingScheduleUpdate(requestCharge, mChargingScheduleUpdate);

    }

    @Override
    public void setChargingScheduleDelete(RequestCharge requestCharge) {
        DataRepository.Companion.getInstance().setChargingScheduleDelete(requestCharge, mChargingScheduleDelete);

    }

    @Override
    public void setAutoSwitch(RequestChargeChange requestChargeChange) {
        DataRepository.Companion.getInstance().setAutoSwitch(requestChargeChange, mAutoSwitch);

    }

    @Override
    public void remoteStart(RequestChargeChange requestChargeChange) {

        DataRepository.Companion.getInstance().remoteStart(requestChargeChange, mRemoteStart);
    }

    @Override
    public void remoteStop(RequestChargeChange requestChargeChange) {

        DataRepository.Companion.getInstance().remoteStop(requestChargeChange, mRemoteStop);
    }

    @Override
    public void bindFamily(RequestScan requestScan) {
        DataRepository.Companion.getInstance().bindFamily(requestScan,mBindLiveData);
    }
}
