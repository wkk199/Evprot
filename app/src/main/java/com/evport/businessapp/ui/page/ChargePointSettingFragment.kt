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
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.ChargeSetting
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel

/**
 * Create by KunMinX at 20/04/26
 */

class ChargePointSettingFragment : BaseFragment() {


    private val item by lazy {
        arguments?.getParcelable<ChargeSetting>(CHARGE_SETTING)
    }

    private lateinit var mDrawerViewModel: DrawerViewModel
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_charge_point_setting, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
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

        fun changePassword(){
            nav().navigate(R.id.action_chargePointSettingFragment_to_chargeChangePasswordFragment,
                Bundle().also {
                    it.putParcelable(CHARGE_SETTING,item)
                }
            )
        }

        fun whiteList(){

//            nav().navigate(R.id.(action_chargePointSettingFragment_to_chargeWhiteListFragment,
//                Bundle().also {
//                    it.putParcelable(CHARGE_SETTING,item)
//                }
//            )
        }



    }
}