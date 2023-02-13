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

import com.google.android.material.tabs.TabLayout;
import com.evport.businessapp.data.bean.NameValue;
import com.evport.businessapp.data.bean.StationListBean;
import com.evport.businessapp.data.bean.RequestStats;
import com.evport.businessapp.data.bean.StatsData;
import com.evport.businessapp.data.bean.StatsDataResp;
import com.evport.businessapp.domain.request.Request;
import com.evport.businessapp.domain.request.StatsRequest;

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
public class StatsViewModel extends ViewModel implements Request.IStatsRequest {

    //TODO 此处用于绑定的状态，使用 LiveData 而不是 ObservableField，主要是考虑到 ObservableField 具有防抖的特性，不适合该场景。

    //如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350

    public final MutableLiveData<List<StatsData>> list = new MutableLiveData<>();
    public final MutableLiveData<List<StationListBean>> gunListBean = new MutableLiveData<>();
    public final MutableLiveData<List<StationListBean>> listOne = new MutableLiveData<>();
    public final MutableLiveData<List<NameValue>> listNameValue = new MutableLiveData<>();

//    public final MutableLiveData<RequestStats> requestStatsMutableLiveData = new MutableLiveData<>();


    public final ObservableBoolean isListShow = new ObservableBoolean();
    public final ObservableBoolean isItemShow = new ObservableBoolean();

    public final ObservableField<String> startDate = new ObservableField<>();
    public final ObservableField<String> endDate = new ObservableField<>();
    //    数据源
    public final ObservableField<String> dataRes = new ObservableField<>();
    public final ObservableField<String> range = new ObservableField<>();
    //    日期选则
//    public final ObservableField<String> date = new ObservableField<>();
    public final ObservableField<String> dateStr = new ObservableField<>();
    // 次数
    public final ObservableField<String> times = new ObservableField<>();
    //    二氧化碳, 二氧化硫, 粉尘
    public final ObservableField<String> chartValueString = new ObservableField<>();
    public final ObservableField<String> ChangeTime = new ObservableField<>();
    public final ObservableField<String> chargingEnergy = new ObservableField<>();
    public final ObservableField<String> chargingTime = new ObservableField<>();
    public final ObservableField<String> money= new ObservableField<>();
    //
    public final ObservableField<Boolean> filterDwa = new ObservableField<>();


    private StatsRequest mInfoRequest = new StatsRequest();

    private TabLayout.OnTabSelectedListener onTabSelectedListener;
//
//    {
//
//        onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
////                        date.set("week");
//                        dateStr.set("by time of the week");
//                        requestStats.setBeginTime(DateUtil.INSTANCE.getOldDate(-7));
//                        break;
//                    case 1:
////                        date.set("moth");
//                        dateStr.set("by time of the moth");
//                        requestStats.setBeginTime(DateUtil.INSTANCE.getOldDate(-31));
//                        break;
//                    default:
////                        date.set("year");
//                        dateStr.set("by time of the year");
//                        requestStats.setBeginTime(DateUtil.INSTANCE.getOldDate(-365));
//
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        };
//    }

    {
//        range.set("20km");
        dateStr.set("by time of the week");
        dataRes.set("Personal charging data");
        ChangeTime.set("2020-01");
    }

    public TabLayout.OnTabSelectedListener getOnTabSelectedListener() {
        return onTabSelectedListener;
    }

    public void setOnTabSelectedListener(TabLayout.OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    @Override
    public LiveData<StatsDataResp> getStatsLiveData() {
        return mInfoRequest.getStatsLiveData();
    }

    @Override
    public void requestStats(RequestStats requestStats) {
        mInfoRequest.requestStats(requestStats);
    }
}
