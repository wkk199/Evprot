package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.InvoicListBean
import com.evport.businessapp.data.bean.InvoiceVo
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.InvoicingListAdapter
import com.evport.businessapp.ui.state.InvoicingViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.onLoadMoreListener
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoicing.*
import java.math.BigDecimal


class InvoicingFragment : BaseFragment() {
    private var mViewModel: InvoicingViewModel? = null
    var pageNumber = 1
    var isLoadedAllPage = false
    var selectType = SelectStateType.NONE

    enum class SelectStateType {
        NONE,
        PART,
        ALL,
    }

    private lateinit var adapter: InvoicingListAdapter
    var allList = ArrayList<InvoicListBean>()
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(InvoicingViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = InvoicingListAdapter(requireContext()).apply {
            selClick = { item, position ->
                mViewModel!!.invoiceDatas.value!![position].isSel =
                    mViewModel!!.invoiceDatas.value!![position].isSel != true
                notifyDataSetChanged()
                switchBtnAll()
            }

        }
        return DataBindingConfig(R.layout.fragment_invoicing, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            ).addBindingParam(BR.adapter, adapter)
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun next() {
            val checkList = allList.filter { it.isSel == true }
            if (checkList.isEmpty()) {
                ToastUtils.showShort("请选择开票订单")
                return
            }
            var counMoney = BigDecimal("0.0")
            checkList.forEach {
                counMoney = counMoney.add(it.amount!!)
            }
            if (counMoney.toDouble() <= 0) {
                ToastUtils.showShort("开票金额必须大于0")
                return
            }
            if (counMoney.toDouble() >=1000) {
                ToastUtils.showShort("开票金额不能大于1000")
                return
            }
            nav().navigate(R.id.action_global_InvoicingDetailsFragment, Bundle().apply
            {
                putString("record", GsonUtils.toJson(checkList))
                putSerializable("type", selectType)
            })

        }

        fun history() {
            nav().navigate(R.id.action_global_billingHistoryFragment)
        }

        fun selNowPage() {
            if (selectType == SelectStateType.PART) {
                selectType = SelectStateType.NONE
                unSelALlToData()
            } else {
                selectType = SelectStateType.PART
                selALlToData()

            }
            switchBtnAll()
        }

        fun allSel() {
            if (selectType == SelectStateType.ALL) {
                selectType = SelectStateType.NONE
                unSelALlToData()
                switchBtnAll()
            } else {
                selectType = SelectStateType.ALL
                // showLoading()
                pageNumber = 0
                canLoadMore = true
                isLoadedAllPage = false
                getData()

            }

        }

    }

    fun unSelALlToData() {
        mViewModel!!.invoiceDatas.value!!.forEach {
            it.isSel = false
        }

        adapter.notifyDataSetChanged()
    }

    fun switchBtnAll() {

        val checkList = allList.filter { it.isSel == true }
        mViewModel!!.count.value = checkList.size.toString()
        var counMoney = BigDecimal("0.0")
        checkList.forEach {
            counMoney = counMoney.add(it.amount!!)
        }
        mViewModel!!.countMoney.value = counMoney.setScale(2, BigDecimal.ROUND_UP).toString()
        if (selectType == SelectStateType.PART && checkList.size == allList.size) {
            sel_now_page.setImageResource(R.drawable.icon_invoicing_sel)
        } else {
            if (isLoadedAllPage && checkList.size == allList.size) {
                sel_now_page.setImageResource(R.drawable.icon_invoicing_sel)
            } else {
                sel_now_page.setImageResource(R.drawable.icon_invoicing_unsel)
            }

        }
        if (selectType == SelectStateType.ALL) {
            if (checkList.size == allList.size) {
                all_sel.setImageResource(R.drawable.icon_invoicing_sel)
            } else {
                all_sel.setImageResource(R.drawable.icon_invoicing_en)
            }
        } else {
            if (isLoadedAllPage && checkList.size == allList.size) {
                all_sel.setImageResource(R.drawable.icon_invoicing_sel)
            } else {
                all_sel.setImageResource(R.drawable.icon_invoicing_unsel)
            }
        }
    }


    fun selALlToData() {
        mViewModel!!.invoiceDatas.value!!.forEach {
            it.isSel = true
        }
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        initObserve()
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            // showLoading()
            pageNumber = 1
            canLoadMore = true
            isLoadedAllPage = false
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

    private fun initObserve() {
        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == Configs.INVOICUNG_REFRESH||it.type==Configs.INVOICUNG_HISTORY) {
                //  showLoading()
                pageNumber = 1
                canLoadMore = true
                isLoadedAllPage = false
                getData()
            }
        })
    }

    var canLoadMore = true
    private fun getData() {
        object : NetworkBoundResource<ArrayList<InvoicListBean>>(networkStatusCallback = object :
            NetworkStatusCallback<ArrayList<InvoicListBean>> {

            override fun onSuccess(data: ArrayList<InvoicListBean>?) {
                //  dismissLoading()
                data?.apply {

                    if (pageNumber == 1 || pageNumber == 0) {
                        allList.clear()
                    }
                    allList.addAll(this)
                    mViewModel?.invoiceDatas?.value = allList
                }


                if (allList.size == 0) {
                    empty_view.visibility = View.VISIBLE
                    bottom_ll.visibility=View.INVISIBLE
                } else {
                    empty_view.visibility = View.GONE
                    bottom_ll.visibility=View.VISIBLE
                }
                canLoadMore = !data.isNullOrEmpty()
                isLoadedAllPage = data.isNullOrEmpty()
                if (pageNumber == 0) {
                    isLoadedAllPage = true
                    selALlToData()
                    switchBtnAll()
                } else if (pageNumber == 1) {
                    selectType = SelectStateType.NONE
                    isLoadedAllPage = false
                    mViewModel!!.count.value = "0"
                    mViewModel!!.countMoney.value = "0.00"
                    sel_now_page.setImageResource(R.drawable.icon_invoicing_unsel)
                    all_sel.setImageResource(R.drawable.icon_invoicing_unsel)
                }
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<InvoicListBean>>> {
                return SingletonFactory.apiService.getInvoiceAbleList(InvoiceVo(pageNumber = pageNumber))
            }
        }
    }

}