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
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.kunminx.architecture.domain.manager.NetState
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.ChargeSetting
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.toMD5

/**
 * Create by KunMinX at 20/04/26
 */
class ChargeChangePasswordFragment : BaseFragment() {


    private lateinit var mChargeSetViewModel: DrawerViewModel
    private val item by lazy {
        arguments?.getParcelable<ChargeSetting>(CHARGE_SETTING)
    }
    override fun initViewModel() {
        mChargeSetViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_charge_setting_pwd, mChargeSetViewModel)
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

    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mChargeSetViewModel.chargeSetLiveData.observe(
            viewLifecycleOwner,
            Observer {
                dismissLoading()
                nav().navigateUp()
            }
        )
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun toSetPassword() {
//
            if (TextUtils.isEmpty(mChargeSetViewModel.password.get())) {
                showLongToast(R.string.please_input)
                return
            }

            if (item==null){
                nav().navigateUp()
            }
            showLoading()
            item?.password = mChargeSetViewModel.password.get().toString().toMD5()
            mChargeSetViewModel.requestChargeSet(item)

        }

        fun toLockPassword() {
            mChargeSetViewModel.passwordVisible.set(!mChargeSetViewModel.passwordVisible.get())
        }


    }
}