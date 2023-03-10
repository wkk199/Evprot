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
import android.bluetooth.*
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.Observer
import blufi.espressif.BlufiCallback
import blufi.espressif.BlufiClient
import blufi.espressif.params.BlufiConfigureParams
import blufi.espressif.params.BlufiParameter
import blufi.espressif.response.BlufiScanResult
import blufi.espressif.response.BlufiStatusResponse
import blufi.espressif.response.BlufiVersionResponse
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.ChargeSetting
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.DrawerAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.LiveBus
import kotlinx.android.synthetic.main.fragment_charge_whitelist_setting.*
import org.jetbrains.anko.support.v4.runOnUiThread

import java.util.*
import kotlin.collections.HashMap

/**

 */


val mBlufiFilter = "BLUFI"

const val DEFAULT_MTU_LENGTH = 128
const val MIN_MTU_LENGTH = 15
const val MAX_MTU_LENGTH = 512
class ChargeWhiteListFragment : BaseFragment() {


    private val item by lazy {
        arguments?.getParcelable<ChargeSetting>(CHARGE_SETTING)
    }

    private lateinit var mDrawerViewModel: DrawerViewModel
    private lateinit var mBleAdapter: DrawerAdapter

    var mBleList = mutableListOf<ScanResult>()

//    private val ? = null
    private var mBlufiClient: BlufiClient? = null


    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        mBleAdapter = DrawerAdapter(requireContext())
        return DataBindingConfig(R.layout.fragment_charge_whitelist_setting, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                mBleAdapter
            )
    }



    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

//        mDrawerViewModel.chargeSetLiveData.observe(
//            viewLifecycleOwner,
//            Observer {
//                initWhiteList()
//            }
//        )



//        mBleAdapter.setMenuClick {s, menu ->
//            nav().navigate(R.id.action_chargeWhiteListFragment_to_chargeWhiteListEditFragment)
//        }
        mBleAdapter.setOnItemClickListener { item, position ->
            mBleAdapter.menuSelect = position
            selectItem = item
            mBleAdapter.notifyDataSetChanged()
        }


        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == ConnectWIFI) {
                val u = it.value as User
                u.apply {
                    ssid = email.toString()
                    this@ChargeWhiteListFragment.password = password.toString()
                    connectWifi()
                }
            }else  if (it.type == ConnectWIFICallBack &&it.value== ConnectWIFICallBack){
                nav().navigateUp()
            }
        })
        initWhiteList()
//        mThreadPool = Executors.newSingleThreadExecutor()
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            111
        )
        refresh_layout.setOnRefreshListener {
            scan()
        }
        refresh_layout.isRefreshing = true
        scan()

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val size = permissions.size
        for (i in 0 until size) {
            val permission = permissions[i]
            val grant = grantResults[i]
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grant == PackageManager.PERMISSION_GRANTED) {
                    refresh_layout.isRefreshing = true
                    scan()
                }
            }
        }
    }
    /**
     * Try to connect device
     */
    private fun connect(mDevice: BluetoothDevice) {
        if (mBlufiClient != null) {
            mBlufiClient?.close()
            mBlufiClient = null
        }
        mBlufiClient = BlufiClient(context?.applicationContext, mDevice)
        mBlufiClient?.setGattCallback(GattCallback())
        mBlufiClient?.setBlufiCallback(BlufiCallbackMain())
        mBlufiClient?.connect()
    }
    fun closeClient(){
        if (mBlufiClient != null) {
            mBlufiClient?.setGattCallback(null)
            mBlufiClient?.setBlufiCallback(null)
            mBlufiClient?.close()
            mBlufiClient = null
        }

    }


//    private var mThreadPool: ExecutorService? = null
//    private var mUpdateFuture: Future<*>? = null
    val mScanCallback = ScanCallback()

    @Volatile
    private var mScanStartTime: Long = 0
    private fun scan() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = adapter.bluetoothLeScanner
        if (!adapter.isEnabled || scanner == null) {

            Toast.makeText(requireContext(), R.string.main_bt_disable_msg, Toast.LENGTH_SHORT).show()
            refresh_layout.setRefreshing(false)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check location enable
            val locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val locationEnable =
                locationManager != null && LocationManagerCompat.isLocationEnabled(
                    locationManager
                )
            if (!locationEnable) {
                Toast.makeText(requireContext(), R.string.main_location_disable_msg, Toast.LENGTH_SHORT).show()
                refresh_layout.setRefreshing(false)
                return
            }
        }
        refresh_layout.isRefreshing  = false
        mBleList.clear()
        mBleAdapter.menuSelect = -1
        mDeviceMap.clear()
        selectItem = null
        initWhiteList()
//        mBlufiFilter = BlufiApp.getInstance().settingsGet(
//            SettingsConstants.PREF_SETTINGS_KEY_BLE_PREFIX,
//            BlufiConstants.BLUFI_PREFIX
//        ) as String
        mScanStartTime = SystemClock.elapsedRealtime()
        Log.e("","Start scan ble")
        scanner.startScan(
            null, ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
            mScanCallback
        )
//        mUpdateFuture = mThreadPool?.submit(Runnable {
//            while (!Thread.currentThread().isInterrupted) {
//                try {
//                    Thread.sleep(1000)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                    break
//                }
//                val scanCost: Long =
//                    SystemClock.elapsedRealtime() - mScanStartTime
//                if (scanCost > 4000L) {
//                    break
//                }
//                onIntervalScanUpdate(false)
//            }
//            val inScanner =
//                BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
//            inScanner?.stopScan(mScanCallback)
//            onIntervalScanUpdate(true)
//            Log.e("blu","Scan ble thread is interrupted")
//        })
    }

    var mDeviceMap = HashMap<String, ScanResult>()
    private fun onIntervalScanUpdate(over: Boolean) {
//        val devices: List<ScanResult> =
//            ArrayList<ScanResult>(mDeviceMap.values)
//        Collections.sort(
//            devices
//        ) { dev1: ScanResult, dev2: ScanResult ->
//            val rssi1 = dev1.rssi
//            val rssi2 = dev2.rssi
//            rssi2.compareTo(rssi1)
//        }
//        runOnUiThread{
//            devices.forEach {  }
//            mBleList.clear()
//            mBleAdapter.menuSelect = -1
//            mBleList.addAll(devices)
//            initWhiteList()
//            if (over) {
//                dismissLoading()
//                refresh_layout.setRefreshing(false)
//            }
//        }
    }

    private fun stopScan() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val scanner = adapter.bluetoothLeScanner
        scanner?.stopScan(mScanCallback)

//        if (mUpdateFuture != null) {
//            mUpdateFuture!!.cancel(true)
//        }
//        mLog.d("Stop scan ble")
    }

   inner class ScanCallback : android.bluetooth.le.ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            if (refresh_layout!=null)
                refresh_layout.isRefreshing = false
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            for (result in results) {
                onLeScan(result)
            }
        }

        override fun onScanResult(
            callbackType: Int,
            result: ScanResult
        ) {
            onLeScan(result)
        }

        private fun onLeScan(scanResult: ScanResult) {
            if (refresh_layout!=null)
                refresh_layout.isRefreshing = false
            val name = scanResult.device.name
//            if (!TextUtils.isEmpty(mBlufiFilter)) {
                if (name.isNullOrBlank() || !name.startsWith(mBlufiFilter)) {
                    return
                }
//            }
            if (mBleList.map { it.device.name }.contains(name))
                return

//            mDeviceMap[scanResult.device.address] = scanResult

            mBleList.add(scanResult)
            runOnUiThread{

//                mBleList.clear()
//                mBleAdapter.menuSelect = -1
//                mBleList.addAll(devices)
                initWhiteList()
//                if (over) {
//                    refresh_layout.setRefreshing(false)
//                }
            }
        }
    }
    fun initWhiteList() {

        mDrawerViewModel.whiteList.value = mBleList

    }
    var selectItem:ScanResult?=null

//????????????
    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun plus() {
            if (selectItem==null)
                updateMessage(getString(R.string.Pleaseselectavaliddevice),true)
            selectItem?.apply {
                showLoading()
                connect(device)
            }

//            if (mBleAdapter.getMenuSelect()>=0&&mBleList.isNotEmpty()) {
//                val item = selectItem
//
//            }

//            nav().navigate(R.id.action_chargeWhiteListFragment_to_chargeWhiteListEditFragment,
//                Bundle().also {
//                    it.putParcelable(CHARGE_SETTING, item)
//                }
//            )
        }



    }



    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()

        stopScan()
        closeClient()
//        mThreadPool?.shutdownNow()


    }
   fun onGattConnected() {
//       ??????????????? ??????Wi-Fi??????
//        mConnected = true;
       dismissLoading()
       nav().navigate(R.id.action_chargeWhiteListFragment_to_chargeWhiteListEditFragment)

    }

     fun onGattDisconnected() {
//        mConnected = false
//        runOnUiThread {
//            mBlufiConnectBtn.setEnabled(true)
//            mBlufiDisconnectBtn.setEnabled(false)
//            mBlufiSecurityBtn.setEnabled(false)
//            mBlufiVersionBtn.setEnabled(false)
//            mBlufiConfigureBtn.setEnabled(false)
//            mBlufiDeviceStatusBtn.setEnabled(false)
//            mBlufiDeviceScanBtn.setEnabled(false)
//            mBlufiCustomBtn.setEnabled(false)
//        }
    }


    var ssid = ""
    var password = ""
    fun connectWifi(){
        showLoading()
        val params = BlufiConfigureParams()
        val opMode: Int = 1 // ??????????????????????????????1 ??? Station ?????????2 ??? SoftAP ?????????3 ??? Station ??? SoftAP ????????????
        params.opMode = BlufiParameter.OP_MODE_STA

//        if (opMode == BlufiParameter.OP_MODE_STA) {
            // ?????? Station ????????????
            params.staSSIDBytes=ssid.toByteArray() // ?????? Wi-Fi SSID
            params.staPassword = password // ?????? Wi-Fi ????????????????????? Wi-Fi ??????????????????
            // ?????????Device ??????????????? 5G Wi-Fi???????????????????????????????????? 5G Wi-Fi
//        }
//        else if (opMode == BlufiParameter.OP_MODE_SOFTAP) {
////            // ?????? SoftAP ????????????
////            params.setSoftAPSSID(ssid) // ?????? Device ??? SSID
////            params.setSoftAPSecurity(security) // ?????? Device ??????????????????0 ???????????????2 ??? WPA???3 ??? WPA2???4 ??? WPA/WPA2
////            params.setSoftAPPAssword(password) // ??? Security ??? 0 ???????????????
////            params.setSoftAPChannel(channel) // ?????? Device ?????????, ?????????
////            params.setSoftAPMaxConnection(maxConnection) // ??????????????? Device ???????????????
//        } else if (opMode == BlufiParameter.OP_MODE_STASOFTAP) {
//            // ????????????
//        }

        Log.e("blue wifi","na:"+ssid+"p:"+password)
        mBlufiClient?.configure(params)
        Handler().postDelayed({
            mBlufiClient?.requestDeviceStatus()
        },5*1000)
    }

    /**
     * mBlufiClient call onCharacteristicWrite and onCharacteristicChanged is required
     *
     *????????????
     */
    inner class GattCallback : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            val devAddr = gatt.device.address
            Log.d("gatt",
                String.format(
                    Locale.ENGLISH,
                    "onConnectionStateChange addr=%s, status=%d, newState=%d",
                    devAddr,
                    status,
                    newState
                )
            )
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        onGattConnected()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
//                        gatt.close()
                        onGattDisconnected()
                        updateMessage(String.format("Disconnected device"), false)
                    }
                }
            } else {
//                gatt.close()
                onGattDisconnected()
                updateMessage(String.format("Connet device failed"), false)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
//            mLog.d(
//                String.format(
//                    Locale.ENGLISH,
//                    "onMtuChanged status=%d, mtu=%d",
//                    status,
//                    mtu
//                )
//            )
            if (status == BluetoothGatt.GATT_SUCCESS) {
                updateMessage(
                    String.format(
                        Locale.ENGLISH,
                        "Set mtu complete, mtu=%d ",
                        mtu), false
                )
            } else {
                mBlufiClient?.setPostPackageLengthLimit(20)
                updateMessage(
                    String.format(
                        Locale.ENGLISH,
                        "Set mtu failed, mtu=%d, status=%d",
                        mtu,
                        status), false
                )
            }
//            onGattServiceCharacteristicDiscovered()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
//            Log.d(""
//                String.format(
//                    Locale.ENGLISH,
//                    "onServicesDiscovered status=%d",
//                    status
//                )
//            )
            if (status != BluetoothGatt.GATT_SUCCESS) {
//                gatt.disconnect()
                updateMessage(
                    String.format(
                        Locale.ENGLISH,
                        "Discover services error status %d",
                        status), false
                )
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
//                gatt.disconnect()
                updateMessage(
                    String.format(
                        Locale.ENGLISH,
                        "WriteChar error status %d",
                        status
                    ), false
                )
            }
        }
    }

//    ??????wifi
   inner class BlufiCallbackMain : BlufiCallback() {
        override fun onGattPrepared(
            client: BlufiClient, gatt: BluetoothGatt, service: BluetoothGattService,
            writeChar: BluetoothGattCharacteristic, notifyChar: BluetoothGattCharacteristic
        ) {
            if (service == null) {
                gatt.disconnect()
                updateMessage(getString(R.string.Discoverservicefailed), true)
                return
            }
            if (writeChar == null) {
                gatt.disconnect()
                updateMessage("Get write characteristic failed", false)
                return
            }
            if (notifyChar == null) {
                gatt.disconnect()
                updateMessage("Get notification characteristic failed", false)
                return
            }
            updateMessage("Discover service and characteristics success", false)
            var mtu: Int = DEFAULT_MTU_LENGTH
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
                && Build.MANUFACTURER.toLowerCase().startsWith("samsung")
            ) {
                mtu = 23
            }
            val requestMtu = gatt.requestMtu(mtu)
            if (!requestMtu) {
//                mLog.w("Request mtu failed")
                client.setPostPackageLengthLimit(20)
                updateMessage(
                    String.format(
                        Locale.ENGLISH,
                        "Request mtu %d failed",
                        mtu
                    ), false
                )
//                onGattServiceCharacteristicDiscovered()
            }
        }

        override fun onNegotiateSecurityResult(
            client: BlufiClient,
            status: Int
        ) {
            if (status == STATUS_SUCCESS) {
                updateMessage("Negotiate security complete", false)
            } else {
                updateMessage("Negotiate security failed??? code=$status", false)
            }
        }

        override fun onConfigureResult(client: BlufiClient, status: Int) {
            if (status == STATUS_SUCCESS) {
                updateMessage("Post configure params complete", false)
            } else {
                updateMessage("Post configure params failed, code=$status", false)
            }
        }

        override fun onDeviceStatusResponse(
            client: BlufiClient,
            status: Int,
            response: BlufiStatusResponse
        ) {


            when (status) {
                STATUS_SUCCESS -> {
//                    updateMessage(
//                        String.format(
//                            "Receive device status response:\n%s",
//                            response.generateValidInfo()
//                        ),
//                        true
//                    )
//                    val opMode = response.opMode
//                    if (opMode == BlufiParameter.OP_MODE_STA) {
                        // ????????? Station ??????
                        val conn =
                            response.staConnectionStatus // ?????? Device ?????????????????????0 ????????? Wi-Fi ????????????????????? Wi-Fi ??????
                        val ssid =
                            response.staSSID // ?????? Device ??????????????? Wi-Fi ??? SSID
                        val bssid =
                            response.staBSSID // ?????? Device ??????????????? Wi-Fi ??? BSSID
                        val password =
                            response.staPassword // ?????? Device ??????????????? Wi-Fi ??????
                        if (conn==0){

                            LiveBus.getInstance().post(EventBean(ConnectWIFICallBack,ConnectWIFICallBack,""))

                            updateMessage(
                                "Station connect Wi-Fi now",
                                true
                            )
                        }else{
                            updateMessage(
                                "There are some problems with the connect, please check if you have entered the correct ssid and password",
                                true
                            )
                            LiveBus.getInstance().post(EventBean(ConnectWIFICallBack, "",""))
                        }
//                    ToastUtils.showShort("staConnectionStatus: $conn")
//                    }else{
//                        LiveBus.getInstance().post(EventBean(ConnectWIFICallBack, String.format(
//                            "Receive device status response:\n%s",
//                            response.generateValidInfo()
//                        )))
//                    }
//                    else if (opMode == BlufiParameter.OP_MODE_SOFTAP) {
//                        // ????????? SoftAP ??????
//                        val ssid = response.softAPSSID // ?????? Device ??? SSID
//                        val channel = response.softAPChannel // ?????? Device ?????????
//                        val security =
//                            response.softAPSecurity // ?????? Device ??????????????????0 ???????????????2 ??? WPA???3 ??? WPA2???4 ??? WPA/WPA2
//                        val connMaxCount =
//                            response.softAPMaxConnectionCount // ?????????????????? Device ??????
//                        val connCount =
//                            response.softAPConnectionCount // ??????????????? ??? Device ??????
//                    } else if (opMode == BlufiParameter.OP_MODE_STASOFTAP) {
//                        // ????????? Station ??? SoftAP ????????????
//                        // ????????????????????? Station ????????? SoftAP ??????
//                    }
                }
                else -> {

                    updateMessage("${resources.getString(R.string.Devicestatusresponseerror)}, $status", true)
                    LiveBus.getInstance().post(EventBean(ConnectWIFICallBack, "",""))
                }
            }
        }

        override fun onDeviceScanResult(
            client: BlufiClient,
            status: Int,
            results: List<BlufiScanResult>
        ) {
            if (status == STATUS_SUCCESS) {
                val msg = StringBuilder()
                msg.append("Receive device scan result:\n")
                for (scanResult in results) {
                    msg.append("SSID:${scanResult.ssid},RSSI:${scanResult.rssi}").append("\n")
                }
                updateMessage(msg.toString(), true)
            } else {
                updateMessage("Device scan result error, code=$status", false)
            }
        }

        override fun onDeviceVersionResponse(
            client: BlufiClient,
            status: Int,
            response: BlufiVersionResponse
        ) {
            if (status == STATUS_SUCCESS) {
                updateMessage(
                    String.format("Receive device version: %s", response.versionString), false
                )
            } else {
                updateMessage("Device version error, code=$status", false)
            }
        }

        override fun onPostCustomDataResult(
            client: BlufiClient,
            status: Int,
            data: ByteArray
        ) {
            val dataStr = String(data)
            val format = "Post data %s %s"
            if (status == STATUS_SUCCESS) {
                updateMessage(String.format(format, dataStr, "complete"), false)
            } else {
                updateMessage(String.format(format, dataStr, "failed"), false)
            }
        }

        override fun onReceiveCustomData(
            client: BlufiClient,
            status: Int,
            data: ByteArray
        ) {
            if (status == STATUS_SUCCESS) {
                val customStr = String(data)
                updateMessage(String.format("Receive custom data:\n%s", customStr), false)
            } else {
                updateMessage("Receive custom data error, code=$status", false)
            }
        }

        override fun onError(client: BlufiClient, errCode: Int) {
            updateMessage(
                String.format(
                    Locale.ENGLISH,
                    "Receive error code %d",
                    errCode
                ), false
            )
        }

    }
    var old=""
    var time=0L

    fun updateMessage(
        message: String,
        isNotificaiton: Boolean
    ) {
        try {
            if(isNotificaiton&&context!=null) {
                if (message != old) {
                    ToastUtils.showShort(message)
                    time = System.currentTimeMillis()
                    old = message
                }else{
                    if (System.currentTimeMillis()-time>=2500){
                        ToastUtils.showShort(message)
                        time = System.currentTimeMillis()
                    }
                }
            }
        }catch (e:Exception){

        }
    }

}