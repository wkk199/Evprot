package com.evport.businessapp.data.http.retrofit


import com.evport.businessapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BaseRetrofit {
    private const val TIMEOUT_READ = 60
    private const val TIMEOUT_CONNECTION = 30
    private val trustAllCert = TrustAllCert()
    private val logInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    private val cacheInterceptor = CacheInterceptor()
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(cacheInterceptor)
            .connectTimeout(TIMEOUT_CONNECTION.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ.toLong(), TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_READ.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .apply {
                if (BuildConfig.DEBUG) addInterceptor(logInterceptor)
                sslSocketFactory(SSL(trustAllCert), trustAllCert)
            }
            .build()

    fun <T> creatApi(clazz: Class<T>, url: String): T {

//        todo url
        return Retrofit.Builder()
//                .baseUrl("http://192.168.0.39:8090/")
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(clazz)
    }
}