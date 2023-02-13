package com.evport.businessapp.data.http.networkmanager

import android.annotation.SuppressLint
import android.util.Log
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.utils.LiveBus
import io.reactivex.Observable
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 数据处理工厂
 *
 * @param isToDatabase 是否从数据库加载数据 默认false
 * @param isToNet 是否从网络获取，默认true
 * @param saveDataToDB 是否存储数据到数据库,如果需要保存必须实现saveCallResult 默认 false
 */
abstract class NetworkBoundResource<ResultType>(
    val networkStatusCallback: NetworkStatusCallback<ResultType>
) {
    //    private val result = MediatorLiveData<Resource<ResultType>>()
//    private val result = Resource<ResultType>()
//    var res: ResultType ?=null

    init {
//        result.value = Resource.loading()
        //        先从数据库加载数据
//        try {
//            if (isToDatabase) {
//                val data: LiveData<ResultType> = this.loadFromDBData()
//                result.addSource<ResultType>(data) {
//                    result.removeSource(data)
//                    if (isToNet) {
//                        //请求网络数据
//                        fetchFromNetwork()
//                    } else {
//                        result.addSource(data) {
//                            result.value = Resource.success(it)
//                        }
//                    }
//                }
//            } else {
        //请求网络数据
        fetchFromNetwork()
//            }
//        } catch (e: Exception) {
//            errors(e, null)
//        }
    }

    @SuppressLint("CheckResult")
    private fun fetchFromNetwork() {
//        val loading: Resource<ResultType>
        loadFromNetData()
            .map {
                if (it == null) {
                    val message = "The data is empty"
                    throw ApiException(message, message)
                } else {
                    return@map it
                }
            }
            .compose(RxHelper.rxSchedulerHelper())
            .subscribe({
                //                if (!saveDataToDB) {
                //，直接观察网络返回数据
                val data = it
                if (data.success) {
//                    if (data.data != null) {
                    networkStatusCallback.onSuccess(data = data.data)
//                    } else {
//                        networkStatusCallback.onSuccessNoData(data.message)
//                    }

                } else {
                    if (data.code == "401" || data.code == "511") {
//                    todo token失效，重新登录
//                    SPUtils.getInstance().put(Configs.TOKEN, "")
                        LiveBus.getInstance().post(EventBean(Configs.EVENT_LOGOUT, true, ""))
                    }else if (data.code.equals("510")) {
                        Log.e("hm----510", "510")
//                        refresh()
                    }
                    Log.e("hm----data", Gson().toJson(data))
//                    if (!data.code .equals("429")) {
                    if (!data.message.isNullOrBlank()) {
                        networkStatusCallback.onFailure(data.message)
                    }

//                    }


                }
//                    result.addSource(data) { s ->
//                        result.removeSource(loading)
//                        result.removeSource(data)
//                        result.addSource(data) {
//                            result.value = Resource.success(s)
//                        }
//                    }
//                } else if (!isToDatabase && isToNet) {
//                    saveResultAndReInit(it, loading)
//                }
            }, {
                errors(it)
            })
    }

    /**
     * 异步存储到数据库，如果需要存储直接调用这个方法，传入一个函数即可
     */
//    @SuppressLint("StaticFieldLeak", "CheckResult")
//    private fun saveResultAndReInit(datas: ResultType?, loading: LiveData<ResultType>) {
//        RxHelper.createObservable(datas)
//            .flatMap { it ->
//                saveCallResult(it)
//                return@flatMap RxHelper.createObservable(datas)
//            }
//            .compose(RxHelper.rxSchedulerHelper())
//            .subscribe({
//                val data = MutableLiveData<ResultType>()
//                data.value = it
//                result.addSource(data) { s ->
//                    result.removeSource(loading)
//                    result.removeSource(data)
//                    result.addSource(data) {
//                        result.value = Resource.success(s)
//                    }
//                }
//            }, {
//                errors(it, loading)
//            })
//    }

    /**
     *处理各种错误
     */
    private fun errors(e: Throwable) {
        //处理系统级错误和业务错误,业务错误分发到底层去
        e.printStackTrace()
        Log.e("fetchFromNetworkx", e.message.toString())

        var message: String?
        var errorResource: Resource<ResultType> = Resource.error(
            e.message ?: Utils.getApp().getString(R.string.Unknownexception)

        )

        if (e is SocketTimeoutException) {
            message = Utils.getApp().getString(R.string.Pleasecheckthenetwork)
            errorResource = Resource.error(
                message
            )
        } else if (e is ConnectException) {
            message = Utils.getApp().getString(R.string.Pleasetryagainlater)
            errorResource = Resource.error(
                message

            )
        } else if (e is IOException || e is UnknownHostException) {
            message = Utils.getApp().getString(R.string.Pleasecheckthenetwork)
            errorResource = Resource.error(
                message
            )
        } else if (e is NullPointerException) {
            message = e.message ?: ""
            errorResource = Resource.error(
                message
            )
        } else if (e is ApiException) {
            message = e.message ?: ""
            errorResource = Resource.error(
                message
            )
        } else if (e is HttpException) {
//            服务器返回错误
            val string = e.response().errorBody()?.string()
            Log.e("errors", string.toString())
            try {
                if (e.code() == 401 || e.code() == 511) {
//                    todo token失效，重新登录
//                    SPUtils.getInstance().put(Configs.TOKEN, "")
                    LiveBus.getInstance().post(EventBean(Configs.EVENT_LOGOUT, true, ""))
                }
                val s =
                    Gson().fromJson(string, Error::class.java)
//                if (message == "Token expired or logged in on another device. Please log in again.") {
//                    UserInfoService.instence.logOut()
//
//                }

                errorResource =
                    Resource.error(
                        s.message
                    )

            } catch (e: Throwable) {
                message = Utils.getApp().getString(R.string.Unknownexception)
                errorResource =
                    Resource.error(
                        message

                    )
            }
        }

        networkStatusCallback.onFailure(errorResource.getErrorMsg().toString())

//
//        data.value = errorResource
//        result.addSource(data) { s ->
//            if (loading != null)
//                result.removeSource(loading)
//            result.removeSource(data)
//            result.addSource(data) {
//                result.value = s
//            }
//        }
    }

//    fun getData(): Resource<ResultType> {
//        return result
//    }

//    /**
//     *从数据库中加载数据,使用workManager
//     */
//    open fun loadFromDBData(): LiveData<ResultType> {
//        return MutableLiveData<ResultType>()
//    }

    /**
     * 将数据存入数据库
     */

//    open fun saveCallResult(data: ResultType) {
//
//    }

    /**
     * 基类只获取数据的Observable，订阅的时候分为 错误和数据解析，分别放到不同的类型中去
     */

    abstract fun loadFromNetData(): Observable<Resource<ResultType>>

    /**
     * 处理业务错误
     */
//    abstract fun loadError(error: ApiException)
    /**
     * 跳转登陆
     */
//    @LoginFilter
    fun toLogin() {

    }

}