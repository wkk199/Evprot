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
import com.google.gson.Gson
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Feedback
import com.evport.businessapp.data.bean.StationCommentReq
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.NotiFeedbackDetailActivity
import com.evport.businessapp.ui.page.adapter.NotificationFeedbackAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.ImageUrl
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.getIsWifiLoad
import com.evport.businessapp.utils.onLoadMoreListener
import com.previewlibrary.GPreviewBuilder
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_notification_feedback_list.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * Create by KunMinX at 19/10/29
 */
class NotiFeedbackListFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_notification_feedback_list, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, NotificationFeedbackAdapter(requireContext()).apply {
                contentClick = { item, position ->
                    /*  nav().navigate(R.id.action_global_notiFeedbackDetailFragment,
                          Bundle().also { b ->
                          b.putParcelable("feedback", item)
                      })*/

                    startActivity<NotiFeedbackDetailActivity>(Pair("feedback", Gson().toJson(item)))
                }
                delClick = { item, position ->
                    delFeedback(item?.feedbackPk.toString())
                }

                recyClick = { item, position ->
                    val list = item?.imageList()
                    val arrayList = ArrayList<ImageUrl>()
                    list?.forEach {
                        arrayList.add(ImageUrl(it))
                    }
                    showBigImg(arrayList, position)
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
        context?.getIsWifiLoad()
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            showLoading()
            pageNumber = 1
            canLoadMore = true
            getData()
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

    fun showBigImg(list: ArrayList<ImageUrl>, position: Int) {
        //打开预览界面
        GPreviewBuilder.from(this)
            .setData(list)
            .setCurrentIndex(position)
            .setSingleFling(true)
            .setType(GPreviewBuilder.IndicatorType.Number)
            // 小圆点
//  .setType(GPreviewBuilder.IndicatorType.Dot)
            .start()//启动
    }

    var canLoadMore = true
    var pageNumber = 1
    var allList = ArrayList<Feedback>()
    fun getData() {
        object : NetworkBoundResource<List<Feedback>>(networkStatusCallback = object :
            NetworkStatusCallback<List<Feedback>> {

            override fun onSuccess(data: List<Feedback>?) {
                dismissLoading()
                data?.apply {
                    if (pageNumber == 1) {
                        allList.clear()
                    }
                    allList.addAll(this)
                    mDrawerViewModel?.listNotiFeedback?.value = allList
                }
                if (allList.size == 0) {
                    empty_view.visibility = View.VISIBLE
                } else {
                    empty_view.visibility = View.GONE
                }
                canLoadMore = !data.isNullOrEmpty()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<Feedback>>> {
                return SingletonFactory.apiService.getNotiFeedback(
                    StationCommentReq(pageNum = pageNumber)
                )
            }
        }
    }


    fun delFeedback(pk: String) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                ToastUtils.showShort("删除成功")
                pageNumber = 1
                getData()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delNotiFeedback(StationCommentReq(feedbackPk = pk))
            }
        }
    }


    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }

}

