package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.AcceptingAdapter
import com.evport.businessapp.ui.state.AcceptingViewModel
import com.evport.businessapp.utils.onLoadMoreListener
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_notification_feedback_list.*
import kotlinx.android.synthetic.main.fragment_notification_feedback_list.selle_recycler_view

class AcceptingFragment : BaseFragment() {
    private var mViewModel: AcceptingViewModel? = null


    override fun initViewModel() {
        mViewModel = getFragmentViewModel(AcceptingViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_accepting, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            ).addBindingParam(BR.adapter, AcceptingAdapter(requireContext()).apply {
                moreClick = { item, position ->
                    mViewModel!!.invoiceDatas.value!![position].isSel =
                        mViewModel!!.invoiceDatas.value!![position].isSel != true
                    notifyDataSetChanged()
                }
            })
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                if (canLoadMore) {
                    pageNumber++
                    getData()
                }

            }
        })
    }

    var canLoadMore = true
    var pageNumber = 1
    var allList = ArrayList<InvoiceHistoryBean>()
    fun getData() {
        object :
            NetworkBoundResource<ArrayList<InvoiceHistoryBean>>(networkStatusCallback = object :
                NetworkStatusCallback<ArrayList<InvoiceHistoryBean>> {

                override fun onSuccess(data: ArrayList<InvoiceHistoryBean>?) {
                    dismissLoading()
                    canLoadMore = !data.isNullOrEmpty()
                    data?.apply {
                        if (pageNumber == 1) {
                            allList.clear()
                        }
                        allList.addAll(this)
                        if (data.isNullOrEmpty()&&allList.size!=0&&allList.size>10){
                            allList.add(InvoiceHistoryBean(isShowMore = true))
                        }else if (allList.size!=0&&allList.size<10&&canLoadMore){
                            allList.add(InvoiceHistoryBean(isShowMore = true))
                        }
                        mViewModel?.invoiceDatas?.value = allList
                    }
                    if (allList.size == 0) {
                        empty_view.visibility = View.VISIBLE
                    } else {
                        empty_view.visibility = View.GONE
                    }


                }

                override fun onFailure(message: String) {
                    if (!message.isNullOrBlank()) {
                        message.toast()
                    }

                }

            }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<InvoiceHistoryBean>>> {
                return SingletonFactory.apiService.getCreatedInvoiceHistoryList(
                    InvoiceHistoryVo(
                        pageNumber = pageNumber,
                        status = 0
                    )
                )
            }
        }
    }
}