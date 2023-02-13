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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.kunminx.architecture.domain.manager.NetState
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.SelectPointListActivity
import com.evport.businessapp.ui.page.adapter.ChargeSettingAdapter
import com.evport.businessapp.ui.state.ScanViewModel

/**
 * Create by KunMinX at 20/04/26
 */

class ChargePointListFragment : BaseFragment() {


    private lateinit var mScaViewModel: ScanViewModel
    private lateinit var mAdapter: ChargeSettingAdapter

    override fun initViewModel() {
        mScaViewModel = getFragmentViewModel(ScanViewModel::class.java)

    }


    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = ChargeSettingAdapter(requireContext())
        return DataBindingConfig(R.layout.fragment_charge_points_setting, mScaViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                mAdapter
            )
    }

    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mScaViewModel.chargePointsLiveData.observe(
            viewLifecycleOwner,
            Observer {
                mScaViewModel.list.value = it
                dismissLoading()
            }
        )


        mScaViewModel?.scanLiveData?.observe(
            viewLifecycleOwner,
            Observer { i ->
                dismissLoading()
                if (!i) {
//                    需要输入密码
                    nav().navigate(R.id.action_scanFragment_to_scanSetPasswordFragment,
                        Bundle().also {
                            it.putString(SCANDATA, mScaViewModel?.requestScan?.chargeBoxPk)
                        }
                    )
                } else {
//                    直接去充电抢页面
//                    nav().navigate(R.id.action_global_selectPointListFragment,
//                        Bundle().also {
//                            it.putString()
//                        }
//                    )
                    val i = Intent(requireContext(), SelectPointListActivity::class.java)
                    i.putExtra(SCANDATA, mScaViewModel?.requestScan?.chargeBoxPk)
                    startActivity(i)
                }
            }
        )

        mAdapter.setOnItemClickListener { item, position ->
            showLoading()
            mScaViewModel?.requestScan?.chargeBoxPk = item.chargeBoxPk
            mScaViewModel?.requestScan(mScaViewModel?.requestScan)
        }


        showLoading()
        mScaViewModel.requestChargePoint()
    }

    override fun onResume() {
        super.onResume()
        mScaViewModel.requestChargePoint()
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun nextClick() {

        }
    }
}