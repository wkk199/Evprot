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
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.RecordDetailResp
import com.evport.businessapp.data.bean.RequestRecordDetail
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.RecordViewModel
import com.evport.businessapp.utils.toast
import com.gyf.immersionbar.ImmersionBar
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_record_detail.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Create by KunMinX at 19/10/29
 */
class RecordDetailFragment : BaseFragment() {
    private var mDrawerViewModel: RecordViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(RecordViewModel::class.java)
    }

    val pk by lazy {
        arguments?.getString("record")
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_record_detail, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
//            .addBindingParam(BR.info,data)
    }

    fun hideToolbar(){
        toolbar.isVisible = false
    }
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        ImmersionBar.with(this@RecordDetailFragment)
            .statusBarDarkFont(false).init()

    }

    inner class ClickProxy {
        fun back() {
            ImmersionBar.with(this@RecordDetailFragment)
                .statusBarDarkFont(true).init()
            nav().navigateUp()
        }
        fun comment() {
            startActivity<CreateCommentActivity>(Pair("record", pk))
//            nav().navigate(R.id.action_global_createCommentFragment,
//                Bundle().apply
//                { putString("record", pk) })
        }
    }

    public override fun loadInitData() {
        super.loadInitData()
        getData()
        tv_show_detail.setOnClickListener {
            rl_card4.isVisible = !rl_card4.isVisible
        }

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    fun getData() {
        object : NetworkBoundResource<RecordDetailResp>(networkStatusCallback = object :
            NetworkStatusCallback<RecordDetailResp> {

            override fun onSuccess(data: RecordDetailResp?) {
                data?.apply {
                    mDrawerViewModel?.info?.value = data
                    tv_start_en3.isVisible = !balance.isNullOrBlank()
                    tv_en3.isVisible = !balance.isNullOrBlank()
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<RecordDetailResp>> {
                return SingletonFactory.apiService.recordsDetail(requestRecord = RequestRecordDetail(pk))
            }
        }
    }

}