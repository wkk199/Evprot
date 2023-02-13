package com.evport.businessapp.data.http.networkmanager


//fun <T> LiveData<Resource<T>>.netStatusObserver(lifecycleOwner: LifecycleOwner, callBack: NetworkStatusCallback<T>) {
//    this.observe(lifecycleOwner, Observer {
//        when (this.value?.status) {
//            Resource.ERROR -> {
//                callBack.onFailure(it.getStatusBean().msg)
//                callBack.onComplete()
//            }
//            Resource.LOADING -> {
//                callBack.onStartLoading()
//            }
//            Resource.SUCCESS -> {
//                callBack.onSuccess(it.getResult())
//                callBack.onComplete()
//            }
//        }
//    })
//}
