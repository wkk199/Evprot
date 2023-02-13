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
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.NotiSys
import com.evport.businessapp.data.bean.StationCommentReq
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.NotificationSysAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.onLoadMoreListener
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_notification_sys_list.empty_view
import kotlinx.android.synthetic.main.fragment_notification_sys_list.refreshLayout
import kotlinx.android.synthetic.main.fragment_notification_sys_list.selle_recycler_view

/**
 * Create by KunMinX at 19/10/29
 */
class NotiSysListFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_notification_sys_list, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, NotificationSysAdapter(requireContext()).apply {
                setOnItemClickListener { item, position ->
                    nav().navigate(R.id.action_global_notiSysDetailFragment,
                        Bundle().apply
                        {
                            putParcelable("sys", item)
                            putString("pk", item.sysMessagePk)
                        }
                    )
                }
            })
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        LiveBus.getInstance().observeEvent(this, Observer { (type) ->
            if (type == Configs.NOTIFICATION_MSG) {
                getData()

            }
        })
    }

    public override fun loadInitData() {
        super.loadInitData()
        getData()
        showLoading()

        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            pageNumber = 1
            canLoadMore = true
            getData()
            showLoading()
            refreshLayout.isRefreshing = false
        }

        selle_recycler_view.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {


                if (canLoadMore) {
                    pageNumber++
                    getData()
                }

            }
        })
    }

    var canLoadMore = true
    var pageNumber = 1
    var allList = ArrayList<NotiSys>()
    fun getData() {
        object : NetworkBoundResource<List<NotiSys>>(networkStatusCallback = object :
            NetworkStatusCallback<List<NotiSys>> {

            override fun onSuccess(data: List<NotiSys>?) {
                dismissLoading()
                data?.apply {
                    if (pageNumber == 1) {
                        allList.clear()
                    }
                    allList.addAll(this)
                    mDrawerViewModel?.listNotiSys?.value = allList
                }
                canLoadMore = !data.isNullOrEmpty()
                empty_view.visibility = if (allList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                empty_view.visibility = if (allList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<NotiSys>>> {
                return SingletonFactory.apiService.getNotiSys(
                    StationCommentReq(pageNum = pageNumber)
                )
            }
        }
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }
}