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

import com.blankj.utilcode.util.Utils;
import com.evport.businessapp.R;
import com.evport.businessapp.data.bean.RecordDetailResp;
import com.evport.businessapp.data.bean.RequestRecord;
import com.evport.businessapp.data.bean.RecordResp;
import com.evport.businessapp.domain.request.RecordRequest;
import com.evport.businessapp.domain.request.Request;

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
public class RecordViewModel extends ViewModel implements Request.IRecordRequest {

    //TODO 此处用于绑定的状态，使用 LiveData 而不是 ObservableField，主要是考虑到 ObservableField 具有防抖的特性，不适合该场景。

    //如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350

    public final MutableLiveData<List<RecordResp>> list = new MutableLiveData<>();
    public final MutableLiveData<RecordDetailResp> info = new MutableLiveData<>();

//    public final MutableLiveData<RequestStats> requestStatsMutableLiveData = new MutableLiveData<>();

//    默认个人
    public RequestRecord requestRecord = new RequestRecord();
//    public RecordDetailResp info = new RecordDetailResp();

    public final ObservableBoolean isPersonal = new ObservableBoolean();

    public final ObservableField<String> startDate = new ObservableField<>();
    public final ObservableField<String> endDate = new ObservableField<>();
    //    数据源
    public final ObservableField<String> dataRes = new ObservableField<>();

    //    二氧化碳, 二氧化硫, 粉尘
    public final ObservableField<String> co2 = new ObservableField<>();
    public final ObservableField<String> so2 = new ObservableField<>();
    public final ObservableField<String> dust = new ObservableField<>();




    private RecordRequest mInfoRequest = new RecordRequest();
    {
        dataRes.set(Utils.getApp().getResources().getString(R.string.Date));
    }


    @Override
    public LiveData<List<RecordResp>> getRecordLiveData() {
        return mInfoRequest.getRecordLiveData();
    }

    @Override
    public void requestRecord(RequestRecord requestRecord) {

        mInfoRequest.requestRecord(requestRecord);
    }

}
