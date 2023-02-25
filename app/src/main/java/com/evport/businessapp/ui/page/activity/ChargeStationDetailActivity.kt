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
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.App
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.base.binding_adapter.TabPageBindingAdapter
import com.evport.businessapp.ui.page.MAX_POWER
import com.evport.businessapp.ui.page.MIN_POWER
import com.evport.businessapp.ui.page.STATION_ITEM
import com.evport.businessapp.ui.page.STATUS
import com.evport.businessapp.ui.page.adapter.ChargeGunListChildAdapter
import com.evport.businessapp.ui.page.adapter.StationCommentAdapter
import com.evport.businessapp.ui.page.adapter.StationDeviceAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.*
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kunminx.architecture.utils.Utils
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_charge_station_detail.*
import kotlinx.android.synthetic.main.activity_charge_station_detail.appbar_layout
import kotlinx.android.synthetic.main.activity_charge_station_detail.refreshLayout
import kotlinx.android.synthetic.main.activity_charge_station_detail.tab_station
import java.lang.Float.min


/**
 * Create by KunMinX at 19/10/29
 */
class ChargeStationDetailActivity : BaseActivity() {
    private var canLoadMore = true
    private var refreshing = false
    private var commentPage = 1
    var stationCommentReq = StationCommentReq(pageNum = commentPage, stationPk = "-1")
    private lateinit var mStationViewModel: StationViewModel
    var imageList = ArrayList<String>()

    private val mStationItem by lazy {
        intent.getParcelableExtra(STATION_ITEM) as StationListBean?

    }
    private val minPower by lazy {
        intent.getStringExtra(MIN_POWER)
    }
    private val maxPower by lazy {
        intent.getStringExtra(MAX_POWER)
    }
    private val status by lazy {
        intent.getStringExtra(STATUS)
    }

    override fun initViewModel() {
        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {

        return DataBindingConfig(R.layout.activity_charge_station_detail, mStationViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                ChargeGunListChildAdapter(baseContext)
            )
            .addBindingParam(
                BR.adapterDevice,
                StationDeviceAdapter(baseContext).apply {
                    likeClick = { item, p ->
                        if (item.isCollect) {
                            collectionDel(item.chargerPk.toString(), p)
//                            item.isCollect = false
                        } else {
                            collectionAdd(item.chargerPk.toString(), p)
//                            item.isCollect = true
                        }
//                        notifyDataSetChanged()
                    }
                    childClick = { item, p ->
                        startActivity(
                            Intent(
                                this@ChargeStationDetailActivity,
                                ChageGunDetailActivity::class.java
                            ).apply {
                                putExtra("pk", item.connectorPk)
                            })
                    }
                }
            )
            .addBindingParam(
                BR.adapterComments,
                StationCommentAdapter(baseContext).apply {
                    setOnItemClickListener { item, position ->
                        startActivity(Intent(baseContext, CommentDetailActivity::class.java).apply {
                            val comment = Comment(
                                appCommentsPk = item?.appCommentsPk,
                                commentDate = item?.commentDate,
                                content = item?.content,
                                delFlag = item?.delFlag,
                                rating = item?.rating,
                                check = item?.check,
                                avatarUrl = item?.avatarUrl,
                                appUserPk = item.appUserPk,
                                replyCounts = item.replyCounts,
                                userName = item.userName,
                                stationAvatarUrl = mStationItem?.stationAvatarUrl,
                                stationName = mStationItem?.stationName,
                                stationStreet = mStationItem?.street,
                            )
                            putExtra("comment", comment)
                            putExtra("type", 2)
                            putExtra("commentsReplyPk", item.appCommentsPk)
                        })
                    }
                    delClick = {
                        val b = it.hasDelFlag()
                        it.delFlag = b.toString()
                        notifyDataSetChanged()
                        delComment(it.appCommentsPk.toString())
                    }
                }
            )
            .addBindingParam(
                BR.info, mStationItem?.apply {
                    distance = baseContext.getDistanceShow(distance)
                }
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()

        appbar_layout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            refreshLayout.isEnabled = verticalOffset >= 0

            //垂直方向偏移量
            val offset = Math.abs(verticalOffset)
            //最大偏移距离
            val scrollRange = appBarLayout!!.totalScrollRange


            //根据百分比计算扫一扫布局透明值
            val scale = offset.toFloat() / scrollRange
            val alpha = (255 * scale).toInt()
            toolbar.setBackgroundColor(
                ColorUtil.getCurrentColor(
                    Color.TRANSPARENT,
                    Color.WHITE,
                    min(scale, 1f)
                )
            )
            title_nam.setTextColor(
                ColorUtil.getCurrentColor(
                    Color.TRANSPARENT,
                    Color.BLACK,
                    min(scale, 1f)
                )
            )

            if (offset > 800) ImmersionBar.with(this).statusBarDarkFont(true)
                .init() else ImmersionBar.with(this).statusBarDarkFont(false).init()

            ll_to_reply.visibility = if (offset >= 890) View.VISIBLE else {
                View.INVISIBLE
            }

            iv_menu1.visibility = if (offset >= 800) View.VISIBLE else View.GONE
            iv_search1.visibility = if (offset >= 800) View.VISIBLE else View.GONE


        })
        mStationItem?.rating?.apply {
            ratingBar.rating =
                if (this.isNotEmpty()) mStationItem?.ratingString()?.toFloat()!! else 0.0f
        }


        stationCommentReq.stationPk = mStationItem?.stationPk
        getData()
        getCommentData()
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            stationCommentReq.pageNum = 1
            commentPage = 1
            canLoadMore = true
            refreshing = true
            getCommentData()
            getData()
            refreshLayout.isRefreshing = false
        }

        list_comment.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                Log.e("list_comment", "$countItem $lastItem")
                if (canLoadMore) {
                    commentPage++
                    stationCommentReq.pageNum = commentPage
                    getCommentData()

                } else {
//                    ToastUtils.showShort("no more data")
                }
            }
        })
        iv_nav.setOnClickListener {
            mStationItem?.let { it1 ->
                this.jumpMap(it1)
            }
        }

        LiveBus.getInstance().observeEvent(this, Observer { (type) ->
            if (type == Configs.NOTIFICATION_MSG) {
                stationCommentReq.pageNum == 1
                getCommentData()
            }
        })

        tab_station.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.customView != null) {
                    val tab_text = tab.customView!!.findViewById<TextView>(R.id.tab_content_text)
                    tab_text.setTextColor(Utils.getApp().resources.getColor(R.color.colorTheme))
                    val imageView = tab.customView!!.findViewById<ImageView>(R.id.tab_content_text1)
                    imageView.setBackgroundResource(R.drawable.layer_tab_indicator)
                }
                if (tab.position == 2) {
                    view_liner1.visibility = View.VISIBLE
                } else {
                    view_liner1.visibility = View.GONE
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


        mStationItem?.apply {
            if (stationAvatarUrl.equals("")) return
            iv_bg.setImageIsWifi(stationAvatarUrl)
        }
        if (!mStationItem!!.stationAvatarUrl.isNullOrBlank()) {
            imageList.add(mStationItem!!.stationAvatarUrl!!)
            initBanner()
        }

    }

    var currentItem = 0

    inner class ClickProxy {
        fun back() {

            finish()
        }
        fun phone() {


            if (text_phone.text.toString() == ""|| TextUtils.isEmpty(text_phone.text)){
                return
            }
            XXPermissions.with(this@ChargeStationDetailActivity)
                .permission(Permission.CALL_PHONE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                        if (!all) {
                            ToastUtils.showShort("Obtain the right to make a call")
                            return
                        }
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text_phone.text)) //直接拨打电话
                        startActivity(dialIntent)
                    }

                    override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                        if (never) {
                            ToastUtils.showShort("Permanently denied authorization, please manually grant call rights")
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(this@ChargeStationDetailActivity, permissions)
                        } else {
                            ToastUtils.showShort("Failed to obtain the call permission. Procedure")
                        }
                    }
                })
        }

        fun love() {

            isStationCollect = if (isStationCollect) {
                collectionDel(mStationItem?.stationPk.toString(), -1)
                false
            } else {
                collectionAdd(mStationItem?.stationPk.toString(), -1)
                true
            }
            stationLike()
        }

        fun love1() {

            isStationCollect = if (isStationCollect) {
                collectionDel(mStationItem?.stationPk.toString(), -1)
                false
            } else {
                collectionAdd(mStationItem?.stationPk.toString(), -1)
                true
            }
            stationLike()
        }

        fun login() {

        }
    }

    fun stationLike() {
        if (isStationCollect) {
            iv_search.setImageResource(R.drawable.icon_love)
            iv_search1.setImageResource(R.drawable.icon_station_like)
        } else {
            iv_search.setImageResource(R.drawable.icon_unlove)
            iv_search1.setImageResource(R.drawable.icon_unlike)
        }
    }


    fun refresh() {
        stationCommentReq.pageNum = 1
        commentPage = 1
        canLoadMore = true
        getCommentData()
        getData()
        refreshLayout.isRefreshing = false
    }


    var isStationCollect = false
    fun getData() {
        object : NetworkBoundResource<StationDetailBean>(networkStatusCallback = object :
            NetworkStatusCallback<StationDetailBean> {

            override fun onSuccess(data: StationDetailBean?) {
                if (data != null) {
                    mStationViewModel.stationInfo.value = data
                    mStationViewModel.device.postValue(data.device)
                    var cNumber = 0
                    data.device?.apply {
                        forEach {
                            it.connectors?.apply {
                                cNumber += size
                            }
                        }

                    }
                    imageList.clear()
                    if (data.images != null) {
                        var imgs = data.images!!.split(",")
                        imageList.addAll(imgs)
                    } else {
                        if (mStationItem!!.stationAvatarUrl!! != "") {
                            imageList.add(mStationItem!!.stationAvatarUrl!!)
                        }
                    }
                    if (imageList.size>0) {
                        initBanner()
                    }
                    mStationViewModel.deviceInfoTitle.set("Connectors $cNumber")
                    mStationViewModel.stationInfoTitle.set("Device " + data.device?.size)
                    TabPageBindingAdapter.tabSelectedListener(
                        tab_station,
                        resources.getString(R.string.device) + "(" + data.device?.size!!.toString() + ")",
                        resources.getString(R.string.comments) + "(" + data.totalComment!!.toString() + ")",
                        "详情"
                    )
                    refreshing = false
                    view_pager.currentItem = currentItem
                    isStationCollect = data.isCollect
                    stationLike()
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }


            }

        }) {
            override fun loadFromNetData(): Observable<Resource<StationDetailBean>> {

                return SingletonFactory.apiService.stationDetail(
                    StationListBean(
                        stationPk = mStationItem?.stationPk,
                        minPower = minPower,
                        maxPower = maxPower,
                        latitude = getLat().toString(),
                        longitude = getLng().toString()
//                        status = status
                    )
                )
            }
        }
    }


    var allComments = ArrayList<Comment>()
    fun getCommentData() {
        object : NetworkBoundResource<List<Comment>>(networkStatusCallback = object :
            NetworkStatusCallback<List<Comment>> {

            override fun onSuccess(data: List<Comment>?) {
                if (data != null) {
                    if (stationCommentReq.pageNum == 1) {
                        allComments.clear()
                        allComments.addAll(data)
                    } else {
                        allComments.addAll(data)
                    }
                    mStationViewModel.listComments.value = allComments
                    if (allComments.size == 0) {
                        empty_view.visibility = View.VISIBLE
                    } else {
                        empty_view.visibility = View.GONE
                    }

                }

                canLoadMore = !data.isNullOrEmpty()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<Comment>>> {
                return SingletonFactory.apiService.getStationComment(stationCommentReq)
            }
        }
    }

    // position <0 就是收藏充电站
    fun collectionAdd(pk: String, position: Int) {
        refreshing = true
        val date = DateUtil.getNow()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
//                ToastUtils.showShort("收藏成功")
                refresh()
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
        refreshing = true
        val date = DateUtil.getNow()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                refresh()

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

    fun delComment(pk: String) {
        refreshing = true
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
//                ToastUtils.showShort("successfully")
                "success".toast()
                refresh()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delCOmment(
                    Comment(
                        appCommentsPk = pk,
                        check = null
                    )
                )
            }
        }
    }


    fun initBanner() {
        iv_bg.visibility = View.GONE
        val ada = object : BannerImageAdapter<String>(imageList) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: String,
                position: Int,
                size: Int
            ) { //图片加载自己实现
                Glide.with(App.appContext).load(data)
                    .into(holder.imageView)

            }
        }
        banner.isAutoLoop(true)
        banner.setLoopTime(3000)
        banner.addBannerLifecycleObserver(this).setAdapter(ada)
        banner.setIndicator(indicator1, false)


    }
}