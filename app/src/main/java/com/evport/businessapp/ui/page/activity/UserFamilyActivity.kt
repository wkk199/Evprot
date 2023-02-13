package com.evport.businessapp.ui.page.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.kunminx.architecture.utils.SPUtils
import com.evport.businessapp.BR
import com.evport.businessapp.OnMessageUserFamily
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.R
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.FamilyListAdapter
import com.evport.businessapp.ui.page.adapter.HomeStationDeviceAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.*
import com.stripe.android.CustomerSession
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_user_family.*
import kotlinx.android.synthetic.main.activity_user_family.fl_progress
import kotlinx.android.synthetic.main.activity_user_family.progress
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.collections.ArrayList

const val HOME_PK = "HOME_PK"

class UserFamilyActivity : BaseActivity() {
    private var canLoadMore = true
    private lateinit var mStationViewModel: StationViewModel


    override fun initViewModel() {
        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    var connectorPk = ""
    var schedulePk = ""
    lateinit var adapter: FamilyListAdapter
    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = FamilyListAdapter(this).apply {

            setOnItemClickListener { item, position ->
                when (item.homePk) {
                    "-1" -> {
                        startActivity<CreateEditFamilyActivity>()
                    }
                    "-2" -> {
                        startActivity<CreateEditFamilyActivity>(
                            Pair(HOME_PK, selectItem?.homePk)
                        )
                    }
                    else -> {
                        selectItem = item
                        mStationViewModel.FamilyTitle.set(selectItem?.homeName)
                        mStationViewModel.FamilyBg.set(selectItem?.img)
                        notifyDataSetChanged()
                        getDeviceData()
                    }
                }
                menuRL.isVisible = false
            }
        }
        return DataBindingConfig(R.layout.activity_user_family, mStationViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                adapter
            )
            .addBindingParam(
                BR.adapterDevice,
                HomeStationDeviceAdapter(baseContext).apply {
                    childClick = { item ->

                        isCss = item.type != null && (item.type == "CCS1" || item.type == "CCS2")
                        connectorPk = item.connectorPk.toString()
                        if (item.btnStr() == "Start") {
                            rl_charge.isVisible = true
                        } else {
                            remoteStart()
                        }
                        notifyDataSetChanged()
                    }

                    childUpdateClick = { it, start ->
                        if (start && it.homeSchedule?.transactionPk != null) {
//                            toast("can not modify start time")

                        } else {
                            schedulePk = it.homeSchedule?.homeSchedulePk ?: ""
                            this@UserFamilyActivity.connectorPk = it.connectorPk ?: ""
                            if (start) {
                                it.homeSchedule?.stopTime?.let {st->
                                    endTime = DateUtil.formatTimeStamp(it.homeSchedule?.stopTimeUtc()).toString()
                                }
                                showDateDialogPick(true, it.homeSchedule?.startTimeUtc())
                            } else {
                                it.homeSchedule?.startTime?.let {st->
                                    startTime = DateUtil.formatTimeStamp(it.homeSchedule?.startTimeUtc()).toString()
                                }
                                showDateDialogPick(false, it.homeSchedule?.stopTimeUtc())

                            }


                        }

                    }

                    childDelClick = { it, start ->

                        if (start && it.homeSchedule?.transactionPk != null) {
//                            toast("can not modify start time")
                        } else {
                            schedulePk = it.homeSchedule?.homeSchedulePk ?: ""
                            this@UserFamilyActivity.connectorPk = it.connectorPk ?: ""
                            if (start) {
                                it.homeSchedule?.stopTime?.let {st->
                                    endTime = DateUtil.formatTimeStamp(it.homeSchedule?.stopTimeUtc()).toString()
                                }
                                startTime = ""
                            } else {
                                it.homeSchedule?.startTime?.let {st->
                                    startTime = DateUtil.formatTimeStamp(it.homeSchedule?.startTimeUtc()).toString()
                                }
                                endTime = ""

                            }
                            if (startTime.isBlank()&&endTime.isBlank()){
                                deleteSchedule()
                            }else{
                                updateSchedule()
                            }

                        }

                    }



                    likeClick = { item, p ->
                        this.isExpand = !this.isExpand
                        notifyDataSetChanged()
                    }

                    delClick = { item, position ->
                        unBindDevice(item?.homeChargeBoxPk.toString())
                    }

                }
            )
    }

    override fun onResume() {
        super.onResume()
        getFamilyList()
    }

    var lat = 0.0f
    var lng = 0.0f
    val h = Handler()
    var runnableNet = Runnable {
        dismissLoading()
        //
        AlertDialog
            .Builder(this)
            .setTitle("Operation timed out")
            .setMessage("The operation may be successful, please click confirm to return to the previous page")
            .setPositiveButton("confirm") { p1, p2 ->
                finish()
            }
            .create().show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        lat = baseContext.getLat()
//        lng = baseContext.getLng()

        adapter.selectItem = getDefaultFamilyItem()
        adapter.selectItem?.apply {
            mStationViewModel.FamilyTitle.set(homeName)
            mStationViewModel.FamilyBg.set(img)
        }
        putDefaultFamilyItem(null)
        getFamilyList()
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            pageNumDevice = 1
            canLoadMore = true
            getFamilyList()
            refreshLayout.isRefreshing = false
        }

//        list_device.addOnScrollListener(object : onLoadMoreListener() {
//            override fun onLoading(countItem: Int, lastItem: Int) {
//                if (canLoadMore) {
//
//                    pageNumDevice++
//                    getDeviceData()
//
//                } else {
////                    toast("no more data")
//                }
//            }
//        })
        LiveBus.getInstance().observeEvent(this, Observer { t ->
            if (t.type == Configs.EVENT_LOGOUT) {
                logOut()
            }
            if (t.type == "ScanResult") {
                bindDevice(t.value.toString())
            }
        })


        LiveBus.getInstance().observeEvent(this, androidx.lifecycle.Observer {

            if (it.type == OnMessageUserFamily) {
                dismissLoading()
                h.removeCallbacks(runnableNet)
                try {
                    val gsonObjects =
                        Gson().fromJson<RemoteDataRespBean>(
                            it.value.toString(),
                            RemoteDataRespBean::class.java
                        )
//                            GsonUtils.getGson().fromJson<RemoteResp>(text, RemoteResp::class.java)
                    Log.e("gson:", gsonObjects.toString())
                    gsonObjects?.data?.apply {

                        if ("true" == gsonObjects.success) {

                            if (!percent.isNullOrEmpty()) {
                                fl_progress.visibility = View.VISIBLE
                                fl_progress.removeCallbacks(runnableNet)
                                progress.setDonut_progress(percent)
                                if (percent.toInt() >= 100) {
                                    fl_progress.visibility = View.GONE
                                    ToastUtils.showLong(getString(R.string.success))

//                                    getData()
                                    val intent = Intent(
                                        this@UserFamilyActivity,
                                        ChargeStatuListActivity::class.java
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                }
                            } else {
                                fl_progress.visibility = View.VISIBLE
                                progress.setDonut_progress("0")
                                showProgress()
                            }
                        } else {
                            ToastUtils.showLong(gsonObjects.data?.message.toString())
                            fl_progress.visibility = View.GONE
//                            getData()
                        }
                    }
                } catch (e: Exception) {
                    fl_progress.visibility = View.GONE
                    ToastUtils.showLong("something error")
//                    getData()


                }

            } else if (it.type == "onClose") {
                h.removeCallbacks(runnableNet)
            }

        })
    }

    var isCss = false
    var outTime = 20
    fun showProgress() {
        fl_progress.isVisible = true
        outTime = if (isCss) {
            90
        } else {
            20
        }
    }

    var startSwitch = false
    var endSwitch = false

    inner class ClickProxy {
        fun back() {

            finish()
        }

        fun setTime() {
            tv_time_end.text = ""
            tv_time_start2.text = ""
            startTime = ""
            endTime = ""
            dismissCharge()
            startSwitch = false
            endSwitch = false
            ll_time_start0.isVisible = startSwitch
            tv_time_start.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.icon_switch_off,
                0
            )
            tv_time.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.icon_switch_off,
                0
            )
            ll_time_stop.isVisible = endSwitch
            btn_ok.isEnabled = false
            rl_setTime.isVisible = true
        }

        fun chargeNow() {
            remoteStart()
            dismissCharge()
        }

        fun dismissCharge() {
            rl_charge.isVisible = false
        }

        fun dismissSetTime() {
            rl_setTime.isVisible = false
        }

        fun startSwitch() {
            startSwitch = !startSwitch
            ll_time_start0.isVisible = startSwitch
            if (startSwitch) {
                tv_time_start.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_switvh_on,
                    0
                )
            } else {
                tv_time_start.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_switch_off,
                    0
                )
                tv_time_start2.text = ""
                startTime = ""
            }
        }

        fun startTime() {
            showDateDialogPick(true)
        }

        fun endSwitch() {
            endSwitch = !endSwitch
            ll_time_stop.isVisible = endSwitch
            if (endSwitch) {
                tv_time.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_switvh_on,
                    0
                )
            } else {
                tv_time.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_switch_off,
                    0
                )
                tv_time_end.text = ""
                endTime = ""
            }
        }

        fun endTime() {
            showDateDialogPick(false)
        }

        fun ok() {
            if (schedulePk.isBlank()) {
                addSchedule()
            } else {
                updateSchedule()
            }
        }

        fun bind() {
            if (adapter.selectItem == null || adapter.selectItem?.homePk == null) {
                ToastUtils.showLong("no family selected")
            }

            adapter.selectItem?.homePk?.apply {


                RxPermissions(this@UserFamilyActivity)
                    .request(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                    .subscribe { it ->
                        if (it) {

                            startActivity<ScanActivity>()

                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this@UserFamilyActivity,
                                    Manifest.permission.CAMERA
                                )
                            ) {
                                try {
                                    ToastUtils.showLong("必须同意权限才能使用扫一扫")
                                } catch (e: Exception) {

                                }
                            } else {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts(
                                        "package",
                                        this@UserFamilyActivity.packageName,
                                        null
                                    )
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    }
            }
        }

        fun menu() {
            changeMenu()
            dismissCharge()
            dismissSetTime()
        }

        fun menuClear() {
            menuRL.isVisible = false
        }
    }

    fun changeMenu() {
        menuRL.isVisible = !menuRL.isVisible
    }

    var startTime = ""
    var endTime = ""


    //将两个选择时间的dialog放在该函数中
    private fun showDateDialogPick(start: Boolean) {
        var time = ""
        val timeLong = 0
        //获取Calendar对象，用于获取当前时间
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        //实例化TimePickerDialog对象
        val timePickerDialog =
            TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    //选择完时间后会调用该回调函数
                    time += " $hourOfDay:$minute:59"
                    //最终选择的时间
                    if (DateUtil.isBeforeNow(DateUtil.formatTimeStamp(time))) {
                        ToastUtils.showLong("date is illegal")
                    } else {


                        val t = DateUtil.formatTimeStamp(time).toString()
                        if (start) {
                            startTime = t
                            tv_time_start2.text = DateUtil.formatTimeStampStr(time)
                        } else {
                            endTime = t
                            tv_time_end.text = DateUtil.formatTimeStampStr(time)
                        }

                        btn_ok.isEnabled  = endTime.isNotBlank()||startTime.isNotBlank()

//                        mScaViewModel.setChargingScheduleUpdate(
//                            RequestCharge(
//                                powerWay = if (item.powerWay != null && item.powerWay!!) "1" else "0",
//                                beginChargingDatetime = t,
//                                chargingSchedulePk = item.chargingSchedulePk,
//                                auto = auto
//                            )
//                        )
                    }

                }, hour, minute, true
            )

        //实例化DatePickerDialog对象
        val datePickerDialog =
            DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    //选择完日期后会调用该回调函数
                    time = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                    //选择完日期后弹出选择时间对话框
                    timePickerDialog.show()
                }, year, month, day
            )

        //弹出选择日期对话框
        datePickerDialog.show()
    }


    //将两个选择时间的dialog放在该函数中
    private fun showDateDialogPick(start: Boolean, initTime: String? = null) {
        var time = ""
        val timeLong = DateUtil.stringToDate(initTime)

        //获取Calendar对象，用于获取当前时间
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = timeLong
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        //实例化TimePickerDialog对象
        val timePickerDialog =
            TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    //选择完时间后会调用该回调函数
                    time += " $hourOfDay:$minute:59"
                    //最终选择的时间
                    if (DateUtil.isBeforeNow(DateUtil.formatTimeStamp(time))) {
                        ToastUtils.showLong("date is illegal")
                    } else {


                        val t = DateUtil.formatTimeStamp(time).toString()
                        if (start) {
                            startTime = t
                        } else {
                            endTime = t
                        }
                        if (schedulePk.isNotBlank())
                            updateSchedule()
                        else {
                            addSchedule()
                        }
//                        mScaViewModel.setChargingScheduleUpdate(
//                            RequestCharge(
//                                powerWay = if (item.powerWay != null && item.powerWay!!) "1" else "0",
//                                beginChargingDatetime = t,
//                                chargingSchedulePk = item.chargingSchedulePk,
//                                auto = auto
//                            )
//                        )
                    }

                }, hour, minute, true
            )

        //实例化DatePickerDialog对象
        val datePickerDialog =
            DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    //选择完日期后会调用该回调函数
                    time = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                    //选择完日期后弹出选择时间对话框
                    timePickerDialog.show()
                }, year, month, day
            )

        //弹出选择日期对话框
        datePickerDialog.show()
    }


    private var allListDevice = ArrayList<HomeStationBean>()

    var pageNumDevice = 1
    fun getDeviceData() {
        if (adapter.selectItem == null || adapter.selectItem?.homePk == null) {
            return
        }
        startLoading()
        object : NetworkBoundResource<List<HomeStationBean>>(networkStatusCallback = object :
            NetworkStatusCallback<List<HomeStationBean>> {

            override fun onSuccess(data: List<HomeStationBean>?) {
                data?.apply {
                    allListDevice.clear()
                    allListDevice.addAll(data)
                    mStationViewModel.deviceListHomeBean?.value = allListDevice
                }
                dismissLoading()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<HomeStationBean>>> {

                return SingletonFactory.apiService.getHomeChargeBox(HomePkBean(adapter.selectItem?.homePk.toString()))
            }
        }
    }


    fun remoteStart() {
        showLoading()
        object : NetworkBoundResource<RemoteStartRep>(networkStatusCallback = object :
            NetworkStatusCallback<RemoteStartRep> {

            override fun onSuccess(data: RemoteStartRep?) {
                dismissLoading()
                data?.apply {
                    if (status == "Success") {
                        showLoading()
//                        Handler().postDelayed({
//                            startActivity<ChargeStatuListActivity>()
//                        },3000)
                    }
                }

            }

            override fun onFailure(message: String) {

                dismissLoading()
                Log.e("hm----onFailure", message);
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<RemoteStartRep>> {
                return SingletonFactory.apiService.remoteStartHome(RequestChargeChange(connectorPk = connectorPk))
            }
        }

    }


    fun bindDevice(chargeBox: String) {
        startLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {

                ToastUtils.showLong(getString(R.string.success))
                getDeviceData()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.bindCharge(
                    HomePkBean(
                        homePk = adapter.selectItem?.homePk.toString(),
                        chargeBox = chargeBox
                    )
                )
            }
        }
    }

    fun unBindDevice(homeChargeBoxPk: String) {
        startLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {

                ToastUtils.showLong(getString(R.string.success))

                getDeviceData()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.unBindCharge(HomePkBean(homeChargeBoxPk = homeChargeBoxPk))
            }
        }
    }

    fun getFamilyList() {
        object : NetworkBoundResource<ArrayList<FamilyList>>(networkStatusCallback = object :
            NetworkStatusCallback<ArrayList<FamilyList>> {

            override fun onSuccess(data: ArrayList<FamilyList>?) {
                if (data.isNullOrEmpty()) {
//                    startActivity<CreateEditFamilyActivity>()
                    adapter.selectItem = null
                    putDefaultFamilyItem(null)

                    mStationViewModel.FamilyTitle.set("")
                    mStationViewModel.FamilyBg.set("")
                    val list = ArrayList<FamilyList>()
                    list.add(FamilyList("-1", null, "Add Home"))
                    mStationViewModel.familyList.value = list
                    mStationViewModel.familyList.value = list
                    allListDevice.clear()
                    mStationViewModel.deviceListHomeBean?.value = allListDevice
                } else {

                    data.add(FamilyList("-1", null, "Add Home"))
                    data.add(FamilyList("-2", null, "Home Setting"))
                    mStationViewModel.familyList.value = data
                    if (adapter.selectItem == null || !data.contains(adapter.selectItem)) {
                        adapter.selectItem = data[0]
                        mStationViewModel.FamilyTitle.set(data[0].homeName)
                        mStationViewModel.FamilyBg.set(data[0].img)
                    }
                    getDeviceData()
                }
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<FamilyList>>> {
                return SingletonFactory.apiService.getHomeList()
            }
        }
    }

    fun addSchedule() {
        if (startTime.isNotBlank()&&endTime.isNotBlank()&&startTime.toLong()>=endTime.toLong()){
            ToastUtils.showLong("stop time must be greater than start time")
            return
        }
        startLoading()

        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {

                schedulePk = ""
                if (startTime.isNotBlank()) {
                    ToastUtils.showLong(getString(R.string.success))
                    dismissLoading()
                    ClickProxy().dismissSetTime()
                    getDeviceData()

                } else {
                    ClickProxy().dismissSetTime()
                    getDeviceData()
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                schedulePk = ""
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.addSchedule(
                    AddSchedule(
                        connectorPk = connectorPk,
                        startTime = startTime, stopTime = endTime
                    )
                )
            }
        }
    }

    fun updateSchedule() {
        if (startTime.isNotBlank()&&endTime.isNotBlank()&&startTime.toLong()>=endTime.toLong()){
            ToastUtils.showLong("stop time must be greater than start time")
            return
        }
        startLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                ToastUtils.showLong("success!")
                schedulePk = ""
                getDeviceData()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()
                schedulePk = ""
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.updateSchedule(
                    AddSchedule(
                        homeSchedulePk = schedulePk,
                        startTime = startTime,
                        stopTime = endTime
                    )
                )
            }
        }
    }

    fun deleteSchedule() {
        startLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                ToastUtils.showLong("delete Success")
                schedulePk = ""
                getDeviceData()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                dismissLoading()
                schedulePk = ""
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.deleteSchedule(AddSchedule(homeSchedulePk = schedulePk))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        putDefaultFamilyItem(adapter.selectItem)
    }

    private fun logOut() { //                    登出
        SPUtils.getInstance().put(Configs.TOKEN, "")
        CustomerSession.endCustomerSession()
        val homeFilter = getHomeFilterData() ?: HomeFilter()
        if (!homeFilter.isDefault)
            saveHomeFilterData(HomeFilter())
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}