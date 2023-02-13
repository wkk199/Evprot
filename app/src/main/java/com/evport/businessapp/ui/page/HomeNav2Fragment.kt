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
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Create by KunMinX at 19/10/29
 */
class HomeNav2Fragment : BaseFragment() {
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
        return DataBindingConfig(R.layout.fragment_home_nav2, mDrawerViewModel)
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
    }

    inner class ClickProxy {
        fun ChargingStatus() {
//            nav().navigate(R.id.action_global_chargeStatuListFragment)
//            startActivity(Intent(requireContext(),ChargeStatuListActivity::class.java))
        }
        fun SelectCharger() {

            nav().navigate(R.id.action_global_chargePointListFragment)

        }
        @SuppressLint("CheckResult")
        fun scan() {

            RxPermissions(requireActivity())
                .request(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .subscribe {
                    if (it) {

                    nav().navigate(R.id.action_global_scanFragment)
//                        startActivity(Intent(requireContext(),ScanActivity::class.java))

                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.CAMERA
                            )
                        ) {
                            try {
                                ToastUtils.showShort(getString(R.string.weneedpermission))
                            } catch (e: Exception) {

                            }
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

        fun open() {
//            sharedViewModel.openOrCloseDrawer.setValue(true)
        }
    }
}