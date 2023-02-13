package com.evport.businessapp.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseActivity


/**
 * Created by mac on 2018/7/31.
 */
class LoadingView(context: Context) : Dialog(context, R.style.BaseDialog) {
    private var mmContext: BaseActivity = context as BaseActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_loading_layout)
//        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setOnCancelListener {
            mmContext.stopLoading()
        }

    }
}