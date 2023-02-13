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

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.evport.businessapp.data.bean.DownloadFile;
import com.evport.businessapp.domain.request.DownloadRequest;
import com.evport.businessapp.domain.request.Request;
import com.evport.businessapp.domain.usecase.CanBeStoppedUseCase;

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
public class SearchViewModel extends ViewModel implements Request.ICanBeStoppedDownloadRequest {

    public final ObservableField<Integer> progress = new ObservableField<>();

    public final ObservableField<Integer> progress_cancelable = new ObservableField<>();

    private DownloadRequest mDownloadRequest = new DownloadRequest();


    @Override
    public LiveData<DownloadFile> getDownloadFileCanBeStoppedLiveData() {
        return mDownloadRequest.getDownloadFileCanBeStoppedLiveData();
    }

    @Override
    public CanBeStoppedUseCase getCanBeStoppedUseCase() {
        return mDownloadRequest.getCanBeStoppedUseCase();
    }

    @Override
    public void requestCanBeStoppedDownloadFile() {
        mDownloadRequest.requestCanBeStoppedDownloadFile();
    }
}
