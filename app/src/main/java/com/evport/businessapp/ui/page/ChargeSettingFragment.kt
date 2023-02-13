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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.kunminx.architecture.ui.callback.EventObserver
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.RequestScan
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.ChargeSettingAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Create by KunMinX at 20/04/26
 */
const val CHARGE_SETTING = "CHARGE_SETTING"
const val WHITELIST = "WHITELIST"

class ChargeSettingFragment : BaseFragment() {


    private lateinit var mDrawerViewModel: DrawerViewModel
    private lateinit var mAdapter: ChargeSettingAdapter

    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)

    }


    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = ChargeSettingAdapter(requireContext(), true)
        return DataBindingConfig(R.layout.fragment_charge_setting, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                mAdapter
            )
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.bindSuccess.observe(
            viewLifecycleOwner,
            EventObserver {
                mDrawerViewModel.requestChargeList()
            }
        )

        mDrawerViewModel.chargeSettingLiveData.observe(
            viewLifecycleOwner,
            Observer {
                mDrawerViewModel.list.value = it
                dismissLoading()
            }
        )
        mAdapter.setOnItemClickListener { item, position ->

            nav().navigate(R.id.action_chargeSettingFragment_to_chargePointSettingFragment,
                Bundle().also {
                    it.putParcelable(CHARGE_SETTING, item)
                }
            )
        }
        mDrawerViewModel.bindLiveData.observe(
            viewLifecycleOwner,
            Observer {
                mDrawerViewModel.requestChargeList()
            }
        )
        mAdapter.deleteClick = { position: Int, item ->
            val bean = RequestScan()
            bean.chargeBoxPk = item.chargeBoxPk
            mDrawerViewModel.unBindFamily(bean)
        }



        showLoading()
        mDrawerViewModel.requestChargeList()
    }


    override fun onResume() {
        super.onResume()
        mDrawerViewModel.requestChargeList()
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun nextClick() {

        }


        @SuppressLint("CheckResult")
        fun add() {

            RxPermissions(requireActivity())
                .request(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .subscribe { it ->
                    if (it) {

                        nav().navigate(R.id.action_global_scanFragment,
                            Bundle().also { b ->
                                b.putString(SCANDTYPE, "add")
                            }
                        )
//                        startActivity(Intent(requireContext(),ScanActivity::class.java))

                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.CAMERA
                            )
                        ) {
//                            try {
//                                ToastUtils.showShort("we need permission to scan")
//                            } catch (e: Exception) {
//
//                            }
                        } else {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
        }

    }
}