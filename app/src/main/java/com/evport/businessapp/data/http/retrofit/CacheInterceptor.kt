package com.evport.businessapp.data.http.retrofit


import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.kunminx.architecture.utils.SPUtils
import com.evport.businessapp.data.config.Configs
import okhttp3.Interceptor

import okhttp3.Request
import okhttp3.Response
import java.util.*
import kotlin.jvm.Throws


/**
 * 缓存拦截器
 * 在Header中添加Token和cookie
 */
class CacheInterceptor : Interceptor {

//    val clientId by lazy {
//        "android${Settings.System.getString(
//            App().getApplication().application.appContext.contentResolver,
//            android.provider.Settings.Secure.ANDROID_ID
//        )}"
//    }

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = getResponse(request)

        val response = chain.proceed(request)
        return response.newBuilder()
            .build()
    }

    private fun getResponse(request: Request): Request {

        val lan = if (!LanguageUtils.isAppliedLanguage(Locale("iw")))"en_US" else "iw_IL"
        return request.newBuilder()
            .addHeader("token", SPUtils.getInstance().getString(Configs.TOKEN))
            .addHeader("Content-Type", "application/json;charset=UTF-8")
            .addHeader("device", "${DeviceUtils.getModel()}(${DeviceUtils.getSDKVersionName()})")
            .addHeader("app_version", "${AppUtils.getAppVersionName()}(${AppUtils.getAppVersionCode()})")
//            .addHeader("app_id", "bao_xin")
            .addHeader("app_id", "EVPORT")
//            .addHeader("Accept-Language" ,lan)
//            .addHeader("X-USER-ID", if (UserInfoService.instence.isLogin) "${UserInfoService.instence.id}" else "")
//            .addHeader("X-ACCESS-TOKEN", if (UserInfoService.instence.isLogin) UserInfoService.instence.token else "")
            .build()
    }
}
