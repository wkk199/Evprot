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

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.kunminx.architecture.domain.manager.NetState
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.RecordResp
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.ChargeStatuListActivity
import com.evport.businessapp.ui.page.adapter.RecordAdapter
import com.evport.businessapp.ui.state.RecordViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.SpaceItemDecoration
import com.evport.businessapp.utils.onLoadMoreListener
import com.evport.businessapp.utils.todp
import kotlinx.android.synthetic.main.fragment_home_nav3.*


/**
 * Create by KunMinX at 19/10/29
 */
const val PAGESIZE = 10

class HomeNav3Fragment : BaseFragment() {
    private var recordViewModel: RecordViewModel? = null

    //    private var mDrawerViewModel: DrawerViewModel? = null
    var page: Int = 1
    var canLoadMore: Boolean = true
    var allList = mutableListOf<RecordResp>()
    lateinit var mAdapter: RecordAdapter

    override fun initViewModel() {
        recordViewModel = getFragmentViewModel(RecordViewModel::class.java)
//        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = RecordAdapter(context).apply {
            setOnItemClickListener { item, position ->
//                nav().navigate(R.id.action_global_recordDetailFragment,
//                    Bundle().apply
//                    { putString("record", item?.transactionPk) })



                if (item.flag) {
                    nav().navigate(R.id.action_global_recordDetailFragment,
                        Bundle().apply
                        { putString("record", item?.transactionPk) })
                } else {
                    val intent = Intent(
                        activity,
                        ChargeStatuListActivity::class.java
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

//                if (item.isChargingState()) {
//                    nav().navigate(R.id.action_global_recordDetailFragment,
//                        Bundle().apply
//                        { putString("record", item?.transactionPk) })
//                } else {
//                    val intent = Intent(
//                        activity,
//                        ChargeStatuListActivity::class.java
//                    )
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(intent)
//                }
            }
        }
        return DataBindingConfig(R.layout.fragment_home_nav3, recordViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, mAdapter)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        //
        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == Configs.RECODE) {
                Log.e("hm----s刷新","刷新")
                page = 1
                canLoadMore = true
                recordViewModel?.requestRecord?.pageNum = page
                recordViewModel!!.requestRecord.transactionPk=""
                recordViewModel?.requestRecord(recordViewModel?.requestRecord)
                refreshLayout.isRefreshing = false
            }
        })
        recordViewModel?.recordLiveData?.observe(viewLifecycleOwner, Observer {

            dismissLoading()
            if (page == 1) {
                allList.clear()
            }
            allList.addAll(it)
            recordViewModel?.list?.value = allList
            mAdapter.submitList(allList)
            canLoadMore = !it.isNullOrEmpty()
            if (allList.size==0){
                empty_view.visibility=View.VISIBLE
            }else{
                empty_view.visibility=View.GONE
            }
        })

        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            page = 1
            canLoadMore = true
            recordViewModel?.requestRecord?.pageNum = page
            recordViewModel!!.requestRecord.transactionPk=""
            recordViewModel?.requestRecord(recordViewModel?.requestRecord)
            refreshLayout.isRefreshing = false
        }

        rv.addItemDecoration(
            SpaceItemDecoration(
                10.todp(),
                ContextCompat.getColor(requireContext(), android.R.color.transparent),
                SpaceItemDecoration.HORIZONTAL_LIST
            )
        )
        rv.adapter = mAdapter
        rv.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                if (canLoadMore) {

                    page++
                    recordViewModel?.requestRecord?.pageNum = page
                    if (allList.size!=0){
                        recordViewModel!!.requestRecord.transactionPk=allList[allList.size-1].transactionPk
                    }
                    recordViewModel?.requestRecord(recordViewModel?.requestRecord)

                } else {
//                    ToastUtils.showShort("no more data")
                }
            }
        })
        recordViewModel?.requestRecord?.desc?.apply {
            if (this) {
                tv_array.text = resources.getString(R.string.Desc)
                tv_array.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.icon_down, null),
                    null
                )
            } else {
                tv_array.text = resources.getString(R.string.Asc)
                tv_array.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.icon_up, null),
                    null
                )


            }
        }

    }

    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    override fun onResume() {
        super.onResume()
        if (allList.isEmpty())
            recordViewModel?.requestRecord(recordViewModel?.requestRecord)
    }

    override fun onPause() {
        super.onPause()
    }


    inner class ClickProxy {
        fun selectRes() {

            val a = arrayOf(
                resources.getString(R.string.Date), resources.getString(R.string.energy),
                resources.getString(R.string.spendTime), resources.getString(R.string.Money)
            )
            AlertDialog.Builder(requireContext())
//            .setTitle("Area Code")
                .setItems(
                    a
                ) { dialogInterface, i ->
                    showLoading()
                    recordViewModel?.dataRes?.set(a[i])
                    when (i) {
                        0 -> {
                            recordViewModel?.requestRecord?.order = "date"
                        }
                        1 -> {
                            recordViewModel?.requestRecord?.order = "energy"
                        }
                        2 -> {
                            recordViewModel?.requestRecord?.order = "spendTime"
                        }
                        3 -> {
                            recordViewModel?.requestRecord?.order = "money"
                        }
                        else -> {
                            recordViewModel?.requestRecord?.order = "date"
                        }
                    }
                    page = 1
                    recordViewModel?.requestRecord?.pageNum = page
                    recordViewModel!!.requestRecord.transactionPk=""
                    recordViewModel?.requestRecord(recordViewModel?.requestRecord)

                }.create()
                .show()
        }

        fun array() {

            recordViewModel?.requestRecord?.desc?.apply {
                Log.e("des", "des")
                recordViewModel?.requestRecord?.desc = !this
                page = 1
                canLoadMore = true
                recordViewModel?.requestRecord?.pageNum = page
                recordViewModel!!.requestRecord.transactionPk=""
                recordViewModel?.requestRecord(recordViewModel?.requestRecord)
                if (!this) {
                    tv_array.text = resources.getString(R.string.Desc)
                    tv_array.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.icon_down, null),
                        null
                    )
                } else {
                    tv_array.text = resources.getString(R.string.Asc)
                    tv_array.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        resources.getDrawable(R.drawable.icon_up, null),
                        null
                    )


                }
            }
        }
    }


}