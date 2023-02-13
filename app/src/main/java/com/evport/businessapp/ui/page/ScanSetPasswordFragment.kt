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
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.kunminx.architecture.domain.manager.NetState
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.SelectPointListActivity
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.utils.toMD5

/**
 * Create by KunMinX at 20/04/26
 */
class ScanSetPasswordFragment : BaseFragment() {


    private lateinit var mChargeSetViewModel: ScanViewModel
    private val item by lazy {
        arguments?.getString(SCANDATA)
    }

    override fun initViewModel() {
        mChargeSetViewModel = getFragmentViewModel(ScanViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_scan_pwd, mChargeSetViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mBinding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_login);
//        getLifecycle().addObserver(DrawerCoordinateHelper.getInstance());
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mChargeSetViewModel.scanLiveData.observe(
            viewLifecycleOwner,
            Observer {
                dismissLoading()
                if (it) {
//                    密码成功
                    val i = Intent(requireContext(), SelectPointListActivity::class.java)
                    i.putExtra(SCANDATA, item)
                    startActivity(i)
                } else {
                    ToastUtils.showShort("wrong password")
                }
            }
        )

    }

    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun toSetPassword() {
//
            if (TextUtils.isEmpty(mChargeSetViewModel.password.get())) {
                showLongToast("password is empty")
                return
            }

            if (item == null) {
                nav().navigateUp()
            }

            showLoading()
            mChargeSetViewModel.requestScan.password = mChargeSetViewModel.password.get().toString().toMD5()
            mChargeSetViewModel.requestScan.chargeBoxPk = item
            mChargeSetViewModel.requestScan(mChargeSetViewModel.requestScan)

        }

        fun toLockPassword() {
            mChargeSetViewModel.passwordVisible.set(!mChargeSetViewModel.passwordVisible.get())
        }


    }
}