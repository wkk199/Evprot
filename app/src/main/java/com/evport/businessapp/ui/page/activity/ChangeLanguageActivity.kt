package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.blankj.utilcode.util.LanguageUtils
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_change_language.*
import java.util.*


class ChangeLanguageActivity : BaseActivity() {


    private var mMainActivityViewModel: MainActivityViewModel? = null

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_change_language, mMainActivityViewModel)
            .addBindingParam(
            BR.click,
            ClickProxy()
        )
    }

    val first by lazy {
        intent.getBooleanExtra("first",false)
    }
    override fun initViewModel() {
        mMainActivityViewModel =
            getActivityViewModel(MainActivityViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spanish = Locale("iw")

        isEnglish = !LanguageUtils.isAppliedLanguage(spanish)
        change()


    }

    var isEnglish = true

    fun downloadRes(){

        val splitInstallManager = SplitInstallManagerFactory.create(this)

//        处理监听
        // Initializes a variable to later track the session ID for a given request.
        var mySessionId = 0

// Creates a listener for request status updates.
        val listener = SplitInstallStateUpdatedListener { state ->
            if (state.sessionId() == mySessionId) {
                // Read the status of the request to handle the state update.
            }
        }

// Registers the listener.
        splitInstallManager.registerListener(listener)





//
        // Uses the addLanguage() method to include French language resources in the request.
        // Note that country codes are ignored. That is, if your app
        // includes resources for “fr-FR” and “fr-CA”, resources for both
        // country codes are downloaded when requesting resources for "fr".
        val request = SplitInstallRequest.newBuilder().addLanguage(Locale.forLanguageTag("es"))
            .build()

// Submits the request to install the additional language resources.
//        splitInstallManager.startInstall(request)


        splitInstallManager
            .startInstall(request)
            // When the platform accepts your request to download
            // an on demand module, it binds it to the following session ID.
            // You use this ID to track further status updates for the request.
            .addOnSuccessListener { sessionId -> mySessionId = sessionId }
            // You should also add the following listener to handle any errors
            // processing the request.
            .addOnFailureListener { exception ->
                // Handle request errors.
            }

    }

    override fun onDestroy() {
        super.onDestroy()

// When your app no longer requires further updates, unregister the listener.
//        splitInstallManager.unregisterListener(listener)
    }

    fun change() {
        english.isSelected = isEnglish
        sp.isSelected = !isEnglish
        iv_select_en.isVisible = isEnglish
        iv_select_sp.isVisible = !isEnglish
        if (isEnglish) {
            english.setTextColor(resources.getColor(R.color.white))
            sp.setTextColor(resources.getColor(R.color.black))
        } else {
            english.setTextColor(resources.getColor(R.color.black))
            sp.setTextColor(resources.getColor(R.color.white))
        }
    }

    override fun onBackPressed() {
        if (first){
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }else{
            super.onBackPressed()
        }
    }

    inner class ClickProxy {
        fun toEng() {
            isEnglish = true
            change()
        }

        fun toSp() {
            isEnglish = false
            change()
        }

        fun back() {
            finish()
        }

        fun ok() {
            if (isEnglish) {
                LanguageUtils.applyLanguage(Locale.ENGLISH, true)
            } else {
                val spanish = Locale("iw")
                LanguageUtils.applyLanguage(spanish, true)
            }
        }
    }
}
