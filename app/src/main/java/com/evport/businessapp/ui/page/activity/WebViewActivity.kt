package com.evport.businessapp.ui.page.activity

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.WebViewViewModel
import com.evport.businessapp.utils.WebViewUtils
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : BaseActivity() {
    private var webUrl: String = ""
    private var title: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()
        initView()
    }

    private var mViewModel: WebViewViewModel? = null
    override fun initViewModel() {
        mViewModel = getActivityViewModel(WebViewViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_web_view, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    inner class ClickProxy {
        fun back() {
            finish()
        }

    }

    fun initView() {
        title=intent.getStringExtra("title")!!
        title_tv.text = title
        WebViewUtils.initWebSettings(this, web_view)
        web_view.apply {
            webViewClient = object : WebViewClient() {}
            webChromeClient = object : WebChromeClient() {}
            webUrl = intent.getStringExtra("url")!!
            Log.e("hm---webUrl",webUrl)
            loadUrl(webUrl)
        }
    }
}