package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kunminx.architecture.ui.callback.EventObserver
import com.evport.businessapp.OnMessageHome
import com.evport.businessapp.data.bean.CheckTransaction
import com.evport.businessapp.data.bean.RemoteDataRespBean
import com.evport.businessapp.data.bean.ReqDefaultPk
import com.evport.businessapp.data.bean.RequestChargeChange
import com.evport.businessapp.*
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.ChargeStatusAdapter
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.utils.DateUtil
import com.evport.businessapp.utils.LiveBus
import com.gyf.immersionbar.ImmersionBar
import com.kunminx.architecture.utils.SPUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_charge_statusv2_list.*
import kotlinx.android.synthetic.main.fragment_charge_statusv2_list.DonutProgress
import kotlinx.android.synthetic.main.fragment_charge_statusv2_list.fl_progress
import kotlinx.android.synthetic.main.fragment_charge_statusv2_list.iv_empty
import kotlinx.android.synthetic.main.fragment_charge_statusv2_list.tv_empty
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.toast
import java.lang.Runnable
import kotlin.collections.ArrayList


class ChargeStatuListV2Fragment : BaseFragment() {

    private lateinit var mScaViewModel: ScanViewModel
    private lateinit var mAdapter: ChargeStatusAdapter

    var position = 0
//    private val chargeBoxPk by lazy {
//        arguments?.getString(SCANDATA,"305278015280128000")
//    }


    fun toastT(srt: String) {
        if (BuildConfig.DEBUG)
            toast(srt)
    }

    fun refreshDataSelf() {
        toastT("主页自动刷新数据-时间戳：${DateUtil.getNow()}")

        stopHandler()
        val i = allList
        if (!i.isNullOrEmpty() && !SPUtils.getInstance().getString(Configs.TOKEN).isNullOrEmpty()) {
            getCheckTransactions(i[position].transactionPk.toString())
            handler.postDelayed({
                refreshDataSelf()
            }, 15 * 1000)
        }
    }

    val handler = Handler()

    var transactionPk = ""

    var h = Handler()
    var runnableNet = Runnable {
        dismissLoading()

        fl_progress.isVisible = false
        //
        AlertDialog
            .Builder(requireContext())
            .setTitle(R.string.Operation_timed_out)
            .setMessage(R.string.The_operation_may_be)
            .setPositiveButton(R.string.Confirm) { p1, p2 ->
                nav().navigateUp()
            }
            .create().show();
    }


    override fun initViewModel() {
        mScaViewModel = getActivityViewModel(ScanViewModel::class.java)

    }

    fun refreshDate() {
        showLoading()

        mScaViewModel.getCheckTransactions()
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = ChargeStatusAdapter(requireContext()).apply {
            stopClick = { item: CheckTransaction, p: Int ->
                transactionPk = item.transactionPk.toString()
                mScaViewModel.remoteStop(RequestChargeChange(transactionPk = transactionPk))
                showLoading()


            }
        }

////        事件监听  启动
        mScaViewModel.chargingSchedule.observe(this, Observer {
            dismissLoading()


        })
        sharedViewModel.refreshNav2.observe(this, EventObserver {
            if (it) {
                ImmersionBar.with(this).statusBarDarkFont(true).init()
                refreshDataSelf()
//                toastT("主页导航栏点击 刷新数据-时间戳：${DateUtil.getNow()}")
            }
        })
        sharedViewModel.stopAPP.observe(this, EventObserver {
            stopHandler()
        })
//
        mScaViewModel.remoteStop.observe(this, Observer {

            fl_progress.visibility = View.VISIBLE
            fl_progress.postDelayed(runnableNet, 20 * 1000)
            dismissLoading()

        })


//                showLoading()
//                mScaViewModel.remoteStop(
//                    RequestChargeChange(
//                        connectorPk = item?.connectorPk
//                    )
//                )

        return DataBindingConfig(R.layout.fragment_charge_statusv2_list, mScaViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                mAdapter
            )
    }


    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun refresh() {

            refreshDate()

            toastT("主页手动刷新数据-时间戳：${DateUtil.getNow()}")
        }


    }


//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState)
//
//
////        rv.postDelayed(runnable, 60 * 1000)
////        mScaViewModel.isConnectSocket.observe(this, EventObserver {
////            if (it) {
////
////            } else {
////
////            }
////        })
//
//    }

    override fun onStart() {
        super.onStart()

//        val text =
//            "{\"code\":\"200\",\"data\":{\"api\":\"remoteStart\",\"message\":\"Remote start [ lijk ] success.\"},\"message\":\"\",\"success\":\"true\"}"
//        val gsonObjects =
//            GsonUtils.getGson().fromJson<RemoteResp>(text, RemoteResp::class.java)
//        val gsonObject =
//            Gson().fromJson<RemoteDataRespBean>(text, RemoteDataRespBean::class.java)
        if (mScaViewModel.checkTransLiveData != null) {
            mScaViewModel.checkTransLiveData.observe(
                this,
                Observer { it ->
                    GlobalScope.launch(Dispatchers.IO) {
                        delay(1000)
                        withContext(Dispatchers.Main) {
                            dismissLoading()
                        }

                    }

                    if (!it.isNullOrEmpty()) {
                        iv_empty.visibility = View.GONE
                        tv_empty.visibility = View.GONE
                    } else {

                        iv_empty.visibility = View.VISIBLE
                        tv_empty.visibility = View.VISIBLE
                    }

                    if (recyclerView.onFlingListener==null){
                        Log.e("hm---绑定","绑定")
                        val snapHelper = PagerSnapHelper()
                        recyclerView.onFlingListener = null
                        snapHelper.attachToRecyclerView(recyclerView)
                    }
                    allList.clear()
                    allList.addAll(it)
                    mScaViewModel.listCheckTransaction.value = allList
                    mAdapter.notifyDataSetChanged()
                }
            )
        } else {

        }

        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == OnMessageHome) {
                h.removeCallbacks(runnableNet)
                dismissLoading()

                try {

                    val gsonObjects =
                        Gson().fromJson<RemoteDataRespBean>(
                            it.value.toString(),
                            RemoteDataRespBean::class.java
                        )
                    gsonObjects?.data?.apply {
                        Log.e("gson:", gsonObjects.toString())
                        if ("true" == gsonObjects.success) {

                            if (!percent.isNullOrEmpty()) {
                                fl_progress.visibility = View.VISIBLE
                                fl_progress.removeCallbacks(runnableNet)
                                DonutProgress.setDonut_progress(percent)
                                if (percent.toInt() >= 100) {
                                    fl_progress.visibility = View.GONE
                                    ToastUtils.showShort(getString(R.string.success))
                                    showLoading()

                                    toastT("主页socket返回成功 - 刷新数据-时间戳：${DateUtil.getNow()}")
                                    mScaViewModel.getCheckTransactions()
//                                            sharedViewModel.bindSuccess.postValue(true)

                                    if (api == "remoteStop") {
                                        if (transactionPk.isNotEmpty()) {
                                            val intent = Intent(
                                                requireContext(),
                                                RecordDetailActivity::class.java
                                            )
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            intent.putExtra("record", transactionPk)
                                            startActivity(intent)
//                                            requireContext().startActivity<RecordDetailActivity>(
//                                                Pair("record", transactionPk)
//                                            )
//                                                nav().navigate(R.id.action_global_recordDetailFragment, Bundle().also {
//                                                    it.putString("record", transactionPk)
//                                                })
                                        }
                                    }
                                }
                            } else {
                                fl_progress.postDelayed(runnableNet, 20 * 1000)
                                fl_progress.visibility = View.VISIBLE
//                                    progress.visibility = View.GONE
                                DonutProgress.setDonut_progress("0")
                            }
                        } else {
                            ToastUtils.showShort(this.message.toString())
                            fl_progress.visibility = View.GONE

                            toastT("主页socket返回失败 - 刷新数据-时间戳：${DateUtil.getNow()}")
//                            mScaViewModel.getCheckTransactions()
                        }
                    }
                } catch (e: Exception) {
                    dismissLoading()
                    ToastUtils.showShort(e.message)
                    e.printStackTrace()
                    fl_progress.visibility = View.GONE

                    toastT("主页socket返回数据有误 - 刷新数据-时间戳：${DateUtil.getNow()}")
//                    mScaViewModel.getCheckTransactions()
                }
            } else if (it.type == "onClose") {

                h.removeCallbacks(runnableNet)
            }
        })


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView != null && recyclerView.childCount > 0) {
                    try {
//                        val  currentPosition =((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                        position =
                            (recyclerView.getChildAt(0).layoutParams as RecyclerView.LayoutParams).bindingAdapterPosition
                        Log.e("=====currentPosition", "" + position)
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        })

        mScaViewModel.getCheckTransactions()
    }

    override fun onResume() {
        super.onResume()
        if (stop) {
            refreshDataSelf()
        }
    }

    var stop = false

    override fun onStop() {
        super.onStop()
        Log.e("frg,", "stop")
        stopHandler()
        stop = true
    }

    fun stopHandler() {
//        if (!AppUtils.isAppForeground()){
        handler.removeCallbacksAndMessages(null)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.e("frg,", "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e("frg,", "onDetach")

    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        h.removeCallbacks(runnableNet)

    }

    var allList = ArrayList<CheckTransaction>()


    fun getCheckTransactions(pk: String) {
        object :
            NetworkBoundResource<MutableList<CheckTransaction>>(networkStatusCallback = object :
                NetworkStatusCallback<MutableList<CheckTransaction>> {

                override fun onSuccess(data: MutableList<CheckTransaction>?) {
                    dismissLoading()

                    if (data.isNullOrEmpty()) {
                        if (allList.size > position)
                            allList.removeAt(position)
                        mScaViewModel.listCheckTransaction.value = allList
                        mAdapter.notifyDataSetChanged()

                    } else {
                        if (allList.size > position) {
                            var i = allList[position]
                            if (i.transactionPk == data[0].transactionPk) {
                                i.current = data[0].current
                                i.usedEnergy = data[0].usedEnergy
                                i.settingValue = data[0].settingValue
                                i.fee = data[0].fee
                                i.meterTimeStamp = data[0].meterTimeStamp
                                i.power = data[0].power
                                i.type = data[0].type
                                i.status = data[0].status
                                i.voltage = data[0].voltage
                                i.chargingSetting = data[0].chargingSetting
                            }

                            allList[position] = i
                        }
                        iv_empty.isVisible = allList.size == 0
                        tv_empty.isVisible = allList.size == 0
                        mScaViewModel.listCheckTransaction.value = allList
                        mAdapter.notifyDataSetChanged()

                    }
                }

                override fun onFailure(message: String) {


                }

            }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<CheckTransaction>>> {
                return SingletonFactory.apiService.getCheckTransactions(ReqDefaultPk(transactionPk = pk))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView();
    }


    fun initView() {


    }



}
