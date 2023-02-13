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

import com.evport.businessapp.data.bean.ChangePassword;
import com.evport.businessapp.data.bean.User;
import com.evport.businessapp.data.config.Configs;
import com.evport.businessapp.data.repository.DataRepository;

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
 * Create by KunMinX at 20/04/26
 */
public class AccountRequest implements Request.IAccountRequest {

    private MutableLiveData<User> userLiveData;
    private MutableLiveData<User> checkNameLiveData;
    private MutableLiveData<ChangePassword> changePasswordLiveData;


    @Override
    public LiveData<User> getUserLiveData() {
        if (userLiveData == null) {
            userLiveData = new MutableLiveData<>();
        }
        return userLiveData;
    }

    public LiveData<User> getCheckNameLiveData() {
        if (checkNameLiveData == null) {
            checkNameLiveData = new MutableLiveData<>();
        }
        return checkNameLiveData;
    }


    public LiveData<ChangePassword> getChangePasswordLiveData() {
        if (changePasswordLiveData == null) {
            changePasswordLiveData = new MutableLiveData<>();
        }
        return changePasswordLiveData;
    }



//    @Override
//    public LiveData<String> getStringLiveData() {
//        if (stringLiveData == null)
//            stringLiveData = new MutableLiveData<>();
//        return stringLiveData;
//    }

    @Override
    public void requestLogin(User user) {
        DataRepository.Companion.getInstance().login(user, userLiveData);
    }

    @Override
    public void requestSignUp(User user) {

        DataRepository.Companion.getInstance().signUp(user, userLiveData);
    }

    @Override
    public void resetPassword(User user) {
        DataRepository.Companion.getInstance().forgetPwd(user, userLiveData);

    }

    @Override
    public void changePassword(ChangePassword changePassword) {

        DataRepository.Companion.getInstance().changePwd(changePassword, changePasswordLiveData);
    }

    @Override
    public void sendEmailCode(String email ,int from) {
        if (from == Configs.CODE_SINGUP)
            DataRepository.Companion.getInstance().getCode(email,userLiveData);
        else
            DataRepository.Companion.getInstance().getCodeResetPwd(email,userLiveData);

    }

    @Override
    public void checkUserName(String email) {

        DataRepository.Companion.getInstance().checkName(email,checkNameLiveData);

    }
}
