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

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ActivityUtils
import com.gyf.immersionbar.ImmersionBar
import com.huantansheng.easyphotos.EasyPhotos
import com.kunminx.architecture.ui.callback.EventObserver
import com.kunminx.architecture.utils.SPUtils
import com.evport.businessapp.BuildConfig.DEBUG
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.bean.HomeFilter
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.WelcomeActivity
import com.evport.businessapp.ui.state.MainActivityViewModel
import com.evport.businessapp.utils.LiveBus.Companion.getInstance
import com.evport.businessapp.utils.getHomeFilterData
import com.evport.businessapp.utils.saveHomeFilterData
import com.evport.businessapp.ws.WsService
import com.stripe.android.CustomerSession
import org.jetbrains.anko.toast

import com.evport.businessapp.data.bean.MessageWrap
import com.evport.businessapp.ui.page.MainFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Create by KunMinX at 19/10/16
 */
const val OnMessageHome = "OnMessageHome"
const val OnMessageList = "OnMessageList"
const val OnMessageGunDetail = "OnMessageGunDetail"
const val OnMessageUserFamily = "OnMessageUserFamily"
const val ChargeGunDialog = "ChargeGunDialog"

class MainActivity() : BaseActivity() {
    private var mMainActivityViewModel: MainActivityViewModel? = null
    private val mIsListened = false
    override fun initViewModel() {
        mMainActivityViewModel =
            getActivityViewModel(MainActivityViewModel::class.java)
    }
//    val desc by lazy {
//        intent.getStringExtra("desc")?:""
//    }
//    val pk by lazy {
//        intent.getStringExtra("pk")?:""
//    }

    override fun getDataBindingConfig(): DataBindingConfig { //TODO tip 1: DataBinding ???????????????
// ??? DataBinding ??????????????? base ???????????????????????????????????????
// ??????????????????????????????????????? ?????????????????????????????????
// ??????????????????????????????????????????????????????????????? Jetpack Compose ?????????
// ??? DataBindingConfig ??????????????????????????????????????? base ???????????? DataBinding ??????????????????
// ?????????????????????????????????????????? https://xiaozhuanlan.com/topic/9816742350 ??? https://xiaozhuanlan.com/topic/2356748910
        return DataBindingConfig(R.layout.activity_main, mMainActivityViewModel)
            .addBindingParam(
                BR.event,
                EventHandler()
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("mainActivity", "onCreate")
        EasyPhotos.preLoad(this)//?????????????????????
        EventBus.getDefault().register(this)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()
        sharedViewModel.activityCanBeClosedDirectly.observe(
            this,
            EventObserver { aBoolean: Boolean? ->
                val nav = Navigation.findNavController(this, R.id.main_fragment_host)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.mainFragment) {
                    nav.navigateUp()
                } else {
                    super.onBackPressed()
                }
            }
        )

        sharedViewModel.enableSwipeDrawer.observe(
            this,
            EventObserver { aBoolean: Boolean? ->
                //TODO yes: ??? tip 1
                mMainActivityViewModel!!.allowDrawerOpen.setValue(aBoolean)
            }
        )
        sharedViewModel.isLoginSuccess.observe(this,
            EventObserver<Boolean> { aBoolean ->
                if (!aBoolean) {
                    logOut()
                }
            })
        getInstance().observeEvent(this, Observer { (type) ->
            if (type == Configs.EVENT_LOGOUT) {
                logOut()
            }
        })
//        getInstance().post(EventBean(desc,pk))

        //  disConnectSocket()
//        Handler().postDelayed(
//            {
        //connectSocket()
        // initTimer()


        Thread {
            Thread.sleep(50)
            //???????????????WebView??????
            val chatService = ComponentName(
                "com.evport.businessapp",
                "com.evport.businessapp.ws.WsService"
            )

            val intent = Intent();
            intent.component = chatService;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }

        }.start()

//        //???????????????WebView??????
//        Intent(this, WsService::class.java).also { intent ->
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent)
//            } else {
//                startService(intent)
//            }
//        }

//

    }

    override fun onStart() {
        super.onStart()

        Log.e("mainActivity", "onStart")
//        val homeFilter = getHomeFilterData() ?: HomeFilter()
//        if (!homeFilter.isDefault)
//            saveHomeFilterData(HomeFilter())
    }

    private fun logOut() { //                    ??????
        SPUtils.getInstance().put(Configs.TOKEN, "")
        CustomerSession.endCustomerSession()
        val homeFilter = getHomeFilterData() ?: HomeFilter()
        if (!homeFilter.isDefault)
            saveHomeFilterData(HomeFilter())
        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
        finish()
    }

    //    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (!mIsListened) {
//
//            // TODO tip 2????????????????????? UnPeekLiveData ????????? ????????????????????????????????????????????? ?????????
//
//            // ?????????????????????????????????????????? https://xiaozhuanlan.com/topic/0168753249
//            // --------
//            // ???????????????????????????????????????????????? ?????????????????????
//            // fragment ?????????????????? fragment ?????????????????????????????? Activity ?????????????????? Fragment ??????????????????
//            // ?????? fragment ??????????????????????????????????????????????????????????????? Activity????????????????????? Activity???
//
////            getSharedViewModel().timeToAddSlideListener.setValue(true);
//
//            mIsListened = true;
//        }
//    }
//    @Override
//    public void onBackPressed() {
//
//        // TODO ??? tip 2
//
////        getSharedViewModel().closeSlidePanelIfExpanded.setValue(true);
//    }
    inner class EventHandler : SimpleDrawerListener() {
        override fun onDrawerOpened(drawerView: View) {
            super.onDrawerOpened(drawerView)
            sharedViewModel.isDrawerOpened.set(true)
        }

        override fun onDrawerClosed(drawerView: View) {
            super.onDrawerClosed(drawerView)
            sharedViewModel.isDrawerOpened.set(false)
        }
    }

    val url =
        SPUtils.getInstance().getString(Configs.HOST).replaceFirst("http", "ws").plus("platform/")
            .plus(SPUtils.getInstance().getString(Configs.PHONE)).plus("_pcApp")
    val url1 = "wss://170s2247n7.51mypc.cn/platform/625297621@qq.com_EBOOSTAPP"

    override fun onResume() {
        super.onResume()
        // val subscription = RxWebSocket.get(url).subscribe()
        //  if (subscription == null || subscription.isDisposed) {
        // connectSocket()
        //   }
        Log.e("mainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("mainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        sharedViewModel.stopAPP.postValue(true)
        Log.e("mainActivity", "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("mainActivity", "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        WsService.isStop = stopService(Intent(this, WsService::class.java))
        EventBus.getDefault().unregister(this);
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (MainFragment.index == 3) {

            ImmersionBar.with(this)
                .statusBarDarkFont(true).init()
        }
        Log.e("TAG", "onBackPressed: =============" + MainFragment.index)
    }


    private fun connectSocket() {
        Log.e("hm-----url", url)
        /* try {
             RxWebSocket.get(url) //RxLifecycle : https://github.com/dhhAndroid/RxLifecycle
 //                .compose(RxLifecycle.with(this).bindToLifecycle())
                 .compose(RxLifecycle.with(this).bindToLifecycle())
                 .subscribe(object : WebSocketSubscriber() {
                     override fun onOpen(webSocket: WebSocket) {
                         Log.d("RxWebSockets", "onOpen1:")
                         toastT("RxWebSockets" + "onOpen1:")

                     }

                     override fun onMessage(text: String) {
                         Log.d("RxWebSockets", "????????????:$text")
                         toastT("RxWebSockets,????????????:$text")

                         when (ActivityUtils.getTopActivity().localClassName) {
                             "ui.page.activity.ChargeStatuListActivity" -> {
                                 getInstance().post(EventBean(OnMessageList, text, ""))
                                 toastT("RxWebSockets,ChargeStatuListActivity????????????socket??????")
                             }
                             "ui.page.activity.ChageGunDetailActivity" -> {
                                 getInstance().post(EventBean(OnMessageGunDetail, text, ""))
                                 toastT("RxWebSockets,????????????????????????socket??????")
                             }
                             "ui.page.activity.UserFamilyActivity" -> {
                                 getInstance().post(EventBean(OnMessageUserFamily, text, ""))
                                 toastT("RxWebSockets,??????????????????????????????socket??????")
                             }
                             else -> {
                                 getInstance().post(EventBean(OnMessageHome, text, ""))
                                 toastT("RxWebSockets,??????????????????socket??????")
                             }
                         }


                     }

                     override fun onMessage(byteString: ByteString) {
                         Log.d("RxWebSockets", "????????????:")
                         dismissLoading()
                     }


                     override fun onReconnect() {
                         Log.d("RxWebSockets", "??????:")
                         dismissLoading()
                     }

                     override fun onClose() {
                         getInstance().post(EventBean("onClose", "", ""))
                         Log.d("RxWebSockets", "onClose:")
                         toastT("RxWebSockets," + "onClose:")
                         dismissLoading()
                     }
                 })
         } catch (e: Exception) {
             Log.d("RxWebSockets", "e:" + e.message)
         }*/

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetMessage(message: MessageWrap) {
        Log.e("hm-----eventbus", message.data)
        if (message.type == 1) {
            var text = message.data
            toastT("RxWebSockets,????????????:$text")
            when (ActivityUtils.getTopActivity().localClassName) {
                "ui.page.activity.ChargeStatuListActivity" -> {
                    getInstance().post(EventBean(OnMessageList, text, ""))
                    sharedViewModel.refreshNav2.postValue(false)
                    toastT("RxWebSockets,ChargeStatuListActivity????????????socket??????")
                }
                "ui.page.activity.ChageGunDetailActivity" -> {
                    getInstance().post(EventBean(OnMessageGunDetail, text, ""))
                    toastT("RxWebSockets,????????????????????????socket??????")
                }
                "ui.page.activity.UserFamilyActivity" -> {
                    getInstance().post(EventBean(OnMessageUserFamily, text, ""))
                    toastT("RxWebSockets,??????????????????????????????socket??????")
                }
                else -> {
                    getInstance().post(EventBean(OnMessageHome, text, ""))
                    toastT("RxWebSockets,??????????????????socket??????")
                }
            }
        }
    }

    fun toastT(srt: String) {
        if (DEBUG)
            toast(srt)
    }

}