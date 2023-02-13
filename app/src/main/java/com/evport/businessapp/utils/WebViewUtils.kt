package com.evport.businessapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import com.evport.businessapp.App.Companion.appContext
import java.io.File

/**
 *    Created by Administrator on 2021/10/16.
 *    Desc :
 */
object WebViewUtils {

    @SuppressLint("SetJavaScriptEnabled")
    fun initWebSettings(context: Context, webView: WebView) {
        val webSetting: WebSettings = webView.settings
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true)
        webSetting.setAppCacheEnabled(true)
        webSetting.databaseEnabled = true
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
//        webSetting.setAppCachePath(createCacheDirectory(context, "app"))
//        webSetting.databasePath = createCacheDirectory(context, "dataBase")
        webSetting.setGeolocationDatabasePath(context.getDir("geolocation", 0).path)
        webSetting.cacheMode = (WebSettings.LOAD_DEFAULT)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    fun cleanWebViewCache() {
        WebStorage.getInstance().deleteAllData()

        val cookieManager = CookieManager.getInstance()
        cookieManager.removeSessionCookies {

        }
        cookieManager.removeAllCookies {

        }
        cookieManager.flush()


        appContext.deleteDatabase("webviewCache.db")
        appContext.deleteDatabase("webview.db")

        val webView = WebView(appContext)
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()

        clearCacheFolder(appContext.cacheDir)
    }

    fun clearCacheFolder(dir: File?): Int {
        var deletedFiles = 0
        if (dir != null && dir.isDirectory) {
            try {
                dir.listFiles().forEach {

                    if (it.isDirectory && it.name.equals("WebView")) {
                        deletedFiles += clearCacheFolder(it)
                    }
                }
            } catch (e: Exception) {

            }
        }
        return deletedFiles;
    }

}