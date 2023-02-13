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

import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.ScanViewModel


/**
 * Create by KunMinX at 20/04/26
 */

class ChargeStatuListFragment : BaseFragment() {
//
    private lateinit var mScaViewModel: ScanViewModel
//    private lateinit var mAdapter: ChargeStatusAdapter
//
//    val url =
//        Configs.WEB_BASE_URL.plus(SPUtils.getInstance().getString(Configs.EMAIL)).plus("_pcApp")
//
////    private val chargeBoxPk by lazy {
////        arguments?.getString(SCANDATA,"305278015280128000")
////    }
//
//    val handler = Handler()
//    val runnable = Runnable {
//        Log.e("runable:", "start")
//        mScaViewModel.getCheckTransactions()
//    }
//
//
//    var h = Handler()
//    var runnableNet = Runnable {
//        dismissLoading()
//        //
//        AlertDialog
//            .Builder(requireContext())
//            .setTitle("Operation timed out")
//            .setMessage("The operation may be successful, please click confirm to return to the previous page")
//            .setPositiveButton("confirm") { p1, p2 ->
//                nav().navigateUp()
//            }
//            .create().show();
//    }
//
    override fun initViewModel() {
        mScaViewModel = getActivityViewModel(ScanViewModel::class.java)

    }
//
////    val runnable = Runnable {
////        mScaViewModel.getCheckTransactions()
////    }
//
    override fun getDataBindingConfig(): DataBindingConfig {
//        mAdapter = ChargeStatusAdapter(requireContext())
//
//////        事件监听  启动
//        mScaViewModel.chargingSchedule.observe(this, Observer {
//            //            mScaViewModel.getCheckTransactions()
//            dismissLoading()
//
//        })
////        mScaViewModel.chargingScheduleUpdate.observe(this, Observer {
////            dismissLoading()
////            mScaViewModel.getCheckTransactions()
////        })
////        mScaViewModel.chargingScheduleDelete.observe(this, Observer {
////            dismissLoading()
////            mScaViewModel.getCheckTransactions()
////        })
////        mScaViewModel.autoSwitch.observe(this, Observer {
////            dismissLoading()
////            mScaViewModel.getCheckTransactions()
////        })
////
//        mScaViewModel.remoteStop.observe(this, Observer {
//            dismissLoading()
//            mScaViewModel.getCheckTransactions()
//        })
//
//
////                showLoading()
////                mScaViewModel.remoteStop(
////                    RequestChargeChange(
////                        connectorPk = item?.connectorPk
////                    )
////                )
//
        return DataBindingConfig(R.layout.fragment_scharge_status_list, mScaViewModel)
//            .addBindingParam(
//                BR.click,
//                ClickProxy()
//            )
//            .addBindingParam(
//                BR.adapter,
//                mAdapter
//            )
    }
//
//    override fun onNetworkStateChanged(netState: NetState?) {
//        super.onNetworkStateChanged(netState)
//        dismissLoading()
//        h.removeCallbacks(runnableNet)
//    }
//
//    inner class ClickProxy {
//        fun back() {
//            nav().navigateUp()
//        }
//
//        fun refresh() {
//
//        }
//
//
//    }
//
//
////    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
////        super.onCreate(savedInstanceState)
////
////
//////        rv.postDelayed(runnable, 60 * 1000)
//////        mScaViewModel.isConnectSocket.observe(this, EventObserver {
//////            if (it) {
//////
//////            } else {
//////
//////            }
//////        })
////
////    }
//
//    override fun onStart() {
//        super.onStart()
//
////        val text =
////            "{\"code\":\"200\",\"data\":{\"api\":\"remoteStart\",\"message\":\"Remote start [ lijk ] success.\"},\"message\":\"\",\"success\":\"true\"}"
////        val gsonObjects =
////            GsonUtils.getGson().fromJson<RemoteResp>(text, RemoteResp::class.java)
////        val gsonObject =
////            Gson().fromJson<RemoteDataRespBean>(text, RemoteDataRespBean::class.java)
//        if (mScaViewModel.checkTransLiveData != null) {
//            mScaViewModel.checkTransLiveData.observe(
//                this,
//                Observer {
//                    dismissLoading()
//                    //                    it.add(CheckTransaction(connectorId = 1,transactionPk = "1111",startTime = "2020-07-10 08:50:50"))
//                    if (!it.isNullOrEmpty()) {
//                        iv_empty.visibility = View.GONE
//                        tv_empty.visibility = View.GONE
//                    } else {
//
//                        iv_empty.visibility = View.VISIBLE
//                        tv_empty.visibility = View.VISIBLE
//                    }
//                    mScaViewModel.listCheckTransaction.value = it
//                    mAdapter.notifyDataSetChanged()
//                }
//            )
//            Handler().postDelayed(
//                Runnable {
//
//                    connectSocket()
//                }
//            ,
//                3000
//            )
//        } else {
//
//        }
//    }
//
//
//    fun connectSocket() {
//        RxWebSocket.get(url) //RxLifecycle : https://github.com/dhhAndroid/RxLifecycle
//            .compose(RxLifecycle.with(this).bindToLifecycle())
//            .subscribe(object : WebSocketSubscriber() {
//                override fun onOpen(webSocket: WebSocket) {
//                    Log.d("sss", "onOpen1:")
//                    mScaViewModel.getCheckTransactions()
//                    if (!iv_empty.isVisible)
//                        handler.postDelayed(runnable, 60 * 1000)
////
////                    mScaViewModel.isConnectSocket.postValue(true)
//                }
//
//                override fun onMessage(text: String) {
//                    Log.d("ssss", "返回数据:$text")
//
//                    h.removeCallbacks(runnableNet)
//                    dismissLoading()
//                    try {
//
//                        val gsonObjects =
//                            Gson().fromJson<RemoteDataRespBean>(
//                                text,
//                                RemoteDataRespBean::class.java
//                            )
//                        gsonObjects?.data?.apply {
//                            Log.e("gson:", gsonObjects.toString())
//                            if ("true" == gsonObjects.success) {
//
//                                if (!percent.isNullOrEmpty()) {
//                                    fl_progress.visibility = View.VISIBLE
//                                    fl_progress.removeCallbacks(runnableNet)
//                                    progress.setDonut_progress(percent)
//                                    if (percent.toInt() >= 100) {
//                                        fl_progress.visibility = View.GONE
//                                        ToastUtils.showShort(getString(R.string.success))
//                                        showLoading()
//                                        mScaViewModel.getCheckTransactions()
//                                    }
//                                } else {
//                                    fl_progress.postDelayed(runnableNet, 30 * 1000)
//                                    fl_progress.visibility = View.VISIBLE
////                                    progress.visibility = View.GONE
//                                    progress.setDonut_progress("0")
//                                }
//                            } else {
//                                ToastUtils.showShort(this.message.toString())
//                                fl_progress.visibility = View.GONE
//
//                                mScaViewModel.getCheckTransactions()
//                            }
//                        }
//                    } catch (e: Exception) {
//                        dismissLoading()
//                        ToastUtils.showShort("something error")
//                        fl_progress.visibility = View.GONE
//                        mScaViewModel.getCheckTransactions()
//                    }
//
//                }
//
//                override fun onMessage(byteString: ByteString) {
//                    Log.d("ssss", "返回数据:")
//
//                    dismissLoading()
//                }
//
//
//                override fun onReconnect() {
//                    mScaViewModel.isConnectSocket.postValue(true)
//                    Log.d("ssss", "重连:")
//                    dismissLoading()
//                }
//
//                override fun onClose() {
//                    h.removeCallbacks(runnableNet)
//                    mScaViewModel.isConnectSocket.postValue(false)
//                    Log.d("ssss", "onClose:")
//                    dismissLoading()
//                }
//            })
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        stopSocket()
//        handler.removeCallbacks(runnable)
//        h.removeCallbacks(runnableNet)
//
//    }
//
//
//    fun stopSocket() {
//        val subscription = RxWebSocket.get(url).subscribe()
//        //注销
//        if (subscription != null && !subscription.isDisposed) {
//
//            Log.e("dispose", "dispose")
//            subscription.dispose()
//        }
//    }
//
//
//    //将两个选择时间的dialog放在该函数中
//    private fun showDialogPick(item: CheckTransaction) {
//        var time = ""
//        val timeLong = 0
//        //获取Calendar对象，用于获取当前时间
//        val calendar: Calendar = Calendar.getInstance()
//        val year: Int = calendar.get(Calendar.YEAR)
//        val month: Int = calendar.get(Calendar.MONTH)
//        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
//        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
//        val minute: Int = calendar.get(Calendar.MINUTE)
//        //实例化TimePickerDialog对象
//        val timePickerDialog =
//            TimePickerDialog(
//                requireContext(),
//                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//                    //选择完时间后会调用该回调函数
////                    time += " $hourOfDay:$minute:00"
////                    //最终选择的时间 todo date
////                    if (DateUtil.isBeforeNow(DateUtil.formatTimeStamp(time.toString()))) {
////                        ToastUtils.showShort("date is illegal")
////                    } else {
//////                        ;是否自动切换, 如果是电网充电, 该字段为空.
////                        var auto: Int? = null
////                        item.powerWay?.apply {
////                            auto = if (item.auto != null && item.auto!!) 1 else 2
////                        }
////
////                        val t = time.toDateUTC()
////                        mScaViewModel.setChargingScheduleUpdate(
////                            RequestCharge(
////                                powerWay = if (item.powerWay != null && item.powerWay!!) "1" else "0",
////                                beginChargingDatetime = t,
////                                chargingSchedulePk = item.chargingSchedulePk,
////                                auto = auto
////                            )
////                        )
////                    }
//
//                }, hour, minute, true
//            )
//
//        //实例化DatePickerDialog对象
//        val datePickerDialog =
//            DatePickerDialog(
//                requireContext(),
//                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    //选择完日期后会调用该回调函数
//                    time = "$year-${(monthOfYear + 1)}-$dayOfMonth"
//                    //选择完日期后弹出选择时间对话框
//                    timePickerDialog.show()
//                }, year, month, day
//            )
//
//
//        //弹出选择日期对话框
//        datePickerDialog.show()
//    }

}