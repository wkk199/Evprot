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
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.bean.MessageData
import com.evport.businessapp.data.bean.NotiSys
import com.evport.businessapp.data.bean.SysDetailReq
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.LiveBus
import io.reactivex.Observable

/**
 * Create by KunMinX at 19/10/29
 */
class NotiSysDetailFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    val data by lazy {
        arguments?.getParcelable<NotiSys>("sys")
    }
    val pk by lazy {
        arguments?.getString("pk")?:""
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_notification_sys_detail, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
//            .addBindingParam(BR.info,data)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }

    public override fun loadInitData() {
        super.loadInitData()

        mDrawerViewModel?.sysDetail?.set(null)
        mDrawerViewModel?.sysDetail?.set(data)

        if (pk.isNotBlank()) {
            getData()
            checkSystemMessage(pk)
        } else {
            checkSystemMessage(data!!.sysMessagePk)
        }

    }


    fun getData() {
        showLoading()
        object : NetworkBoundResource<NotiSys>(networkStatusCallback = object :
            NetworkStatusCallback<NotiSys> {

            override fun onSuccess(data: NotiSys?) {
                dismissLoading()
                data?.apply {
                    mDrawerViewModel?.sysDetail?.set(this)
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<NotiSys>> {
                return SingletonFactory.apiService.getNotiSysDetail(
                    SysDetailReq(sysMessagePk = pk)
                )
            }
        }
    }
    fun checkSystemMessage(sysMessagePk: String?) {
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                LiveBus.getInstance().post(EventBean(Configs.NOTIFICATION_MSG, true, ""))

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.checkSystemMessage(
                    MessageData(systemMessagePk = sysMessagePk)
                )
            }
        }
    }
}