package com.evport.businessapp.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.evport.businessapp.App
import com.evport.businessapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.tbruyelle.rxpermissions2.RxPermissions


/**
 * Created by mac on 2018/7/24.
 */
abstract class BaseLocationFragment : BaseFragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private lateinit var mGoogleApiClient: GoogleApiClient
    @SuppressLint("CheckResult")
    private fun requestLocation() {
        mGoogleApiClient = GoogleApiClient.Builder(context!!)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        RxPermissions(this.requireActivity())
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (granted) {
                        getLocation()
                    } else {
                        try {
                            if (isAdded)
                                ToastUtils.showShort(getString(R.string.need_location_permission))
                        } catch (e: Exception) {

                        }
                    }
                }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestLocation()
    }

//
//
//    override fun <T> onReceiveEvent(event: CommonEvent<T>) {
//        if (event.getEventType() == EventType.PRAYER_UPDATE||event.getEventType() == EventType.UPDATE_PRAYER_TIME_24) {
//            initPrayerTime()
//        }
//    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
//        if (verifyGPS()) {
        if (!mGoogleApiClient.isConnected)
            mGoogleApiClient.connect()
//        } else {
//            openGPS()
//        }
    }

    private fun disConnect() {
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disConnect()
    }

    abstract fun onLocationGet(location: Location)
    private fun verifyGPS(): Boolean {
        activity?.let {
            val locationManager =
                    it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return (locationManager
                    .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) or locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER))
        }
        return false

    }

//    private fun openGPS() {
//        alert(getString(R.string.gps_need), "Tips") {
//            yesButton {
//                val intent = Intent(
//                        Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivityForResult(intent, 0) // 设置完成后返回到原来的界面
//            }
//            isCancelable = false
//            show()
//        }
//
//
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            getLocation()
        }
    }


    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {

//        startLocationUpdates()
        var mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
//        var mLocation = LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
        if (mLocation != null) {
            App.currentLocation = mLocation
            try {
                onLocationGet(mLocation)

            } catch (e: Exception) {

            }

        } else {
//            info { "last location is null" }
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
//        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
//            .addOnSuccessListener { location : Location? ->
//                // Got last known location. In some rare situations this can be null.
//                if (location != null) {
//                    App.currentLocation = location
//                    try {
//                        onLocationGet(location)
//                    } catch (e: Exception) {
//                    }
//                    disConnect()
//                }
//            }
        // Create the location request



        var mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10)
            .setFastestInterval(10)


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
            mLocationRequest) { location ->
            if (location != null) {
                App.currentLocation = location
                try {
                    onLocationGet(location)
                } catch (e: Exception) {
                }
                disConnect()
            }
//            else
//                info { "location is null" }

        }

    }


}