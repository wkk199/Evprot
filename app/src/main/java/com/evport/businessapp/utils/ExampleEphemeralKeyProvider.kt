package com.evport.businessapp.utils

import androidx.annotation.Size
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.data.bean.GetEphemeralKey
import com.evport.businessapp.data.http.networkmanager.Resource
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
Create by ljx at 2020/12/10
 **/
class ExampleEphemeralKeyProvider(val pk: String) : EphemeralKeyProvider {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
//    private val backendApi: BackendApi =
//        RetrofitFactory.instance.create(BackendApi::class.java)

    override fun createEphemeralKey(
        @Size(min = 4) apiVersion: String,
        keyUpdateListener: EphemeralKeyUpdateListener
    ) {


        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                data?.apply {

                    val ephemeralKeyJson = data as String
                    keyUpdateListener.onKeyUpdate(ephemeralKeyJson)
                }

            }

            override fun onFailure(message: String) {
                keyUpdateListener.onKeyUpdateFailure(0,message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.getEphemeralKey(GetEphemeralKey(apiVersion=apiVersion,connectorPk = pk))
            }
        }

//        compositeDisposable.add(

//            backendApi.createEphemeralKey(hashMapOf("api_version" to apiVersion))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { responseBody ->
//                    try {
//                        val ephemeralKeyJson = responseBody.string()
//                        keyUpdateListener.onKeyUpdate(ephemeralKeyJson)
//                    } catch (e: IOException) {
//                        keyUpdateListener
//                            .onKeyUpdateFailure(0, e.message ?: "")
//                    }
//                }
//        )
    }
}