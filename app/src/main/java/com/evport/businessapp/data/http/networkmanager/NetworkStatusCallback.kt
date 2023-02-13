package com.evport.businessapp.data.http.networkmanager

interface NetworkStatusCallback<T> {
//    fun onStartLoading()
    fun onFailure(message: String)
    fun onSuccess(data: T?)
//    fun onSuccessNoData(message:String)
//    fun onSucc()
//    fun onComplete()
}