package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.MainActivity
import com.evport.businessapp.R
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.kunminx.architecture.utils.SPUtils

class WelcomeActivity : BaseActivity() {


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_welcome, sharedViewModel)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .statusBarDarkFont(true)
            .init()

        if (!SPUtils.getInstance().getString(Configs.TOKEN).isNullOrEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        sharedViewModel.isLoginSuccess.observe(this
        ) { isLogin: Boolean ->
            Log.e("hm----isLogin", isLogin.toString())
            if (isLogin) { //                登录成功
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
            }
        }

    }

    override fun initViewModel() {
    }

}



