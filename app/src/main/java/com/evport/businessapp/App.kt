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
package com.evport.businessapp

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.location.Location
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.ToastUtils
import com.carlt.networklibs.NetworkManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.libraries.places.api.Places
import com.evport.businessapp.data.bean.HomeFilter
import com.evport.businessapp.player.PlayerManager
import com.evport.businessapp.utils.*
import com.kunminx.architecture.utils.Utils
import com.onesignal.OneSignal
import com.previewlibrary.ZoomMediaLoader

/**
 * Create by KunMinX at 19/10/29
 */
class App : Application(), ViewModelStoreOwner {

//    var appContext: Context? = null

    //TODO tip：可借助 Application 来管理一个应用级 的 SharedViewModel，
// 实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
    companion object {
        lateinit var appContext: Context
        var currentLocation: Location? = null

    }

    var aCache: ACache? = null
    private var mAppViewModelStore: ViewModelStore? = null
    private var mFactory: ViewModelProvider.Factory? = null
    override fun onCreate() {
        super.onCreate()
        aCache = ACache.get(this)
        mAppViewModelStore = ViewModelStore()
        appContext = this.applicationContext
//        val homeFilter = getHomeFilterData() ?: HomeFilter()
//        if (!homeFilter.isDefault)
//            saveHomeFilterData(HomeFilter())
// Initialize Places.
        //google 地图搜索
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        //网络监听
        NetworkManager.getInstance().init(this);

        Utils.init(this)
//        PlayerManager.getInstance().init(this)
        //initWebSocket()
        Fresco.initialize(this)
        ZoomMediaLoader.getInstance().init(MyImageLoader())
        initOneSignal(this)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            // 关闭暗黑模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        CrashUtils.init()

        initToast()

    }


    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore!!
    }

    fun initOneSignal(context: Context) {
        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(context)
        OneSignal.setNotificationOpenedHandler(NotificationClickOpenedHandler(context))
        OneSignal.setAppId("c4f88051-c6ba-4128-9bac-dd259328903d")
    }

    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        return ViewModelProvider(
            (activity.applicationContext as App),
            (activity.applicationContext as App).getAppFactory(activity)
        )
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return mFactory!!
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    private fun checkActivity(fragment: Fragment): Activity {
        return fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }


    override fun registerActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.registerActivityLifecycleCallbacks(callback)

    }

    override fun unregisterActivityLifecycleCallbacks(callback: ActivityLifecycleCallbacks?) {
        super.unregisterActivityLifecycleCallbacks(callback)

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND)

            Log.e("onTrimMemory:", level.toString())
    }


    //Toast设置统一样式
    fun initToast() {
        //Kotlin
        val defaultMaker = ToastUtils.getDefaultMaker()
        defaultMaker.setBgColor(this.resources.getColor(R.color.color_bf000000))
        defaultMaker.setGravity(Gravity.CENTER, 0, 0)
        defaultMaker.setTextColor(this.resources.getColor(R.color.white))

    }
}