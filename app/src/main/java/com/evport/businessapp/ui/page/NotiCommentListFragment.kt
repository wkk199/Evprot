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
import com.evport.businessapp.data.bean.Comment
import com.evport.businessapp.data.bean.StationCommentReq
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.CommentDetailActivity
import com.evport.businessapp.ui.page.adapter.NotificationCommentAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.onLoadMoreListener
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_notification_comment_list.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Create by KunMinX at 19/10/29
 */
class NotiCommentListFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_notification_comment_list, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, NotificationCommentAdapter(requireContext()).apply {
                delClick = { item, position ->
                    item?.appCommentsPk?.let {
                        delComment(it)
                    }!!
                }
                contentClick =  { item, position ->
                    startActivity<CommentDetailActivity>(Pair("comment",item),
                    Pair("appCommentsPk", item!!.appCommentsPk))
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

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }

    public override fun loadInitData() {
        super.loadInitData()

        showLoading()
        getData()

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

                pageNumber++
                getData()

            }
        })


    }
    fun  refresh(){

        showLoading()
        pageNumber = 1
        canLoadMore = true
        getData()
        refreshLayout.isRefreshing = false
    }

    var canLoadMore = true
    var pageNumber = 1
    var allList = ArrayList<Comment>()
    fun getData() {
        object : NetworkBoundResource<List<Comment>>(networkStatusCallback = object :
            NetworkStatusCallback<List<Comment>> {

            override fun onSuccess(data: List<Comment>?) {
                dismissLoading()
                data?.apply {
                    if (pageNumber == 1) {
                        allList.clear()
                    }
                    allList.addAll(this)
                    mDrawerViewModel?.listNotiComment?.value = allList
                }
                canLoadMore = !data.isNullOrEmpty()
                empty_view.visibility = if (allList.isNullOrEmpty()) View.VISIBLE else View.GONE

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<Comment>>> {
                return SingletonFactory.apiService.getNotiComments(
                    StationCommentReq(pageNum = pageNumber)
                )
            }
        }
    }

    fun delComment(pk: String) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
               ToastUtils.showShort(getString(R.string.success))
                refresh()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showShort(message)
                }


            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delCOmment(Comment(appCommentsPk = pk, check = null))
            }
        }
    }

}