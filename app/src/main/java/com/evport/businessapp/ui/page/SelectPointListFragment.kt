///*
// * Copyright 2018-2020 KunMinX
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.powercore.eviecharge.eviechargeljx.ui.page
//
//import android.app.DatePickerDialog
//import android.app.DatePickerDialog.OnDateSetListener
//import android.app.TimePickerDialog
//import android.app.TimePickerDialog.OnTimeSetListener
//import android.os.Bundle
//import android.view.View
//import androidx.lifecycle.Observer
//import com.blankj.utilcode.util.TimeUtils
//import com.blankj.utilcode.util.ToastUtils
//import com.powercore.eviecharge.eviechargeljx.BR
//import com.powercore.eviecharge.eviechargeljx.R
//import com.powercore.eviecharge.eviechargeljx.data.bean.GunResp
//import com.powercore.eviecharge.eviechargeljx.ui.base.BaseFragment
//import com.powercore.eviecharge.eviechargeljx.ui.base.DataBindingConfig
//import com.powercore.eviecharge.eviechargeljx.ui.page.adapter.ChargeGunsAdapter
//import com.powercore.eviecharge.eviechargeljx.ui.state.ScanViewModel
//import com.powercore.eviecharge.eviechargeljx.utils.DateUtil
//import com.powercore.eviecharge.eviechargeljx.utils.DateUtil.longToString
//import kotlinx.android.synthetic.main.fragment_select_points_list.*
//import java.util.*
//
//
///**
// * Create by KunMinX at 20/04/26
// */
//
//class SelectPointListFragment : BaseFragment() {
//
//
//    private lateinit var mScaViewModel: ScanViewModel
//    private lateinit var mAdapter: ChargeGunsAdapter
//
//    private val chargeBoxPk by lazy {
//        arguments?.getString(SCANDATA,"-1")
//    }
//
//    override fun initViewModel() {
//        mScaViewModel = getFragmentViewModel(ScanViewModel::class.java)
//
//    }
//
//
//    override fun getDataBindingConfig(): DataBindingConfig {
//        mAdapter = ChargeGunsAdapter(requireContext())
//        return DataBindingConfig(R.layout.fragment_select_points_list, mScaViewModel)
//            .addBindingParam(
//                BR.click,
//                ClickProxy()
//            )
//            .addBindingParam(
//                BR.adapter,
//                mAdapter
//            )
//    }
//
//
//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?
//    ) {
//        super.onViewCreated(view, savedInstanceState)
//
//        mScaViewModel.chargeGunsLiveData.observe(
//            viewLifecycleOwner,
//            Observer {
////                it.add(GunResp("sasas1",false,"Plugged"))
////                it.add(GunResp("sasas2",false,"Plugged"))
////                it.add(GunResp("sasas",false,"sdasda"))
//                mScaViewModel.listGuns.value = it
//            }
//        )
//        mScaViewModel.requestScan?.chargeBoxPk = chargeBoxPk
//
////
////        mAdapter.setOnItemClickListener { item, position ->
////
////        }
//
//
//        mScaViewModel.requestChargeGun(mScaViewModel.requestScan)
//    }
//
//    inner class ClickProxy {
//        fun back() {
//            nav().navigateUp()
//        }
//
//        fun setTime() {
//            showDialogPick()
//        }
//
//        fun chargeNow() {
//
//        }
//
//
//        fun SolarPowerStorage() {
//            p_sol.isSelected = true
//            p_grid.isSelected = false
//
//            mScaViewModel.selectSolarPower.set(true)
//        }
//
//        fun PowerGrid() {
//
//            p_sol.isSelected = true
//            p_grid.isSelected = false
//            mScaViewModel.selectSolarPower.set(false)
//
//        }
//
//
//    }
//
//    //????????????????????????dialog??????????????????
//    private fun showDialogPick() {
//        val time = StringBuffer()
//        val timeLong = 0
//        //??????Calendar?????????????????????????????????
//        val calendar: Calendar = Calendar.getInstance()
//        val year: Int = calendar.get(Calendar.YEAR)
//        val month: Int = calendar.get(Calendar.MONTH)
//        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
//        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute: Int = calendar.get(Calendar.MINUTE)
//        //?????????TimePickerDialog??????
//        val timePickerDialog =
//            TimePickerDialog(requireContext(),
//                OnTimeSetListener { view, hourOfDay, minute ->
//                    //??????????????????????????????????????????
//                    time.append(" $hourOfDay:$minute")
//                    //????????????????????? todo date
//                    if (DateUtil.isBeforeNow(DateUtil.formatTimeStamp(time.toString()))){
//                        ToastUtils.showShort("data is illegal")
//                    }else{
//
//                    }
//
////                    timeText.text = time
//                }, hour, minute, true)
//        //?????????DatePickerDialog??????
//        val datePickerDialog =
//            DatePickerDialog(requireContext(),
//                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    //??????????????????????????????????????????
//time.plus("$year-${(monthOfYear+1)}-$dayOfMonth")//                    //?????????????????????????????????????????????
//                    timePickerDialog.show()
//                }, year, month, day
//            )
//        //???????????????????????????
//        datePickerDialog.show()
//    }
//}