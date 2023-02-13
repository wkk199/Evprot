package com.evport.businessapp.ui.page.activity

import android.os.Bundle
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.RecordDetailResp
import com.evport.businessapp.data.bean.RequestRecordDetail
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.CreateCommentActivity
import com.evport.businessapp.ui.state.RecordViewModel
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_record_detail.rl_card4
import kotlinx.android.synthetic.main.activity_record_detail.tv_en3
import kotlinx.android.synthetic.main.activity_record_detail.tv_show_detail
import kotlinx.android.synthetic.main.activity_record_detail.tv_start_en3
import org.jetbrains.anko.startActivity

class RecordDetailActivity : BaseActivity() {
    val pk by lazy {
        intent.getStringExtra("record")
    }
    private var mDrawerViewModel: RecordViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getActivityViewModel(RecordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_record_detail, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
//            .addBindingParam(BR.info,data)
    }


    inner class ClickProxy {
        fun back() {
            finish()
        }

        fun comment() {
            startActivity<CreateCommentActivity>(Pair("record", pk))
//            nav().navigate(R.id.action_global_createCommentFragment,
//                Bundle().apply
//                { putString("record", pk) })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()
        getData()
        tv_show_detail.setOnClickListener {
            rl_card4.isVisible = !rl_card4.isVisible
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    fun getData() {
        object : NetworkBoundResource<RecordDetailResp>(networkStatusCallback = object :
            NetworkStatusCallback<RecordDetailResp> {

            override fun onSuccess(data: RecordDetailResp?) {
                data?.apply {
                    mDrawerViewModel?.info?.value = data
                    tv_start_en3.isVisible = !balance.isNullOrBlank()
                    tv_en3.isVisible = !balance.isNullOrBlank()
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<RecordDetailResp>> {
                return SingletonFactory.apiService.recordsDetail(
                    requestRecord = RequestRecordDetail(
                        pk
                    )
                )
            }
        }
    }
}