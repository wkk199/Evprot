package com.evport.businessapp.ui.page.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.R
import kotlinx.android.synthetic.main.activity_how_use.*

class HowUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .statusBarColor(R.color.white)
            .statusBarDarkFont(true)
            .init()
        setContentView(R.layout.activity_how_use)
        back.setOnClickListener {
            finish()
        }
    }
}