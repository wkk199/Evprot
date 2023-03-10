package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Inputtips.InputtipsListener
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.evport.businessapp.App
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.PoiAdapter
import com.evport.businessapp.ui.page.adapter.PoiHistoryAdapter
import com.evport.businessapp.ui.state.SearchMapViewModel
import com.evport.businessapp.utils.ACache
import kotlinx.android.synthetic.main.activity_search_map.*


class SearchMapActivity : BaseActivity(), InputtipsListener, PoiSearch.OnPoiSearchListener {
    private var mViewModel: SearchMapViewModel? = null
    var aCache: ACache? = null

    lateinit var mAdapter: PoiAdapter
    lateinit var mAdapterHistory: PoiHistoryAdapter
    var historyList = arrayListOf<PoiItem>()

    override fun initViewModel() {
        mViewModel = getActivityViewModel(SearchMapViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        mAdapter = PoiAdapter(this).apply {
            setOnItemClickListener { item, position ->
                Log.e("hm---", Gson().toJson(item))
                Log.e("hm---", item.poiId)
                var intent = Intent()
                intent.putExtra("lat", item.latLonPoint.latitude.toString())
                intent.putExtra("lng", item.latLonPoint.longitude.toString())
                intent.putExtra("name", item.cityName)
                for (i in 0 until historyList.size) {
                    if (historyList[i].cityName.isNullOrBlank()) {
                        historyList.removeAt(i)
                    }
                }
                var count = 0;
                historyList.forEach {
                    if (it.poiId.equals(item.poiId)) {
                        count++
                    }
                }
                if (count == 0) {
                    historyList.add(0, item)
                    if (historyList.size > 10) {
                        historyList.removeAt(10)
                    }
                    aCache!!.put("history", Gson().toJson(historyList))
                }

                this@SearchMapActivity.setResult(100, intent)
                this@SearchMapActivity.finish()
            }
        }
        mAdapterHistory = PoiHistoryAdapter(this).apply {
            setOnItemClickListener { item, position ->
                if (position < mAdapterHistory.currentList.size - 1) {
                    var intent = Intent()
                    intent.putExtra("lat", item.latLonPoint.latitude.toString())
                    intent.putExtra("lng", item.latLonPoint.longitude.toString())
                    intent.putExtra("name", item.cityName)
                    this@SearchMapActivity.setResult(100, intent)
                    this@SearchMapActivity.finish()
                } else {
                    historyList.clear()
                    mViewModel!!.listHistory.value = historyList
                    aCache!!.put("history", "")
                    empty_rl.visibility=View.VISIBLE
                    history_empty.visibility=View.VISIBLE
                    search_empty.visibility=View.GONE

                }
            }
        }
        return DataBindingConfig(R.layout.activity_search_map, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            ).addBindingParam(BR.adapter, mAdapter)
            .addBindingParam(BR.adapterHistory, mAdapterHistory)
    }

    inner class ClickProxy {
        fun back() {
            finish()
        }
        fun close(){
            search_edit_text.setText("")
            recyclerView.visibility = View.GONE
            recyclerView_history.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //??????
//        NetworkManager.getInstance().registerObserver(this);
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .statusBarColor(R.color.white)     //???????????????????????????????????????
            .statusBarDarkFont(true)
            .keyboardEnable(false)
            .init()
        aCache = ACache.get(this)
        initView()
        initHistory()
        showKeyWord()

        search_edit_text.isFocusable = true;
        search_edit_text.isFocusableInTouchMode = true;
        search_edit_text.requestFocus();
        search_edit_text.requestFocusFromTouch();
    }

    private fun initHistory() {
        var historyContent = aCache!!.getAsString("history")
        if (!historyContent.isNullOrBlank()) {
            val listType = object : TypeToken<ArrayList<PoiItem>>() {}.type
            historyList = Gson().fromJson(historyContent, listType)
            historyList.add(PoiItem("", null, null, null))
            mViewModel!!.listHistory.value = historyList
            empty_rl.visibility = View.GONE

        } else {
            empty_rl.visibility = View.VISIBLE
        }

    }

    fun initView() {
        search_edit_text.doOnTextChanged { charSequence, start, _, _ ->
            // ??????EditText????????????
            var keyWord = search_edit_text.text.toString()
            /*val inputquery = InputtipsQuery(keyWord, "")
            inputquery.cityLimit = true

            val inputTips = Inputtips(this, inputquery)
            inputTips.setInputtipsListener(this)
            inputTips.requestInputtipsAsyn()*/
            if (!keyWord.isNullOrBlank()) {
                initSearch(keyWord)
                close_iv.visibility=View.VISIBLE
            } else {
                close_iv.visibility=View.GONE
                var list = arrayListOf<PoiItem>()
                mViewModel!!.listOne.value = list
                recyclerView.visibility = View.VISIBLE
                recyclerView_history.visibility = View.GONE
                empty_rl.visibility = View.GONE
            }


        }
    }

    fun initSearch(keyWord: String) {
        mQuery = PoiSearch.Query(
            keyWord,
            "190000",
            ""
        ) // ????????????????????????????????????????????????????????????poi????????????????????????????????????poi??????????????????????????????????????????

        mQuery!!.isDistanceSort = true //???????????????????????????

        mQuery!!.cityLimit = true
        mQuery!!.pageSize = 30 // ?????????????????????????????????poiitem


        mQuery!!.pageNum = currentPage // ??????????????????
        var mPoiSearch = PoiSearch(App.appContext, mQuery)
        mPoiSearch.setOnPoiSearchListener(this)
        mPoiSearch.searchPOIAsyn()

    }

    var allList = arrayListOf<Tip>()
    override fun onGetInputtips(tipList: MutableList<Tip>?, rCode: Int) {

        if (rCode == AMapException.CODE_AMAP_SUCCESS) {

            if (tipList != null) {
                allList.clear()

                for (i in 0 until tipList.size) {
                    val tip = tipList[i]
                    if (tip?.point != null) {
                        allList.add(tip)
                    }
                }
                //   mViewModel!!.listOne.value = allList
            }
        } else {
            ToastUtils.showShort(rCode)
        }
    }

    private var mQuery // Poi???????????????
            : PoiSearch.Query? = null
    private var poiResult // poi???????????????
            : PoiResult? = null
    private val currentPage = 0 // ??????????????????0????????????


    private var poiItems: List<PoiItem>? = null// poi??????
    private val poiSearch: PoiSearch? = null

    override fun onPoiSearched(result: PoiResult?, rcode: Int) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result?.query != null) {// ??????poi?????????
                if (result.query == mQuery) {// ??????????????????
                    // ??????????????????
                    poiResult = result
                    poiItems = poiResult!!.getPois() // ??????????????????poiitem????????????????????????0??????
                    val suggestionCities =
                        poiResult!!.searchSuggestionCitys // ???????????????poiitem?????????????????????????????????????????????????????????
                    mAdapter.contentS=search_edit_text.text.toString()
                    if (poiResult!!.pois.size > 0) {
                        mViewModel!!.listOne.value = poiItems
                        recyclerView.visibility = View.VISIBLE
                        recyclerView_history.visibility = View.GONE
                        empty_rl.visibility = View.GONE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        recyclerView_history.visibility = View.GONE
                        empty_rl.visibility = View.VISIBLE
                        history_empty.visibility = View.GONE
                        search_empty.visibility = View.VISIBLE
                    }

                }
            }
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
//??????
//        NetworkManager.getInstance().unRegisterObserver(this);
    }

//    @NetWork(netType = NetType.AUTO)
//    fun network(netType: NetType?) {
//        when (netType) {
//            NetType.WIFI ->{
//                Log.e("hmm----", "wifi")
//                empty_network.visibility=View.GONE
//            }
//            NetType.CMNET, NetType.CMWAP -> {
//                Log.e("hmm----", "4G")
//                empty_network.visibility=View.GONE
//            }
//            NetType.AUTO -> {}
//            NetType.NONE -> {
//                Log.e("hmm----", "?????????")
//                empty_network.visibility=View.VISIBLE
//            }
//            else -> {}
//        }
//    }


}