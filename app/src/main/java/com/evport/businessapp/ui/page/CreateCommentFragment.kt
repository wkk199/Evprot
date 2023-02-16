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
package com.evport.businessapp.ui.page

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.data.bean.CommentAdd
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.RecordViewModel
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_create_comment.*

/**
 * Create by KunMinX at 19/10/29
 */
class CreateCommentActivity : BaseActivity() {
    private var mDrawerViewModel: RecordViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getActivityViewModel(RecordViewModel::class.java)
    }

    val pk by lazy {
        intent?.getStringExtra("record") ?: ""
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_create_comment, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
//            .addBindingParam(BR.info,data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ImmersionBar.with(this)
//            .navigationBarColor(R.color.black)
//            .keyboardEnable(false)
//            .init()
        if (pk.isEmpty())
            finish()
        initView()
    }

    fun initView() {
        et_reason.doOnTextChanged { text, start, before, count ->
            count_num.text = "${text!!.length}/500"
        }
    }

    inner class ClickProxy {
        fun back() {
            finish()
        }

        fun comment() {
            if (et_reason.text.toString().trim().isEmpty()) {
                showLongToast("content is empty")
                return
            }
            if (ratingBar.rating <= 0) {
                showLongToast("score is empty")
                return
            }
            getData()
        }
    }

    fun getData() {
        showLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                ToastUtils.showShort("Submit Successfully")
                finish()
                dismissLoading()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.addComment(
                    CommentAdd(
                        transactionPk = pk,
                        content = et_reason.text.toString(),
                        rating = ratingBar.rating.toString()
                    )
                )
            }
        }
    }

}