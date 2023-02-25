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
package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.base.binding_adapter.TabPageBindingAdapter
import com.evport.businessapp.ui.page.STATION_ITEM
import com.evport.businessapp.ui.page.adapter.ChargeGunListAdapter
import com.evport.businessapp.ui.page.adapter.StationDeviceAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.*
import com.kunminx.architecture.utils.Utils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_user_collect.*
import kotlinx.android.synthetic.main.activity_user_collect.empty_view
import kotlinx.android.synthetic.main.activity_user_collect.refreshLayout
import org.jetbrains.anko.startActivity


/**
 * Create by KunMinX at 19/10/29
 */
class UserCollectActivity : BaseActivity() {
    private var canLoadMore = true
    private var refreshing = false
    private lateinit var mStationViewModel: StationViewModel


    override fun initViewModel() {
        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    lateinit var adapter: ChargeGunListAdapter
    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = ChargeGunListAdapter(this).apply {
            setOnItemClickListener { item, position ->
                startActivity<ChargeStationDetailActivity>(
                    Pair(STATION_ITEM, item)
                )

            }
        }
        return DataBindingConfig(R.layout.activity_user_collect, mStationViewModel).addBindingParam(
            BR.click, ClickProxy()
        ).addBindingParam(
            BR.adapter, adapter
        ).addBindingParam(BR.adapterDevice, StationDeviceAdapter(baseContext).apply {
            childClick = { item, p ->
                startActivity(Intent(
                    this@UserCollectActivity, ChageGunDetailActivity::class.java
                ).apply {
                    putExtra("pk", item.connectorPk)
                })
            }
            likeClick = { item, p ->
                if (item.isCollect) {
                    collectionDel(item.chargerPk.toString(), p)
                    item.isCollect = false
                } else {
                    collectionAdd(item.chargerPk.toString(), p)
                    item.isCollect = true
                }
            }
        })
    }

    var lat = 0.0f
    var lng = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lat = baseContext.getLat()
        lng = baseContext.getLng()



        getDeviceData()
        getStationData()
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            pageNumDevice = 1
            pageNumStation = 1
            canLoadMore = true
            refreshing = true
            getDeviceData()
            getStationData()

            refreshLayout.isRefreshing = false
        }
//        refreshLayout_station.isRefreshing = false
//        refreshLayout_station.setOnRefreshListener {
//            pageNumStation = 1
//            canLoadMore = true
//            getData()
//            getStationData()
//            refreshLayout_station.isRefreshing = false
//        }
        list_stations.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                if (canLoadMore) {

                    pageNumStation++
                    getStationData()

                } else {
//                    ToastUtils.showShort("no more data")
                }
            }
        })
        list_device.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                if (canLoadMore) {

                    pageNumDevice++
                    getDeviceData()

                } else {
//                    ToastUtils.showShort("no more data")
                }
            }
        })


//        view_pager.adapter = CommonViewPagerAdapter(
//            2, false,
//            arrayOf("站点", "device"), null
//        )
//
//        tab_station.setupWithViewPager(view_pager)
//        tabSelectedListener(tab_station, "站点", "Device")
//
//
//        tab_station.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                if (tab.customView != null) {
//                    val tab_text = tab.customView!!.findViewById<TextView>(R.id.tab_content_text)
//                    tab_text.setTextColor(Utils.getApp().resources.getColor(R.color.colorTheme))
//                    val imageView = tab.customView!!.findViewById<ImageView>(R.id.tab_content_text1)
//                    imageView.setBackgroundResource(R.drawable.layer_tab_indicator)
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                if (tab.customView != null) {
//                    val tab_text = tab.customView!!.findViewById<TextView>(R.id.tab_content_text)
//                    tab_text.setTextColor(Utils.getApp().resources.getColor(R.color.black))
//                    val imageView = tab.customView!!.findViewById<ImageView>(R.id.tab_content_text1)
//                    imageView.setBackgroundResource(0)
//                }
//            }
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })

        for (i in 0 until tab_station.tabCount) {
            val tab: TabLayout.Tab = tab_station.getTabAt(i)!!
            if (tab != null) {
                tab.view.isLongClickable = false
                // 针对android 26及以上需要设置setTooltipText为null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 可以设置null也可以是""
                    tab.view.tooltipText = null
                    // tab.view.setTooltipText("");
                }
            }
        }



        tab_station.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.customView != null) {
                    val tab_text = tab.customView!!.findViewById<TextView>(R.id.tab_content_text)
                    tab_text.setTextColor(Utils.getApp().resources.getColor(R.color.colorTheme))
                    val imageView = tab.customView!!.findViewById<ImageView>(R.id.tab_content_text1)
                    imageView.setBackgroundResource(R.drawable.layer_tab_indicator)
                }
                if (!refreshing)
                    currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.customView != null) {
                    val tab_text = tab.customView!!.findViewById<TextView>(R.id.tab_content_text)
                    tab_text.setTextColor(Utils.getApp().resources.getColor(R.color.black))
                    val imageView = tab.customView!!.findViewById<ImageView>(R.id.tab_content_text1)
                    imageView.setBackgroundResource(0)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    var currentItem = 0

    inner class ClickProxy {
        fun back() {

            finish()
        }

        fun login() {

        }
    }


    // position <0 就是收藏充电站
    fun collectionAdd(pk: String, position: Int) {

        val date = DateUtil.getNow()
        object :
            NetworkBoundResource<Any>(networkStatusCallback = object : NetworkStatusCallback<Any> {

                override fun onSuccess(data: Any?) {
                    pageNumDevice = 1
                    getDeviceData()

                }

                override fun onFailure(message: String) {
                    if (!message.isNullOrBlank()) {
                        message.toast()
                    }

                }

            }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.collectionAdd(CollectionAdd(pk, date))
            }
        }
    }

    // position <0 就是收藏充电站
    fun collectionDel(pk: String, position: Int) {
        val date = DateUtil.getNow()
        object :
            NetworkBoundResource<Any>(networkStatusCallback = object : NetworkStatusCallback<Any> {

                override fun onSuccess(data: Any?) {
                    pageNumDevice = 1
                    getDeviceData()

                }

                override fun onFailure(message: String) {
                    if (!message.isNullOrBlank()) {
                        message.toast()
                    }

                }

            }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.collectionDelete(CollectionAdd(pk, date))
            }
        }
    }

    private var allListStation = ArrayList<StationListBean>()
    private var allListDevice = ArrayList<Device>()


    var isStationData = false
    var isStationCommentData = false


    var pageNumStation = 1
    var pageNumDevice = 1
    fun getStationData() {
        object : NetworkBoundResource<CollectList>(networkStatusCallback = object :
            NetworkStatusCallback<CollectList> {

            override fun onSuccess(data: CollectList?) {
                data?.stations?.data?.apply {
                    if (pageNumStation > 1) {
                        allListStation.addAll(this)
                    } else {
                        allListStation.clear()
                        allListStation.addAll(this)

                    }
                    mStationViewModel.gunListBean.value = allListStation
                }
                if (data?.stations == null || data.stations?.data == null) {
                    allListStation.clear()
                    mStationViewModel.gunListBean.value = allListStation
                }

//                tab_station.getTabAt(0)?.text =
//                    "${resources.getString(R.string.stations)}(${data?.stations?.counts ?: 0})"

                isStationData = true
                counts = resources.getString(R.string.stations)+" (" + (data?.stations?.counts ?: 0)+")"
                if (isStationData && isStationCommentData) {

                    TabPageBindingAdapter.tabSelectedListener(
                        tab_station, counts, deviceCounts,"收藏"
                    )
                    view_pager.currentItem = currentItem
                    isStationData =false
                    refreshing = false
                }

                empty_view.visibility =
                    if (allListStation.isNullOrEmpty()) View.VISIBLE else View.GONE

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<CollectList>> {
                return SingletonFactory.apiService.getCollection(
                    getCollectReq(
                        latitude = lat, longitude = lng, type = "station", pageNum = pageNumStation
                    )
                )
            }
        }
    }

    var counts = ""
    var deviceCounts = ""


    fun getDeviceData() {
        object : NetworkBoundResource<CollectList>(networkStatusCallback = object :
            NetworkStatusCallback<CollectList> {

            override fun onSuccess(data: CollectList?) {
                data?.devices?.data?.apply {
                    if (pageNumDevice > 1) {
                        allListDevice.addAll(this)
                    } else {
                        allListDevice.clear()
                        allListDevice.addAll(this)

                    }
                    empty_view1.visibility =
                        if (allListDevice.isNullOrEmpty()) View.VISIBLE else View.GONE
                    mStationViewModel.deviceListBean?.value = allListDevice
                }
                if (data?.devices == null || data.devices?.data == null) {
                    allListDevice.clear()
                    mStationViewModel.deviceListBean?.value = allListDevice
                }
                isStationCommentData = true
                deviceCounts =
                    resources.getString(R.string.device) +" ("+ (data?.devices?.counts ?: 0)+")"
                if (isStationData && isStationCommentData) {
                    TabPageBindingAdapter.tabSelectedListener(
                        tab_station, counts, deviceCounts,"收藏"
                    )
                    view_pager.currentItem = currentItem
                    isStationCommentData =false
                    refreshing = false
                }

//                tab_station.getTabAt(1)?.text =
//                    "${resources.getString(R.string.device)}(${data?.devices?.counts ?: 0})"
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<CollectList>> {
                return SingletonFactory.apiService.getCollection(
                    getCollectReq(
                        latitude = lat, longitude = lng, type = "charger", pageNum = pageNumDevice
                    )
                )
            }
        }
    }


}