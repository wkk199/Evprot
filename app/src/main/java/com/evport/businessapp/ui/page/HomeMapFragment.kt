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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.kunminx.architecture.domain.manager.NetState
import com.kunminx.architecture.ui.callback.EventObserver
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseLocationFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.ChargeStationDetailActivity
import com.evport.businessapp.ui.page.activity.PlacesMapsLatngActivity
import com.evport.businessapp.ui.page.activity.UserCollectActivity
import com.evport.businessapp.ui.page.adapter.ChargeGunListAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.ui.state.StatsViewModel
import com.evport.businessapp.utils.*
import com.evport.businessapp.widget.PopFilterPicker
import com.google.android.gms.location.LocationListener
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home_map.*
import org.jetbrains.anko.support.v4.startActivity

/**
 *
 *
 */

const val AUTOCOMPLETE_REQUEST_CODE = 101
const val STATION_ITEM = "STATION_ITEM"
const val MIN_POWER = "MIN_POWER"
const val MAX_POWER = "MAX_POWER"
const val STATUS = "STATUS"

class HomeMapFragment : BaseLocationFragment(), OnMapReadyCallback, LocationListener {
    private val DEFAULT_ZOOM = 13f
    private var statsViewModel: StatsViewModel? = null
    private var mDrawerViewModel: DrawerViewModel? = null
    private var points = ArrayList<ChargeSetting>()
    var selectPoints = 0;
    var selectPointsPk = ArrayList<String>()
    var selectPointsB = ArrayList<Boolean>()
    var ek = mutableListOf<StatsData>()
    private var currentMarker: Marker? = null
    private var currentSearchPlace: LatLng? = null
    var currentPosition = -1
    private var mMap: GoogleMap? = null
    private var isRequest: Boolean = false
    lateinit var adapter: ChargeGunListAdapter
    lateinit var adapterOne: ChargeGunListAdapter
    private var mHomeSearch = HomeSearch(pageNum = 1)
//    val placesClient = Places.createClient(requireContext())

    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)

    var lat = 0.00
    var lng = 0.00
    override fun onLocationGet(location: Location) {

        Log.e("onLocationGet", "onLocationGet")
        if (mMap == null)
            return
        mMap?.let {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            it.isMyLocationEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = true
            it.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), DEFAULT_ZOOM
                )
            )
            mMap?.moveCamera(
                CameraUpdateFactory.zoomTo(14f)
            )
            lat = location.latitude
            lng = location.longitude


            context?.saveLat(location.latitude.toFloat())
            context?.saveLng(location.longitude.toFloat())
            Log.e("location:", "la " + location.latitude + "  ln  " + location.longitude)
            mHomeSearch.latitude = location.latitude
            mHomeSearch.longitude = location.longitude
            mHomeSearch.pageNum = 1
            getData()



            if (!isRequest) {
                isRequest = true
                if (arguments == null) {
//                    requestMosqueByDistrcs(EventType.NET_MOSQUES_BY_DISTRCS)
//                    requestMosqueByLng(EventType.NET_GET_MOSQUE)
                } else {
                    val kids: String? = arguments?.getString("kids")
                    val adults: String? = arguments?.getString("adults")
                    if (kids == null && adults == null) {
//                        requestMosqueByLng(EventType.NET_GET_MOSQUE)
//                        requestMosqueByDistrcs(EventType.NET_MOSQUES_BY_DISTRCS)
                    } else {
                        var kid: Int? = null
                        var adult: Int? = null
                        if (kids != null) {
                            try {

                                kid = kids.toInt()
                            } catch (e: NumberFormatException) {
                                ToastUtils.showShort("deliver param error")
                                return
                            }
                        }
                        if (adults != null) {
                            try {
                                adult = adults.toInt()
                            } catch (e: NumberFormatException) {
                                ToastUtils.showShort("deliver param error")
                                return
                            }
                        }
//                        requestMosqueByKids(EventType.NET_GET_MOSQUE, kid, adult)
//                        requestMosqueBydistricsByKids(EventType.NET_MOSQUES_BY_DISTRCS, kid, adult)
                    }

                }
            }

        }
    }

    val SEARCHTAG = "search_tag"

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e("onMapReady", "onMapReady")
        statsViewModel?.listOne?.value = arrayListOf<StationListBean>()
        mMap = googleMap
        mMap?.setOnMarkerClickListener { it ->

            if (it.tag == SEARCHTAG) {
                return@setOnMarkerClickListener false
            }
            try {
                if (currentMarker != null) {
                    currentMarker?.hideInfoWindow()
                    statsViewModel?.isItemShow?.set(false)
                    setMapsOnclickLogoShow()
//                    val b = BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker)
                    currentPosition = -1
                }
            } catch (e: Exception) {

            }
//            it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mark_select))
            it.showInfoWindow()
            currentMarker = it
            allList.apply {
                this.forEachIndexed { index, gunlist ->


                    if (gunlist.stationPk == currentMarker?.tag) {
                        val list = arrayListOf<StationListBean>()
                        currentPosition = index
                        list.add(gunlist)
                        statsViewModel?.listOne?.value = list
                        val statsData =
                            gunlist.connectorStatus?.filter { its -> its.status == "Charging" }
                        val statsIdle =
                            gunlist.connectorStatus?.filter { its -> its.status == "Idle" }
                        if (statsData!!.isNotEmpty()) {
                            it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mark_select))
                        } else {
                            if (statsIdle!!.isNotEmpty()) {
                                it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_ldle_ok))
                            } else {
                                it.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_offline_ok))
                            }
                        }
                    }
                }
            }
            statsViewModel?.isItemShow?.set(true)
            false
        }
        mMap?.setOnMapClickListener {
            statsViewModel?.listOne?.value = arrayListOf<StationListBean>()
            statsViewModel?.isItemShow?.set(true)
            try {
                if (currentMarker != null) {
                    setMapsOnclickLogoShow()
//                currentMarker?.hideInfoWindow()
                    currentMarker = null
                }
            } catch (e: Exception) {

            }


        }

    }

    override fun initViewModel() {
        statsViewModel = getFragmentViewModel(StatsViewModel::class.java)
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig { //TODO tip: DataBinding 严格模式：

        adapter = ChargeGunListAdapter(requireContext()).apply {
            setOnItemClickListener { item, position ->
                if (NoFastClickUtils.isFastClick())
                startActivity(Intent(activity, ChargeStationDetailActivity::class.java).apply {
                    putExtra(STATION_ITEM, item)
                        .putExtra(MIN_POWER, mHomeSearch.minPower)
                        .putExtra(MAX_POWER, mHomeSearch.maxPower)
                        .putExtra(STATUS, mHomeSearch.status)
                })
            }
        }
        adapterOne = ChargeGunListAdapter(requireContext()).apply {
            setOnItemClickListener { item, position ->
                if (NoFastClickUtils.isFastClick())
                startActivity(
                    Intent(activity, ChargeStationDetailActivity::class.java).apply {
                        putExtra(STATION_ITEM, item)
                            .putExtra(MIN_POWER, mHomeSearch.minPower)
                            .putExtra(MAX_POWER, mHomeSearch.maxPower)
                            .putExtra(STATUS, mHomeSearch.status)
                    }
                )
            }
        }
        return DataBindingConfig(R.layout.fragment_home_map, statsViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, adapter)
            .addBindingParam(BR.adapterOne, adapterOne)
    }

    //  设置请求数据
    fun initSearch() {
        val f = context?.getHomeFilterData()
        f?.apply {
            mHomeSearch.minPower = minPower
            mHomeSearch.maxPower = maxPower
            mHomeSearch.status = if (statusIsSelected) "Idle" else null
            mHomeSearch.pageNum = 1
//            mHomeSearch.operatorPk = operators.map { it.operatorPk }
            mHomeSearch.socket = operators.map { it.operatorPk }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//
        initSearch()
        getData()

//        初始化地图
        val mapFragment = childFragmentManager.findFragmentById(R.id.fl_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sharedViewModel.mapRefresh.observe(this, EventObserver {
            if (it) {
                ImmersionBar.with(this).statusBarDarkFont(false).init()
                mapFragment.getMapAsync(this)
                refreshData()
            }
        })
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            canLoadMore = true
            showLoading()
            refreshData()
            mapFragment.getMapAsync(this)
        }
        rv_stationlist.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                if (canLoadMore) {
                    mHomeSearch.pageNum++
                    getData()
                }
            }

        })

        refreshLayout.isEnabled = statsViewModel?.isListShow?.get()!!



        search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    close.visibility = View.VISIBLE
                } else {
                    close.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })



    }


    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }


    inner class ClickProxy {

        fun searchClick() {
            statsViewModel?.listOne?.value = arrayListOf<StationListBean>()
            statsViewModel?.isItemShow?.set(true)
            currentPosition = -1
            try {
                if (currentMarker != null) {
                    setMapsOnclickLogoShow()
//                currentMarker?.hideInfoWindow()
                    currentMarker = null
                }
            } catch (e: Exception) {

            }
//            // Use fields to define the data types to return.
//            val placeFields = Arrays.asList(Place.Field.NAME);
//
//            // Use the builder to create a FindCurrentPlaceRequest.
//            val request =
//                FindCurrentPlaceRequest.builder(placeFields).build();

            // Start the autocomplete intent.
//            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                .build(requireContext())
////            val intent1 = Auto.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
////                .build(requireContext())
//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            var intent = Intent(
                this@HomeMapFragment.context,
                PlacesMapsLatngActivity::class.java
            )
            startActivityForResult(intent, 100)

        }

        fun showList() {
            statsViewModel?.isListShow?.set(!statsViewModel?.isListShow?.get()!!)
            refreshLayout.isEnabled = statsViewModel?.isListShow?.get()!!
        }

        fun zoomIn() {
            var lat = 0.0f
            var lng = 0.0f
            lat = activity!!.getLat()
            lng = activity!!.getLng()
            mMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        lat.toDouble(),
                        lng.toDouble()
                    ), DEFAULT_ZOOM
                )
            )
            mMap?.moveCamera(
                CameraUpdateFactory.zoomTo(14f)
            )

        }

        fun favorities() {
            startActivity<UserCollectActivity>()
        }

        fun nearby() {


//            val list = context?.getDistanceList()?.toTypedArray()
//            AlertDialog.Builder(requireContext())
////                .setTitle("choose ")
//                .setItems(
//                    list
//                ) { p0, p1 ->
//
//                    selectPoints = p1
//                    statsViewModel?.range?.set(list?.get(p1).toString())
//                    mHomeSearch.distance = context?.getDistanceString(p1)
//
//                    val homeFilter = requireContext().getHomeFilterData() ?: HomeFilter()
//
//
////                    homeFilter.apply {
////                        distance = context?.getDistanceString(p1).toString()
////                    }
//                    requireContext().saveHomeFilterData(homeFilter)
//                    getData()
//
//                }
//                .create()
//                .show()


        }

        fun close() {
            search_edit_text.text = ""
            if (lat != 0.00) {
                mMap!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lat,lng
                        ), 14f
                    )
                )
            }
            mHomeSearch.latitude = lat
            mHomeSearch.longitude = lng
            mHomeSearch.pageNum = 1
            getData()

        }

        fun filter() {
            statsViewModel?.listOne?.value = arrayListOf<StationListBean>()
            statsViewModel?.isItemShow?.set(true)
            currentPosition = -1
            try {
                if (currentMarker != null) {
                    setMapsOnclickLogoShow()
//                currentMarker?.hideInfoWindow()
                    currentMarker = null
                }
            } catch (e: Exception) {

            }

            XPopup.Builder(requireContext())
                .asCustom(PopFilterPicker(requireContext()).apply {
                    setCallBack {

                        statsViewModel?.listOne?.value = arrayListOf<StationListBean>()
                        statsViewModel?.isItemShow?.set(true)
                        currentPosition = -1
                        try {
                            if (currentMarker != null) {
//                                setMapsOnclickLogoShow()
//                currentMarker?.hideInfoWindow()
                                currentMarker = null
                            }
                        } catch (e: Exception) {

                        }
                        initSearch()
                        refreshData()
                    }
                })
                .show()


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null) {
            val name = data.getStringExtra("name")
            val latitude = data.getStringExtra("latitude")
            val longitude = data.getStringExtra("longitude")

//            search_edit_text.text = name
//
//            val latLng =LatLng(latitude!!.toDouble(),longitude!!.toDouble())
//            mMap?.moveCamera(
//                CameraUpdateFactory
//                    .newLatLngZoom(latLng, DEFAULT_ZOOM)
//            )
            if (!latitude.isNullOrBlank()) {
                currentSearchPlace = LatLng(latitude.toDouble(),longitude!!.toDouble())
                search_edit_text.text = name
                mMap!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(latitude.toDouble(),longitude.toDouble()
                        ), 14f
                    )
                )

                mHomeSearch.latitude = latitude.toDouble()
                mHomeSearch.longitude = longitude.toDouble()
                mHomeSearch.pageNum = 1
                getData()
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    data?.let {
//                        val place = Autocomplete.getPlaceFromIntent(data)
//                        search_edit_text.text = place.name
//                        currentSearchPlace = place
//                        mMap?.moveCamera(
//                            CameraUpdateFactory
//                                .newLatLngZoom(place.latLng!!, DEFAULT_ZOOM)
//                        )
//                        Log.i(
//                            "onActivityResult",
//                            "Place: ${place.name}, ${place.id}, ${place.latLng}"
//                        )
//                    }
//                }
//                AutocompleteActivity.RESULT_ERROR -> {
//                    // TODO: Handle the error.
//                    data?.let {
//                        val status = Autocomplete.getStatusFromIntent(data)
//                        Log.i("onActivityResult", status.statusMessage.toString())
//                    }
//                }
//                Activity.RESULT_CANCELED -> {
//                    // The user canceled the operation.
//                }
//            }
//            return
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }

    private var allList = ArrayList<StationListBean>()
    var canLoadMore = true

    fun refreshData() {
        mHomeSearch.pageNum = 1
        canLoadMore = false
        getData()

    }

    fun getData() {
        object : NetworkBoundResource<List<StationListBean>>(networkStatusCallback = object :
            NetworkStatusCallback<List<StationListBean>> {

            override fun onSuccess(data: List<StationListBean>?) {
                if (data != null) {
                    if (mHomeSearch.pageNum == 1) {
                        allList.clear()
                    }
//                    重置数据
                    allList.addAll(data)
                    statsViewModel?.gunListBean?.value = allList
                    mMap?.clear()
                    if(allList.size>0){
                        empty_view.visibility=View.GONE
                    }else{
                        empty_view.visibility=View.VISIBLE
                    }
                    allList.forEachIndexed { index, stationListBean ->

                        val statsData =
                            stationListBean.connectorStatus?.filter { it.status == "Charging" }
                        val statsIdle =
                            stationListBean.connectorStatus?.filter { it.status == "Idle" }
                        val bitmap =
                            if (index == currentPosition) {
                                setMapsLogoShow(statsData, statsIdle)
                            } else {
                                setMapsLogoDlss(statsData, statsIdle)
                            }
                        val markOption = MarkerOptions().position(
                            LatLng(
                                stationListBean.latitude?.toDouble()!!,
                                stationListBean.longitude?.toDouble()!!
                            )
                        ).icon(BitmapDescriptorFactory.fromBitmap(bitmap))


                        val marker = mMap?.addMarker(markOption)
                        marker?.tag = stationListBean.stationPk
                        if (index == currentPosition && currentMarker != null)
                            currentMarker = marker

                        if (!bitmap.isRecycled) bitmap.recycle()
                    }
                    if (currentSearchPlace != null) {
                        val bitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.icon_map_search)


                        val markOption = MarkerOptions().position(
                            LatLng(
                                currentSearchPlace!!.latitude,currentSearchPlace!!.longitude
                            )
                        ).icon(BitmapDescriptorFactory.fromBitmap(bitmap))

                        val marker = mMap?.addMarker(markOption!!)
                        marker?.tag = SEARCHTAG
                        if (!bitmap.isRecycled) bitmap.recycle()
                    }


                    //添加当前图标位置
                    val markOption = MarkerOptions().position(
                        LatLng(
                            activity!!.getLat().toDouble(),
                            activity!!.getLng().toDouble()
                        )
                    ).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(
                                resources,
                                R.drawable.location
                            )
                        )
                    )
                    mMap?.addMarker(markOption)


                }
                canLoadMore = !data.isNullOrEmpty()
                dismissLoading()
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<StationListBean>>> {
                return SingletonFactory.apiService.homeSearch(mHomeSearch)
            }
        }
    }


    fun setMapsLogoShow(statsData: List<Status>?, statsIdle: List<Status>?): Bitmap {
        return if (statsData!!.isNotEmpty()) {
            BitmapFactory.decodeResource(resources, R.drawable.mark_select)
        } else {
            if (statsIdle!!.isNotEmpty()) {
                BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker_ldle_ok)
            } else {
                BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker_offline_ok)
            }
        }
    }

    fun setMapsLogoDlss(statsData: List<Status>?, statsIdle: List<Status>?): Bitmap {
        return if (statsData!!.isNotEmpty()) {
            BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker)
        } else {
            if (statsIdle!!.isNotEmpty()) {
                BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker_ldle)
            } else {
                BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker_offline)
            }
        }
    }

    fun setMapsOnclickLogoShow() {

        allList.apply {
            this.forEachIndexed { index, gunlist ->
                if (gunlist.stationPk == currentMarker?.tag) {
                    val statsData =
                        gunlist.connectorStatus?.filter { its -> its.status == "Charging" }
                    val statsIdle =
                        gunlist.connectorStatus?.filter { its -> its.status == "Idle" }
                    if (statsData!!.isNotEmpty()) {
                        currentMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                    } else {
                        if (statsIdle!!.isNotEmpty()) {
                            currentMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_ldle))
                        } else {
                            currentMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_offline))
                        }
                    }
                }
            }
        }


    }

    override fun onLocationChanged(p0: Location?) {


    }


}