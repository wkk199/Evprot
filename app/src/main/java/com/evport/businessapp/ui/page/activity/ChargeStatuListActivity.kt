package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BuildConfig
import com.evport.businessapp.OnMessageList
import com.evport.businessapp.R
import com.evport.businessapp.BR
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.ChargeStatusAdapter
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.utils.DateUtil
import com.evport.businessapp.utils.LiveBus
import com.google.gson.Gson
import com.kunminx.architecture.utils.SPUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_charge_statu_list.*
import org.jetbrains.anko.toast

class ChargeStatuListActivity : BaseActivity() {
//    val pk by lazy {
//        intent.getStringExtra("pk")?:"de"
//    }

    private lateinit var mScaViewModel: ScanViewModel
    private lateinit var mAdapter: ChargeStatusAdapter

    val url =
        Configs.WEB_BASE_URL.plus(SPUtils.getInstance().getString(Configs.EMAIL)).plus("_pcApp")

//    private val chargeBoxPk by lazy {
//        arguments?.getString(SCANDATA,"305278015280128000")
//    }

    fun refreshDataSelf() {
        val i = allList
        handler.removeCallbacksAndMessages(null)
        if (!i.isNullOrEmpty()) {
            getCheckTransactions(i[position].transactionPk.toString())
            handler.postDelayed({
                refreshDataSelf()
            }, 15 * 1000)
        }
        toastT("充电页面-自动刷新数据-时间戳：${DateUtil.getNow()}")
    }

    fun toastT(srt: String) {
        if (BuildConfig.DEBUG)
            toast(srt)
    }

    val handler = Handler()


    var transactionPk = ""

    var h = Handler()
    var runnableNet = Runnable {
        dismissLoading()
        fl_progress.isVisible = false
        //
        AlertDialog
            .Builder(this)
            .setTitle("Operation timed out")
            .setMessage("The operation may be successful, please click confirm to return to the previous page")
            .setPositiveButton("confirm") { p1, p2 ->
                finish()
            }
            .create().show();
    }

    override fun initViewModel() {
        mScaViewModel = getActivityViewModel(ScanViewModel::class.java)

    }


    fun hideToolbar() {
//        toolbar.isVisible = false
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = ChargeStatusAdapter(this).apply {
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
//        sharedViewModel.refreshNav2.observe(this, EventObserver {
//            if (it){
//                showLoading()
//                getCheckTransactions()
//            }
//        })
        mScaViewModel.remoteStop.observe(this, Observer {

            fl_progress.visibility = View.VISIBLE
            fl_progress.postDelayed(runnableNet, 20 * 1000)
            dismissLoading()
        })


        return DataBindingConfig(R.layout.activity_charge_statu_list, mScaViewModel)
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
            finish()
        }


    }

    var allList = ArrayList<CheckTransaction>()

    fun getCheckTransactions() {

        object :
            NetworkBoundResource<MutableList<CheckTransaction>>(networkStatusCallback = object :
                NetworkStatusCallback<MutableList<CheckTransaction>> {

                override fun onSuccess(data: MutableList<CheckTransaction>?) {

                    dismissLoading()
//                    todo del
//                    it.add(CheckTransaction(transactionPk = "1111",startTime = "2020-07-10 08:50:50"))
                    if (!data.isNullOrEmpty()) {
                        iv_empty.visibility = View.GONE
                        tv_empty.visibility = View.GONE
                    } else {

                        iv_empty.visibility = View.VISIBLE
                        tv_empty.visibility = View.VISIBLE
                    }

                    allList.clear()
                    data?.reversed()?.let { allList.addAll(it) }
                    mScaViewModel.listCheckTransaction.value = allList
                    mAdapter.notifyDataSetChanged()
                }

                override fun onFailure(message: String) {
                    ToastUtils.showLong(message)
                    dismissLoading()

                }

            }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<CheckTransaction>>> {
                return SingletonFactory.apiService.getCheckTransactions(ReqDefault())
            }
        }
    }

    override fun onStart() {
        super.onStart()

//        val text =
//            "{\"code\":\"200\",\"data\":{\"api\":\"remoteStart\",\"message\":\"Remote start [ lijk ] success.\"},\"message\":\"\",\"success\":\"true\"}"
//        val gsonObjects =
//            GsonUtils.getGson().fromJson<RemoteResp>(text, RemoteResp::class.java)
//        val gsonObject =
//            Gson().fromJson<RemoteDataRespBean>(text, RemoteDataRespBean::class.java)

        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == OnMessageList) {

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
                                    ToastUtils.showShort("success")
                                    showLoading()
                                    toastT("充电页面-socket返回成功数据-刷新数据-时间戳：${DateUtil.getNow()}")
                                    getCheckTransactions()
                                    if (api == "remoteStop") {
                                        if (transactionPk.isNotEmpty()) {
//
                                            val intent = Intent(
                                                this@ChargeStatuListActivity,
                                                RecordDetailActivity::class.java
                                            )
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            intent.putExtra("record", transactionPk)
                                            startActivity(intent)
//                                            startActivity<RecordDetailActivity>(
//                                                Pair("record", transactionPk)
//                                            )
                                        }
                                    }
                                }

                            } else {
                                fl_progress.postDelayed(runnableNet, 15 * 1000)
                                fl_progress.visibility = View.VISIBLE
//                                    progress.visibility = View.GONE
                                DonutProgress.setDonut_progress("0")
                            }
                        } else {
                            ToastUtils.showShort(this.message.toString())
                            fl_progress.visibility = View.GONE
                            toastT("充电页面-socket返回失败数据-刷新数据-时间戳：${DateUtil.getNow()}")

//                            getCheckTransactions()
                        }
                    }
                } catch (e: Exception) {
                    dismissLoading()
                    ToastUtils.showShort("something error:" + e.message)
                    fl_progress.visibility = View.GONE
                    toastT("充电页面-socket数据错误-刷新数据-时间戳：${DateUtil.getNow()}")
//                    getCheckTransactions()
                }
            } else if (it.type == "onClose") {
                h.removeCallbacks(runnableNet)
            }
        })
        getCheckTransactions()

        rv.addOnScrollListener( object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView != null && recyclerView.childCount > 0) {
                    try {
//                        val  currentPosition =((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                        position = (recyclerView.getChildAt(0).layoutParams as RecyclerView.LayoutParams).bindingAdapterPosition
                        Log.e("=====currentPosition", "" + 0)
                    } catch (e:Exception) {

                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        })
        rv.postDelayed({
            refreshDataSelf()
        },2000)
    }


    override fun onResume() {
        super.onResume()
        refreshDataSelf()
    }
    var position = 0

    override fun onStop() {
        super.onStop()
        Log.e("frg,", "stop")

        handler.removeCallbacksAndMessages(null)

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        h.removeCallbacks(runnableNet)

    }


    fun getCheckTransactions(pk:String) {
        object : NetworkBoundResource<MutableList<CheckTransaction>>(networkStatusCallback = object :
            NetworkStatusCallback<MutableList<CheckTransaction>> {

            override fun onSuccess(data: MutableList<CheckTransaction>?) {
                if (data.isNullOrEmpty()) {
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
                    mScaViewModel.listCheckTransaction.value = allList
                    mAdapter.notifyDataSetChanged()
                    iv_empty.isVisible = allList.size==0
                    tv_empty.isVisible = allList.size==0
                }
            }

            override fun onFailure(message: String) {
                ToastUtils.showLong(message)

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<MutableList<CheckTransaction>>> {
                return SingletonFactory.apiService.getCheckTransactions(ReqDefaultPk(transactionPk = pk))
            }
        }
    }
fun stopCharge(){
    val popupView =
        XPopup.Builder(this) //                        .hasBlurBg(true)
            //                         .dismissOnTouchOutside(false)
            //                         .autoDismiss(false)
            //                        .popupAnimation(PopupAnimation.NoAnimation)
            //                        .isLightStatusBar(true)
            //                        .hasNavigationBar(false)

          .asConfirm(
                "stop", "Are you sure to finish charging?",
                "Cancel", "Confirm",
                OnConfirmListener { toast("click confirm") }, null, false
            )
    //popupView.contentTextView.setTextColor(Color.RED)
    popupView.cancelTextView.setTextColor(resources.getColor(R.color.red))
    popupView.confirmTextView.setTextColor(resources.getColor(R.color.colorPrimary))
    popupView.show()
}

}