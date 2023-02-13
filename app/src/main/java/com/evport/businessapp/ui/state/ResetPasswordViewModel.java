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
import androidx.lifecycle.ViewModel;

import com.evport.businessapp.data.bean.ChangePassword;
import com.evport.businessapp.data.bean.User;
import com.evport.businessapp.data.config.Configs;
import com.evport.businessapp.domain.request.AccountRequest;
import com.evport.businessapp.domain.request.Request;

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
 * Create by KunMinX at 20/04/26
 */
public class ResetPasswordViewModel extends ViewModel implements Request.IAccountRequest {



    public final ObservableField<String> email = new ObservableField<>();

    public final ObservableField<String> password = new ObservableField<>();

    public final ObservableField<String> emailCode = new ObservableField<>();
    public final ObservableField<String> confirmPassword = new ObservableField<>();

    public final ObservableBoolean passwordVisible = new ObservableBoolean();
    public final ObservableBoolean passwordVisible2 = new ObservableBoolean();
    public final ObservableBoolean passwordVisible3 = new ObservableBoolean();
    public final ObservableBoolean sendCodeEnable = new ObservableBoolean();

    private AccountRequest mAccountRequest = new AccountRequest();

//

    public final ObservableBoolean passwordVisible1 = new ObservableBoolean();
    public final ObservableField<String> password1 = new ObservableField<>();

    @Override
    public LiveData<User> getUserLiveData() {
        return mAccountRequest.getUserLiveData();
    }

    public LiveData<ChangePassword> getChangePwdLiveData() {
        return mAccountRequest.getChangePasswordLiveData();
    }

    @Override
    public void requestLogin(User user) {

    }

    @Override
    public void requestSignUp(User user) {

    }

    @Override
    public void resetPassword(User user) {
        mAccountRequest.resetPassword(user);
    }

    @Override
    public void changePassword(ChangePassword changePassword) {
        mAccountRequest.changePassword(changePassword);
    }

    @Override
    public void sendEmailCode(String email,int f) {
        mAccountRequest.sendEmailCode(email, Configs.CODE_RESETPWD);
    }

    @Override
    public void checkUserName(String name) {

    }
}
