package com.evport.businessapp.data.http.networkmanager


import com.kunminx.architecture.utils.SPUtils
import com.evport.businessapp.data.http.retrofit.BaseRetrofit
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.api.ApiService


object SingletonFactory {
//    val apiService by lazy {
//        BaseRetrofit.creatApi(
//            ApiService::class.java, SPUtils.getInstance()
//                .getString(Configs.HOST)
//        )
//    }
//val apiServiceSplash by lazy {
//    BaseRetrofit.creatApi(
//        ApiService::class.java,
//        if (SPUtils.getInstance()
//                .getString(Configs.HOST).isNullOrEmpty()
//        )
//            Configs.BASE_URL else SPUtils.getInstance()
//            .getString(Configs.HOST)
//    )
//}
    var apiService  = BaseRetrofit.creatApi(
    ApiService::class.java,
        com.blankj.utilcode.util.SPUtils.getInstance().getString(Configs.HOST))
    var apiServiceSplash  =
        BaseRetrofit.creatApi(
            ApiService::class.java,
            if (SPUtils.getInstance()
                    .getString(Configs.HOST).isNullOrEmpty()
            )
                Configs.BASE_URL else SPUtils.getInstance()
                .getString(Configs.HOST)
        )


    fun changeAPI(){
        apiService  = BaseRetrofit.creatApi(
            ApiService::class.java,
            com.blankj.utilcode.util.SPUtils.getInstance().getString(Configs.HOST))


    }
}