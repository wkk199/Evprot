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

import com.evport.businessapp.data.bean.TestAlbum;
import com.evport.businessapp.data.repository.DataRepository;

/**
 * 音乐资源  Request
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
 * Create by KunMinX at 19/10/29
 */
public class MusicRequest implements Request.IMusicRequest {

    private MutableLiveData<TestAlbum> mFreeMusicsLiveData;

    //TODO tip 向 ui 层提供的 request LiveData，使用抽象的 LiveData 而不是 MutableLiveData
    // 如此是为了来自数据层的数据，在 ui 层中只读，以避免团队新手不可预期的误用

    @Override
    public LiveData<TestAlbum> getFreeMusicsLiveData() {
        if (mFreeMusicsLiveData == null) {
            mFreeMusicsLiveData = new MutableLiveData<>();
        }
        return mFreeMusicsLiveData;
    }

    @Override
    public void requestFreeMusics() {
        DataRepository.Companion.getInstance().getFreeMusic(mFreeMusicsLiveData);
    }
}
