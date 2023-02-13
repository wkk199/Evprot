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

import com.evport.businessapp.data.bean.Comment;
import com.evport.businessapp.data.bean.ConnectorDetail;
import com.evport.businessapp.data.bean.Device;
import com.evport.businessapp.data.bean.FamilyList;
import com.evport.businessapp.data.bean.Fee;
import com.evport.businessapp.data.bean.ReplyDetail;
import com.evport.businessapp.data.bean.StationListBean;
import com.evport.businessapp.data.bean.RequestStats;
import com.evport.businessapp.data.bean.StationDetailBean;
import com.evport.businessapp.data.bean.StatsDataResp;
import com.evport.businessapp.data.bean.TestAlbum;
import com.evport.businessapp.data.bean.HomeStationBean;
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
public class StationViewModel extends ViewModel  {

    public final ObservableBoolean initTabAndPage = new ObservableBoolean();

    public final MutableLiveData<ConnectorDetail> info = new MutableLiveData<>();
    public final MutableLiveData<Comment> Comment = new MutableLiveData<>();
    public final MutableLiveData<StationDetailBean> stationInfo = new MutableLiveData<>();
    public final MutableLiveData<List<Fee>> Fee = new MutableLiveData<>();
    public final MutableLiveData<List<Device>> device = new MutableLiveData<>();
    public final MutableLiveData<Integer> commentCount = new MutableLiveData<>();

    public final ObservableField<String> deviceInfoTitle = new ObservableField<>();
    public final ObservableField<String> stationInfoTitle = new ObservableField<>();
    public final ObservableField<String> tabTitleDev = new ObservableField<>();
    public final ObservableField<String> tabTitleCom = new ObservableField<>();

    //TODO 此处用于绑定的状态，使用 LiveData 而不是 ObservableField，主要是考虑到 ObservableField 具有防抖的特性，不适合该场景。

    //如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350

    public final MutableLiveData<List<TestAlbum.TestMusic>> list = new MutableLiveData<>();
    public final MutableLiveData<List<Comment>> listCommentReply = new MutableLiveData<>();
    public final MutableLiveData<List<ReplyDetail>> listReplyDetail = new MutableLiveData<>();
    public final MutableLiveData<List<Comment>> listComments = new MutableLiveData<>();
    public final MutableLiveData<List<StationListBean>> gunListBean = new MutableLiveData<>();
    public final MutableLiveData<List<Device>> deviceListBean = new MutableLiveData<>();

    public final MutableLiveData<Boolean> notifyCurrentListChanged = new MutableLiveData<>();


    public final ObservableField<String> GunTitle = new ObservableField<>();



//    private MusicRequest mMusicRequest = new MusicRequest();
    private StatsRequest mInfoRequest = new StatsRequest();


    public LiveData<StatsDataResp> getStatsLiveData() {
        return mInfoRequest.getStatsLiveData();
    }

    public void requestStats(RequestStats requestStats) {
        mInfoRequest.requestStats(requestStats);
    }




//    family


    public final ObservableField<String> FamilyTitle = new ObservableField<>();
    public final ObservableField<String> FamilyBg = new ObservableField<>();
    public final ObservableField<String> imgBg = new ObservableField<>();
    public final MutableLiveData<List<FamilyList>> familyList = new MutableLiveData<>();

    public final MutableLiveData<List<HomeStationBean>> deviceListHomeBean = new MutableLiveData<>();

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> btnString = new ObservableField<>();
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> maxP = new ObservableField<>();
    public final ObservableField<String> reserveP = new ObservableField<>();
    public final ObservableField<String> note = new ObservableField<>();


}
