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


import android.bluetooth.le.ScanResult;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evport.businessapp.data.bean.ChargeSetting;
import com.evport.businessapp.data.bean.Comment;
import com.evport.businessapp.data.bean.Feedback;
import com.evport.businessapp.data.bean.MyCard;
import com.evport.businessapp.data.bean.NotiSys;
import com.evport.businessapp.data.bean.ReplyDetail;
import com.evport.businessapp.data.bean.RequestScan;
import com.evport.businessapp.data.bean.SocketType;
import com.evport.businessapp.domain.request.ChargeSettingRequest;
import com.evport.businessapp.domain.request.Request;

import java.util.ArrayList;
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
 *
 *
 */
public class DrawerViewModel extends ViewModel implements Request.IChargeSettingRequest {

    //TODO 此处用于绑定的状态，使用 LiveData 而不是 ObservableField，主要是考虑到 ObservableField 具有防抖的特性，不适合该场景。

    //如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350

    public final MutableLiveData<List<ChargeSetting>> list = new MutableLiveData<>();
    public final MutableLiveData<List<SocketType>> list1 = new MutableLiveData<>();
    public final MutableLiveData<List<SocketType>> list2 = new MutableLiveData<>();
    public final MutableLiveData<List<SocketType>> list3 = new MutableLiveData<>();

    public final MutableLiveData<List<ScanResult>> whiteList = new MutableLiveData<>();

    public final ObservableField<String> whiteListStr = new ObservableField<>();

    public final ObservableBoolean passwordVisible = new ObservableBoolean();

    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<String> range = new ObservableField<>();
    public final ObservableField<String> left = new ObservableField<>();
    public final ObservableField<String> right = new ObservableField<>();

    public final ObservableField<String> cache = new ObservableField<>();
    public final ObservableField<String> version = new ObservableField<>();
    public final ObservableField<NotiSys> sysDetail = new ObservableField<>();


//    notification

    public final ObservableBoolean emptyshow = new ObservableBoolean();
    public final MutableLiveData<ArrayList<Comment>> listNotiComment = new MutableLiveData<>();
    public final MutableLiveData<ArrayList<ReplyDetail>> listNotiReply = new MutableLiveData<>();
    public final MutableLiveData<ArrayList<NotiSys>> listNotiSys = new MutableLiveData<>();
    public final MutableLiveData<ArrayList<Feedback>> listNotiFeedback = new MutableLiveData<>();
    public final MutableLiveData<ArrayList<MyCard>> listMyCard = new MutableLiveData<>();


    private ChargeSettingRequest mInfoRequest = new ChargeSettingRequest();

    {
        left.set("0");
        right.set("360");

    }


    @Override
    public LiveData<List<ChargeSetting>> getChargeSettingLiveData() {
        return mInfoRequest.getChargeSettingLiveData();
    }
    public LiveData<ArrayList<SocketType>> getOperatorLiveData() {
        return mInfoRequest.getOperatorLiveData();
    }

    public LiveData<ChargeSetting> getChargeSetLiveData() {
        return mInfoRequest.getChargeSetLiveData();
    }

    public LiveData<Boolean> getBindLiveData() {
        return mInfoRequest.getBindLiveData();
    }


    @Override
    public void unBindFamily(RequestScan requestScan) {
        mInfoRequest.unBindFamily(requestScan);
    }

    @Override
    public void requestChargeList() {
        mInfoRequest.requestChargeList();

    }

    @Override
    public void requestChargeSet(ChargeSetting chargeSetting) {
        mInfoRequest.requestChargeSet(chargeSetting);

    }

    public void getOperator() {
        mInfoRequest.getOperator();

    }

}
