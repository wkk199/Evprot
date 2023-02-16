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
import com.evport.businessapp.data.bean.ReplyDetail
import com.evport.businessapp.data.bean.StationCommentReq
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.CommentDetailActivity
import com.evport.businessapp.ui.page.activity.CommentReplyDetailActivity
import com.evport.businessapp.ui.page.adapter.NotificationReplyAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.onLoadMoreListener
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_notification_reply_list.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Create by KunMinX at 19/10/29
 */
class NotiReplyListFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_notification_reply_list, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, NotificationReplyAdapter(requireContext()).apply {
                delClick = { item, position ->
                    showLoading()
                    delReply(item?.commentsReplyPk.toString())
                }
                contentClick = { item, position ->
                    if(item?.commentsReplyRootPk.isNullOrBlank()){
                        val comment = item?.rootComment
                        startActivity<CommentDetailActivity>(Pair("comment",comment), Pair("commentsReplyPk", item!!.commentsReplyPk))
                    }else {
                        val comment = Comment(
//                            commentsPk = item?.rootComment?.appCommentsPk,
                            commentsReplyRootPk = item?.commentsReplyRootPk
//                            appCommentsPk = item?.rootComment?.appCommentsPk,
//                            commentsReplyPk = item?.commentsReplyPk
                        )
                        startActivity<CommentReplyDetailActivity>(Pair("comment", comment),   Pair("commentsReplyPk", item!!.commentsReplyPk))
                    }
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
        showLoading()
        getData()

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
    fun refresh(){

        pageNumber = 1
        canLoadMore = true
        getData()
        showLoading()
        refreshLayout.isRefreshing = false
    }

    var canLoadMore = true
    var pageNumber = 1
    var allList = ArrayList<ReplyDetail>()
    fun getData() {
        object : NetworkBoundResource<List<ReplyDetail>>(networkStatusCallback = object :
            NetworkStatusCallback<List<ReplyDetail>> {

            override fun onSuccess(data: List<ReplyDetail>?) {
                dismissLoading()
                data?.apply {
                    if (pageNumber == 1) {
                        allList.clear()
                    }
                    allList.addAll(this)
                    mDrawerViewModel?.listNotiReply?.value = allList
                }
                if (allList.size==0){
                    empty_view.visibility=View.VISIBLE
                }else{
                    empty_view.visibility=View.GONE
                }
                canLoadMore = !data.isNullOrEmpty()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<ReplyDetail>>> {
                return SingletonFactory.apiService.getNotiReply(
                    StationCommentReq(pageNum = pageNumber)
                )
            }
        }
    }

    fun delReply(pk: String) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
               refresh()
                ToastUtils.showShort(getString(R.string.success))

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delReply(Comment(commentsReplyPk = pk, check = null))
            }
        }
    }


    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }
}