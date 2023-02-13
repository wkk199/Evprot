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
import com.kunminx.architecture.domain.manager.NetState
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.RecordViewModel
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_home_stats_and_records.*


/**
 * Create by KunMinX at 19/10/29
 */
class HomeRecordAndStatsFragment : BaseFragment() {
    private var recordViewModel: RecordViewModel? = null
    override fun initViewModel() {
        recordViewModel = getFragmentViewModel(RecordViewModel::class.java)
//        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home_stats_and_records, recordViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }
    var isRecordFm = true

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        //
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .statusBarDarkFont(true)
            .init()

        back.setImageResource(R.drawable.icon_record)
        title.text = resources.getString(R.string.record)
        childFragmentManager
            .beginTransaction()
            .add(R.id.content, HomeNav3Fragment())
            .commit()

    }


    fun changeFm(){
        if (isRecordFm){
            back.setImageResource(R.drawable.icon_record)
            title.text = resources.getString(R.string.order)
            childFragmentManager
                .beginTransaction()
                .replace(R.id.content, HomeNav3Fragment())
                .commit()
        }else{
            back.setImageResource(R.drawable.icon_sats)
            title.text = resources.getString(R.string.stats)
            childFragmentManager
                .beginTransaction()
                .replace(R.id.content, HomeNav1Fragment())
                .commit()
        }
    }

    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }


    inner class ClickProxy {


        fun open() {
            isRecordFm = !isRecordFm
            changeFm()
        }
    }


}