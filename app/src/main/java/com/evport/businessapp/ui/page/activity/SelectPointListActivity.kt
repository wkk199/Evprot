package com.evport.businessapp.ui.page.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.RequestCharge
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.SCANDATA
import com.evport.businessapp.ui.page.adapter.ChargeGunsAdapter
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.utils.DateUtil
import com.evport.businessapp.utils.gunStatus
import com.kunminx.architecture.domain.manager.NetState
import com.kunminx.architecture.utils.SPUtils
import kotlinx.android.synthetic.main.fragment_select_points_list.*
import java.util.*

class SelectPointListActivity : BaseActivity() {

    private lateinit var mScaViewModel: ScanViewModel
    private lateinit var mAdapter: ChargeGunsAdapter
    val url =
        Configs.WEB_BASE_URL.plus(SPUtils.getInstance().getString(Configs.EMAIL)).plus("_evie")

    private val chargeBoxPk by lazy {
        intent?.getStringExtra(SCANDATA)
    }

    override fun initViewModel() {
        mScaViewModel = getActivityViewModel(ScanViewModel::class.java)

    }

    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
        h.removeCallbacks(runnableNet)
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = ChargeGunsAdapter(this)
        return DataBindingConfig(R.layout.fragment_select_points_list, mScaViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                mAdapter
            )
    }


   val h = Handler()
    var runnableNet = Runnable {
        dismissLoading()
        mScaViewModel.requestChargeGun(mScaViewModel.requestScan)
        //
           AlertDialog
                   .Builder(this)
                   .setTitle("Operation timed out")
                   .setMessage("The operation may be successful, please click confirm to return to the previous page")
                   .setPositiveButton("confirm"){
                      p1,p2->
                       finish()
                   }
                   .create().show();
    }

    inner class ClickProxy {
        fun back() {
            finish()
        }

        fun setTime() {
            if (mAdapter.selectPosition < 0) {
                ToastUtils.showShort("please select charging gun first")
                return
            }
            showDialogPick()
        }

        fun chargeNow() {
            if (mAdapter.selectPosition < 0) {
                ToastUtils.showShort("please select charging gun first")
                return
            }
            if (mAdapter.selectItem.status != gunStatus.Plugged.name ) {
                ToastUtils.showShort("the gun is not Plugged")
                return
            }
           // connectSocket(false,"")
        }


        fun SolarPowerStorage() {
            p_sol.isSelected = true
            p_grid.isSelected = false

            mScaViewModel.selectSolarPower.set(true)
        }

        fun PowerGrid() {

            p_sol.isSelected = false
            p_grid.isSelected = true
            mScaViewModel.selectSolarPower.set(false)

        }


    }

    override fun onResume() {
        super.onResume()

        mScaViewModel.requestChargeGun(mScaViewModel.requestScan)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScaViewModel.chargeGunsLiveData.observe(
            this,
            Observer {

                mScaViewModel.listGuns.value = it
            }
        )
        mScaViewModel.requestScan?.chargeBoxPk = chargeBoxPk
        mScaViewModel.requestChargeGun(mScaViewModel.requestScan)

        mScaViewModel.remoteStart.observe(
            this,
            Observer {
//                dismissLoading()
                //                startActivity(Intent(this,MainActivity::class.java))
//                finish()

            }
        )

        mScaViewModel.chargingSchedule.observe(
            this,
            Observer {
//                dismissLoading()
                dismissLoading()
                mScaViewModel.requestChargeGun(mScaViewModel.requestScan)
//                startActivity(Intent(this, ChargeStatuListActivity::class.java))
            }
        )

    }

    override fun onStart() {
        super.onStart()

    }


    override fun onDestroy() {
        super.onDestroy()
        h.removeCallbacks(runnableNet)
    }



    fun setTime(time:String){

//                        定时开启充电
        val way = if (mScaViewModel.selectSolarPower.get()) "true" else "false"
        val connectorId = mAdapter.selectItem.connectorId
        val t = time
        val timez = DateUtil.stringToUTC(time)
        mScaViewModel.setChargingSchedule(
            RequestCharge(
                powerWay = way,
                beginChargingDatetime = timez, chargeBoxPk = chargeBoxPk,
                connectorId = connectorId
            )
        )
    }
    //将两个选择时间的dialog放在该函数中
    private fun showDialogPick() {
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
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    //选择完时间后会调用该回调函数
                    time += " $hourOfDay:$minute:00"
                    //最终选择的时间
                    Log.e("time:", time)
                    if (DateUtil.isBeforeNow(DateUtil.formatTimeStamp(time.toString()))) {
                        ToastUtils.showShort("date is illegal")
                    } else {
//                        定时开启充电
                        setTime(time)
//                        connectSocket(true,time)
                    }

                }, hour, minute, true
            )
        //实例化DatePickerDialog对象
        val datePickerDialog =
            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    //选择完日期后会调用该回调函数

                    time = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                    //选择完日期后弹出选择时间对话框
                    timePickerDialog.show()
                }, year, month, day
            )
        //弹出选择日期对话框
        datePickerDialog.show()
    }
}
