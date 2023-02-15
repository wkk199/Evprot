package com.evport.businessapp.ui.page.activity

import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.QrCodeAnalyzer
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : BaseActivity() {

    private var mScaViewModel: ScanViewModel? = null


    override fun initViewModel() {
        mScaViewModel = getActivityViewModel(ScanViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_scan, mScaViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    var isFlashLightOn = false

//    fun getData(code: String) {
//        showLoading()
//        object : NetworkBoundResource<ConnectorDetail>(networkStatusCallback = object :
//            NetworkStatusCallback<ConnectorDetail> {
//
//            override fun onSuccess(data: ConnectorDetail?) {
//                dismissLoading()
//                if (data != null) {
//                    startActivity<ChageGunDetailActivity>(
//                        Pair("scanData", data),
//                        Pair("pk", data.connectorPk)
//                    )
//                    finish()
//                }
//
//            }
//
//            override fun onFailure(message: String) {
//                message.toast()
//                dismissLoading()
//
//            }
//
//        }) {
//            override fun loadFromNetData(): Observable<Resource<ConnectorDetail>> {
//                return SingletonFactory.apiService.scanV2(RequestScan(QRCode = code))
//            }
//        }
//    }
//

    override fun onStart() {
        super.onStart()
//打开后置摄像头预览,但并没有开始扫描
//        zxingview.startCamera();
//        开启扫描框
//        zxingview.showScanRect();

        Log.e("scan:", "start")
//        zxingview.startSpotAndShowRect()

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


    override fun onDestroy() {
//        zxingview.onDestroy()
        super.onDestroy()

        stopCamera()

    }

    inner class ClickProxy {
        fun close() {
            finish()
        }
    }


    var barCode = ""

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
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
        val executor = ContextCompat.getMainExecutor(this)


        val imageAnalyzer = ImageAnalysis.Builder().build().also { imageAbalysis ->
            imageAbalysis.setAnalyzer(executor, QrCodeAnalyzer({ qrCodes ->
                qrCodes?.forEach {
//                    Toast.makeText(requireContext(), it.rawValue, Toast.LENGTH_SHORT).show()
                    Log.d("scan", "QR Code detected: ${it.rawValue}.")
                    if (barCode.isBlank()) {
                        barCode = it.rawValue ?: ""
                        showLoading()

                        texture_view.postDelayed({
                            LiveBus.getInstance().post(EventBean("ScanResult",barCode,it.rawValue))
                            finish()
                        }, 800)
                    }
                }
//                    val code = qrCodes[0]
//                    Log.e("scan", "QRCode detected: ${code.rawValue}.")
//                    Toast.makeText(requireContext(), code.rawValue, Toast.LENGTH_SHORT).show()


            }, {
//                Toast.makeText(requireContext(), "QRCode is error", Toast.LENGTH_SHORT).show()
finish()
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

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
    }

}
