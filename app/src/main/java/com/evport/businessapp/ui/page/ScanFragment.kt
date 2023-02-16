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

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.evport.businessapp.BR
import com.evport.businessapp.ChargeGunDialog
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.ConnectorDetail
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.bean.RequestScan
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.ChageGunDetailActivity
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.QrCodeAnalyzer
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_scan.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * Create by KunMinX at 19/10/29
 */
const val SCANDATA = "SCANDATA"
const val SCANDTYPE = "SCANTYPE"

class ScanFragment : BaseFragment() {
    private var mScaViewModel: ScanViewModel? = null

    val scanType by lazy {
        arguments?.getString(SCANDTYPE, "")
    }

    override fun initViewModel() {
        mScaViewModel = getFragmentViewModel(ScanViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_scan, mScaViewModel)
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

    var isFlashLightOn = false


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        zxingview.setDelegate(object : QRCodeView.Delegate {
//            override fun onScanQRCodeSuccess(result: String?) {
//                Log.e("scan:",result.toString())
////             zxingview.stopSpot()
//                if (result != null) {
////                    if (scanType !== null && scanType == "add") {
////
////                        mScaViewModel?.requestScan?.chargeBoxPk = result
////                        mScaViewModel?.bindFamily(mScaViewModel?.requestScan)
////                    } else {
////                        mScaViewModel?.requestScan?.chargeBoxPk = result
////                        mScaViewModel?.requestScan(mScaViewModel?.requestScan)
////                    }
//                }else{
//                    ToastUtils.showShort("no data")
//                    nav().navigateUp()
//                }
//
//            }
//
//            override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
//                // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
//                var tipText = zxingview.scanBoxView.tipText
//                val ambientBrightnessTip = "\nThe environment is too dark, please turn on the flash"
//                if (tipText == null) return
//                if (isDark) {
//                    if (!tipText.contains(ambientBrightnessTip)) {
//                        zxingview.scanBoxView.tipText = tipText + ambientBrightnessTip
//                    }
//                    zxingview.openFlashlight()
//                } else {
//                    zxingview.closeFlashlight()
//                    if (tipText.contains(ambientBrightnessTip)) {
//                        tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
//                        zxingview.scanBoxView.tipText = tipText
//                    }
//                }
//            }
//
//            override fun onScanQRCodeOpenCameraError() {
////                ToastUtils.showShort("no data")
//                Log.e("scan:","no data")
//            }
//        })

    }

    fun getData(code: String) {
        showLoading()
        object : NetworkBoundResource<ConnectorDetail>(networkStatusCallback = object :
            NetworkStatusCallback<ConnectorDetail> {

            override fun onSuccess(data: ConnectorDetail?) {
                dismissLoading()
                if (data != null) {
                    if (data.vendor.equals("ThirdPart")) {
                        LiveBus.getInstance()
                            .post(EventBean(ChargeGunDialog, Gson().toJson(data.connectors), ""))
                        nav().navigateUp()
                    } else {
                        startActivity<ChageGunDetailActivity>(
                            Pair("scanData", data),
                            Pair("pk", data.connectorPk)
                        )
                        nav().navigateUp()
                    }

                }

            }

            override fun onFailure(message: String) {
                message.toast()
                dismissLoading()
                nav().navigateUp()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ConnectorDetail>> {
                return SingletonFactory.apiService.scanV2(RequestScan(QRCode = code))
            }
        }
    }


    override fun onStart() {
        super.onStart()
//打开后置摄像头预览,但并没有开始扫描
//        zxingview.startCamera();
//        开启扫描框
//        zxingview.showScanRect();

        Log.e("scan:", "start")
//        zxingview.startSpotAndShowRect()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.e("scan:", "onDetach")
    }

    override fun onDetach() {
        super.onDetach()

        Log.e("scan:", "onDetach")
    }

    override fun onStop() {
//        zxingview.stopCamera();
        Log.e("scan:", "onStop")
        super.onStop()

    }

    override fun onResume() {
        super.onResume()

        Log.e("scan:", "onResume")
//        zxingview.startSpotAndShowRect()
        startCamera()
    }

    override fun onDestroyView() {

//        zxingview.onDestroy();
        super.onDestroyView()
        stopCamera()
    }

    override fun onDestroy() {
//        zxingview.onDestroy()
        super.onDestroy()


    }

    inner class ClickProxy {
        fun close() {
            nav().navigateUp()
        }
    }


    var barCode = ""

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val previewConfig = Preview.Builder()
            // We want to show input from back camera of the device
            .setTargetResolution(Size(400, 400))
            .build()

        previewConfig.setSurfaceProvider(texture_view.createSurfaceProvider())
        barCode = ""

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetResolution(Size(400, 400))
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits requested capture mode
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .build()
        val executor = ContextCompat.getMainExecutor(requireContext())


        val imageAnalyzer = ImageAnalysis.Builder().build().also { imageAbalysis ->
            imageAbalysis.setAnalyzer(executor, QrCodeAnalyzer({ qrCodes ->
                qrCodes?.forEach {
//                    Toast.makeText(requireContext(), it.rawValue, Toast.LENGTH_SHORT).show()
                    Log.d("scan", "QR Code detected: ${it.rawValue}.")
                    if (barCode.isBlank()) {
                        barCode = it.rawValue ?: ""
                        showLoading()

                        texture_view.postDelayed({

                            getData(barCode)

                        }, 800)
                    }
                }
//                    val code = qrCodes[0]
//                    Log.e("scan", "QRCode detected: ${code.rawValue}.")
//                    Toast.makeText(requireContext(), code.rawValue, Toast.LENGTH_SHORT).show()


            }, {
//                Toast.makeText(requireContext(), "QRCode is error", Toast.LENGTH_SHORT).show()
                nav().navigateUp()
            }))
        }

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewConfig,
                imageCapture,
                imageAnalyzer
            )
            //Handle flash
//            camera.cameraControl.enableTorch(true)
            iv_logo.setOnClickListener {
//切换手电筒
                if (!isFlashLightOn) {
                    camera.cameraControl.enableTorch(true)
                } else {
                    camera.cameraControl.enableTorch(false)
                }
                isFlashLightOn = !isFlashLightOn

            }
        }, executor)
    }


    fun stopCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
    }

}