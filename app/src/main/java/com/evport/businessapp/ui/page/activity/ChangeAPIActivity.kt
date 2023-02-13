package com.evport.businessapp.ui.page.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.blankj.utilcode.util.SPUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.MainViewModel
import kotlinx.android.synthetic.main.activity_change_api.*
import org.jetbrains.anko.runOnUiThread

class ChangeAPIActivity : BaseActivity() {


    private lateinit var mMainViewModel  : MainViewModel
    override fun initViewModel() {
        mMainViewModel = getActivityViewModel(MainViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_change_api, mMainViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        绑定试图
//        val binding =
//            DataBindingUtil.setContentView<ActivityChangeApiBinding>(this, R.layout.activity_change_api)
//        binding.lifecycleOwner = this
////        binding.toolbar.toolbar_title.text = resources.getString(R.string.activity_title_internet_api)
////        binding.toolbar.left_image.setOnClickListener {
////            finish()
////        }
//        binding.vm = mMainViewModel
//        binding.click = ClickProxy()

        showNowIP()
        et_ipaddress.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
               checkEnable()
            }

        })

    }

    //    检查按钮是否可用
    fun checkEnable(){
        mMainViewModel.pingBtnEnable.set(
            !mMainViewModel.ipAddress.get().isNullOrBlank()
        )
    }

    inner class ClickProxy {
        fun toPing() {
            pingIp()
        }
    }

    fun showNowIP(){
        mMainViewModel.pingNotice.set("当前API地址：\n${SPUtils.getInstance().getString(Configs.HOST)}")
        SingletonFactory.changeAPI()
    }

    fun  pingIp(){
        var str = "${mMainViewModel.ipAddress.get().toString()}:${mMainViewModel.ipPort.get().toString()}/"
        if (str.startsWith("http://")||str.startsWith("https://")) {
            SPUtils.getInstance()
                .put(Configs.HOST, str)
            showNowIP()
        }else{
            str = "http://$str"
            SPUtils.getInstance()
                .put(Configs.HOST, str)
            showNowIP()
        }
        Configs.WEB_BASE_URL = SPUtils.getInstance().getString(Configs.HOST).replaceFirst("http", "ws")
    }
}