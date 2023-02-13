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
package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.kunminx.architecture.ui.callback.EventObserver
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.FamilyList
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.*
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home_user.*
import kotlinx.android.synthetic.main.fragment_home_user.iv_avatar
import kotlinx.android.synthetic.main.fragment_home_user.tv_name
import org.jetbrains.anko.support.v4.startActivity

/**
 * Create by KunMinX at 19/10/29
 */
class HomeUserFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig { //TODO tip: DataBinding 严格模式：
// 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
// 通过这样的方式，来彻底解决 视图调用的一致性问题，
// 如此，视图刷新的安全性将和基于函数式编程的 Jetpack Compose 持平。
// 而 DataBindingConfig 就是在这样的背景下，用于为 base 页面中的 DataBinding 提供绑定项。
// 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910
        return DataBindingConfig(R.layout.fragment_home_user, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
        //                .addBindingParam(BR.adapter, new DrawerAdapter(getContext()));
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

//        tv_name.text = SPUtils.getInstance().getString(Configs.NAME)
        //        mDrawerViewModel.getLibraryLiveData().observe(getViewLifecycleOwner(), libraryInfos -> {
//            if (mAnimationLoaded && libraryInfos != null) {
//                mDrawerViewModel.list.setValue(libraryInfos);
//            }
//        });
//
//        mDrawerViewModel.requestLibraryInfo();
        initUser()
        sharedViewModel.isLoginSuccess.observe(this,
            EventObserver<Boolean> { aBoolean ->
                if (aBoolean) {
                    initUser()
                }
            })
    }

    public override fun loadInitData() {
        super.loadInitData()
    }

    override fun onResume() {
        super.onResume()
        initUser()

    }

    fun initUser(){
        val user = requireContext().getUser()
        user?.apply {
            iv_avatar.setImageIsWifi(avatarUrl)
            tv_name.text = name
            tv_email.text = email

        }
    }

    inner class ClickProxy {
        fun logoClick() {
            nav().navigate(R.id.action_global_userSettingFragment)
        }

        fun myMessage() {
            nav().navigate(R.id.action_global_notificationCenterFragment)

        }
        fun myCollect() {
            startActivity<UserCollectActivity>()
        }
        fun myCard() {
            nav().navigate(R.id.action_global_myWalletFragment)
        }
        fun family() {
            getFamilyList()

        }
        fun bluetooth() {
            nav().navigate(R.id.action_global_chargeWhiteListFragment)
        }

        fun feedback() {
            startActivity<CreatFeedbackActivity>()
        }

        fun setting() {
            nav().navigate(R.id.action_global_appSettingFragment)
        }

        fun aboutUS() {
            nav().navigate(R.id.action_global_aboutUsFragment)
        }
        fun invoicing(){
            nav().navigate(R.id.action_global_InvoicingFragment)

        }


    }


    fun getFamilyList() {
        showLoading()
        object : NetworkBoundResource<ArrayList<FamilyList>>(networkStatusCallback = object :
            NetworkStatusCallback<ArrayList<FamilyList>> {

            override fun onSuccess(data: ArrayList<FamilyList>?) {

                if (data.isNullOrEmpty()){
                    startActivity<CreateEditFamilyActivity>(
                        Pair(ISSUCCESSTOHOME,true)
                    )
                }else {
                    startActivity<UserFamilyActivity>()
                }
                dismissLoading()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<FamilyList>>> {
                return SingletonFactory.apiService.getHomeList()
            }
        }
    }

}