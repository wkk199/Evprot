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
import com.evport.businessapp.data.bean.RequestScan;
import com.evport.businessapp.data.bean.SocketType;
import com.evport.businessapp.data.repository.DataRepository;

import java.util.ArrayList;
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
public class ChargeSettingRequest implements Request.IChargeSettingRequest {

    private MutableLiveData<List<ChargeSetting>> mChargeSettingLiveData;
    private MutableLiveData<ArrayList<SocketType>> mOperatorLiveData;
    private MutableLiveData<ChargeSetting> mChargeSetLiveData;
    private MutableLiveData<Boolean> mBindLiveData;

    //TODO tip 向 ui 层提供的 request LiveData，使用抽象的 LiveData 而不是 MutableLiveData
    // 如此是为了来自数据层的数据，在 ui 层中只读，以避免团队新手不可预期的误用

    @Override
    public LiveData<List<ChargeSetting>> getChargeSettingLiveData() {
        if (mChargeSettingLiveData == null) {
            mChargeSettingLiveData = new MutableLiveData<>();
        }
        return mChargeSettingLiveData;
    }

    public LiveData<ChargeSetting> getChargeSetLiveData() {
        if (mChargeSetLiveData == null) {
            mChargeSetLiveData = new MutableLiveData<>();
        }
        return mChargeSetLiveData;
    }

    public LiveData<Boolean> getBindLiveData() {
        if (mBindLiveData == null) {
            mBindLiveData = new MutableLiveData<>();
        }
        return mBindLiveData;
    }

    public LiveData<ArrayList<SocketType>> getOperatorLiveData() {
        if (mOperatorLiveData == null) {
            mOperatorLiveData = new MutableLiveData<>();
        }
        return mOperatorLiveData;
    }

    @Override
    public void unBindFamily(RequestScan requestScan) {
        DataRepository.Companion.getInstance().unbingfamily(requestScan,mBindLiveData);
    }

    @Override
    public void requestChargeList() {
        DataRepository.Companion.getInstance().getChargeList(mChargeSettingLiveData);
    }

    @Override
    public void requestChargeSet(ChargeSetting chargeSetting) {

        DataRepository.Companion.getInstance().chargeSetting(chargeSetting,mChargeSetLiveData);
    }

    public void getOperator(){
        DataRepository.Companion.getInstance().getOperator(mOperatorLiveData);

    }

}
