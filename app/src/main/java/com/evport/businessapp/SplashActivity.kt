package com.evport.businessapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.evport.businessapp.data.bean.HomeFilter
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.WelcomeActivity
import com.evport.businessapp.ui.state.MainActivityViewModel
import com.evport.businessapp.utils.getHomeFilterData
import com.evport.businessapp.utils.saveHomeFilterData
import com.evport.businessapp.utils.toast
import com.gyf.immersionbar.ImmersionBar
import com.kunminx.architecture.utils.SPUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    private var mMainActivityViewModel: MainActivityViewModel? = null

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_splash, mMainActivityViewModel)
    }

    override fun initViewModel() {
        mMainActivityViewModel =
            getActivityViewModel(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        closeKeyWord()
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()
        // 关闭暗黑模式
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val homeFilter = getHomeFilterData()?: HomeFilter()
        if (SPUtils.getInstance().getString(Configs.HOST).isNullOrEmpty())
            SPUtils.getInstance().put(Configs.HOST, Configs.BASE_URL)
//        SPUtils.getInstance()
//            .put(Configs.HOST, Configs.BASE_URL)
        Configs.WEB_BASE_URL =
            SPUtils.getInstance().getString(Configs.HOST).replaceFirst("http", "ws")
      //  if(SPUtils.getInstance().getInt(Configs.LANGUAGE, 0)==0){
         //   startActivity<ChangeLanguageActivity>(Pair("first",true))
          //  finish()
        //}else {
            if (!homeFilter.isDefault)
                saveHomeFilterData(HomeFilter())
            root.postDelayed({
                link()
//            getUrl()
            }, 1000)
       // }

    }

    override fun finish() {
        super.finish()
        SPUtils.getInstance().put(Configs.LANGUAGE, 1)
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    fun link() {
        if (!SPUtils.getInstance().getString(Configs.TOKEN).isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        finish()
    }


    fun getUrl(
    ) {
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                data?.apply {
                    SPUtils.getInstance()
//                        .put(Configs.HOST, data.plus("/"))
                        .put(Configs.HOST, "https://cloud.cnpowercore.com:8091")
//                    Configs.BASE_URL = data.plus("/")
                    Configs.WEB_BASE_URL =
                        Configs.BASE_URL.replaceFirst("http", "ws").plus("platform/")
//                       todo url test
//                    Configs.WEB_BASE_URL =
//                        "http://192.168.0.39:8090/".replace("http", "ws").plus("platform/")

//                    LiveBus.getInstance().post(EventBean("ChangeUrl",""))

                    link()
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                link()

                SPUtils.getInstance()
                    .put(Configs.HOST, Configs.BASE_URL)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
//                return SingletonFactory.apiService.usersAuth(mapOf(Pair("user", user)))
                return SingletonFactory.apiServiceSplash.getUrl()
            }
        }
    }

}
