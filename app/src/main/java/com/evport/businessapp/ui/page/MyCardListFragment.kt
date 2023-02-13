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
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.MyCard
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.MyCardAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_my_card_list.*

/**
 * Create by KunMinX at 19/10/29
 */
class MyCardListFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_my_card_list, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, MyCardAdapter(requireContext()).apply {

                contentClick =  { item, position ->
//                    startActivity<CommentDetailActivity>(Pair("comment",item))
                }
            })
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }

    public override fun loadInitData() {
        super.loadInitData()
        getData()

        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            pageNumber = 1
            canLoadMore = true
            getData()
            refreshLayout.isRefreshing = false
        }
//
//        selle_recycler_view.addOnScrollListener(object : onLoadMoreListener() {
//            override fun onLoading(countItem: Int, lastItem: Int) {
//
//                pageNumber++
//                getData()
//
//            }
//        })


    }

    var canLoadMore = true
    var pageNumber = 1
    var allList = ArrayList<MyCard>()
    fun getData() {
        object : NetworkBoundResource<List<MyCard>>(networkStatusCallback = object :
            NetworkStatusCallback<List<MyCard>> {

            override fun onSuccess(data: List<MyCard>?) {
                data?.apply {
                    if (pageNumber == 1) {
                        allList.clear()
                    }
                    allList.addAll(this)
                    mDrawerViewModel?.listMyCard?.value = allList
                }
                canLoadMore = !data.isNullOrEmpty()
                empty_view.visibility = if (allList.isNullOrEmpty()) View.VISIBLE else View.GONE

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    ToastUtils.showLong(message)
                }
                empty_view.visibility = if (allList.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<MyCard>>> {
                return SingletonFactory.apiService.getCard()
            }
        }
    }

}