package com.evport.businessapp.ui.page.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.MainActivityViewModel
import com.pdfview.subsamplincscaleimageview.SubsamplingScaleImageView
import kotlinx.android.synthetic.main.activity_pdfview.*

class PdfviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        back.setOnClickListener {
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        title_tv.text = intent.getStringExtra("title")!!
        val stringExtra = intent.getStringExtra("url")!!
        back.setOnClickListener {
            this.finish()
        }
        pdf_view.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)
        pdf_view.fromAsset(stringExtra)
        pdf_view.show()


    }


    private var mMainActivityViewModel: MainActivityViewModel? = null

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_pdfview, mMainActivityViewModel)
    }

    override fun initViewModel() {
        mMainActivityViewModel =
            getActivityViewModel(MainActivityViewModel::class.java)
    }
}