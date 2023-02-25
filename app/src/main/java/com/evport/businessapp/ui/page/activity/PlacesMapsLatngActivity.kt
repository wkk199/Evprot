package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlt.networklibs.NetType
import com.carlt.networklibs.NetworkManager
import com.carlt.networklibs.annotation.NetWork
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.base.LatLng
import com.evport.businessapp.ui.base.PlaceBean
import com.evport.businessapp.ui.page.adapter.PlaceHistoryAdapter
import com.evport.businessapp.ui.page.adapter.PlacePredictionAdapter
import com.evport.businessapp.ui.state.MainActivityViewModel
import com.evport.businessapp.utils.ACache
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFetchPlace
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_search_map.*
import kotlinx.android.synthetic.main.fragment_home_map.*
import kotlinx.coroutines.launch

class PlacesMapsLatngActivity : BaseActivity() {
    var aCache: ACache? = null
    private val handler = Handler()
    private val predictionAdapter = PlacePredictionAdapter()
    private val historyAdapter = PlaceHistoryAdapter()

    private lateinit var placesClient: PlacesClient
    private var sessionToken: AutocompleteSessionToken? = null

    private lateinit var viewAnimator: ViewAnimator
    private lateinit var progressBar: ProgressBar
    private lateinit var empty_rl: RelativeLayout
    private lateinit var cancel: TextView
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var search_empty: RelativeLayout
    private var search_edit_text: EditText? = null
    private var close_iv: ImageView? = null


    override fun initViewModel() {
        mMainActivityViewModel = getActivityViewModel(MainActivityViewModel::class.java)
    }

    private var mMainActivityViewModel: MainActivityViewModel? = null

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(
            R.layout.activity_programmatic_autocomplete,
            mMainActivityViewModel
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_programmatic_autocomplete)
        //注册
        NetworkManager.getInstance().registerObserver(this)
        aCache = ACache.get(this)
        placesClient = Places.createClient(this)
        sessionToken = AutocompleteSessionToken.newInstance()
        // Initialize members


        search_edit_text = findViewById(R.id.search_edit_text)
        viewAnimator = findViewById(R.id.view_animator)
        progressBar = findViewById(R.id.progress_bar)
        empty_rl = findViewById(R.id.empty_rl)
        search_empty = findViewById(R.id.search_empty)
        cancel = findViewById(R.id.cancel)
        close_iv = findViewById(R.id.close_iv)




        recyclerView1 = findViewById(R.id.recyclerView)
        val layoutManager1 = LinearLayoutManager(this)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = historyAdapter
        historyAdapter.setCallBack {
            historyAdapter.setPredictions()
            aCache!!.put("history", "")
            empty_rl.visibility = View.VISIBLE
            search_empty.visibility = View.GONE

        }

        historyAdapter.setonPlaceClickListener {
            closeKeyWord()


            val intent = Intent()
            intent.putExtra("name", it.name)
            intent.putExtra("latitude", it.latLng.latitude.toString())
            intent.putExtra("longitude", it.latLng.longitude.toString())
            this@PlacesMapsLatngActivity.setResult(100, intent)
            this@PlacesMapsLatngActivity.finish()
        }

        initRecyclerView()
        initSearchView()
        initHistory()





        cancel.setOnClickListener {
            closeKeyWord()
            finish()
        }

        search_edit_text?.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            requestFocusFromTouch()
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s!!.isNotEmpty()) {
                        close_iv!!.visibility = View.VISIBLE
                    } else {
                        close_iv!!.visibility = View.INVISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
        }


        close_iv!!.setOnClickListener {
            search_edit_text!!.setText("")
            getPlacePredictions("")



        }



    }


    private fun initHistory() {
        val historyContent = aCache!!.getAsString("history")
        if (!historyContent.isNullOrBlank()) {
            recyclerView1.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            val listType = object : TypeToken<ArrayList<PlaceBean>>() {}.type
            historyList = Gson().fromJson(historyContent, listType)
            historyList.add(historyList.size, PlaceBean("", "", "", LatLng()))
            historyList.forEach {
                historyAdapter.setPredictions(it)
            }
            empty_rl.visibility = View.GONE


        } else {
            empty_rl.visibility = View.VISIBLE
            recyclerView1.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }


    private fun initSearchView() {

        search_edit_text?.doOnTextChanged { _, _, _, _ ->
            // 禁止EditText输入空格
            val keyWord = search_edit_text!!.text.toString()
            /*val inputquery = InputtipsQuery(keyWord, "")
            inputquery.cityLimit = true

            val inputTips = Inputtips(this, inputquery)
            inputTips.setInputtipsListener(this)
            inputTips.requestInputtipsAsyn()*/
            if (!keyWord.isNullOrBlank()) {
                progressBar.isIndeterminate = true

                // Cancel any previous place prediction requests
                handler.removeCallbacksAndMessages(null)

                // Start a new place prediction request in 300 ms
                handler.postDelayed({ getPlacePredictions(keyWord) }, 10)
            } else {

                recyclerView1.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                empty_rl.visibility = View.GONE
                search_empty.visibility = View.GONE
                if (historyAdapter.itemCount <= 0) {
                    empty_rl.visibility = View.VISIBLE
                }

            }


        }


    }

    var historyList = arrayListOf<PlaceBean>()
    private fun initRecyclerView() {

        recyclerView = findViewById(R.id.recyclerView_history)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = predictionAdapter
        predictionAdapter.onPlaceClickListener = { item ->
            val intent = Intent()
            intent.putExtra("name", item.name)
            intent.putExtra("latitude", item.latLng.latitude.toString())
            intent.putExtra("longitude", item.latLng.longitude.toString())
            for (i in 0 until historyList.size) {
                if (historyList[i].name.isNullOrBlank()) {
                    historyList.removeAt(i)
                }
            }
            var count = 0;
            var name :PlaceBean? = null
            historyList.forEach {
                if (it.id == item.id) {
                    name = it
                    count++
                }
            }
            if (name!=null) {
                historyList.remove(name)
                historyList.add(0,item)
            }
            if (count == 0) {
                historyList.add(0, item)
                if (historyList.size > 10) {
                    historyList.removeAt(10)
                }
            }
            aCache!!.put("history", Gson().toJson(historyList))
            closeKeyWord()
            this@PlacesMapsLatngActivity.setResult(100, intent)
            this@PlacesMapsLatngActivity.finish()
        }


    }


    fun placeFields(string: String) {

        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        )
        lifecycleScope.launch {
            try {
                val response = placesClient.awaitFetchPlace(string, placeFields)
                predictionAdapter.contentS = search_edit_text!!.text.toString()



                predictionAdapter.setPredictions(
                    PlaceBean(
                        response.place.id!!,
                        response.place.name!!,
                        response.place.address!!,
                        LatLng(response.place.latLng!!.latitude, response.place.latLng!!.longitude)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


    private fun getPlacePredictions(query: String) {
        recyclerView1.visibility = View.GONE
        predictionAdapter.setPredictions()
        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        val newRequest = FindAutocompletePredictionsRequest
            .builder()
//            .setOrigin(LatLng(-33.8749937, 151.2041382))
            .setSessionToken(sessionToken)
//            .setLocationBias(bias)
            .setTypeFilter(TypeFilter.GEOCODE)
            .setQuery(query)
//            .setCountries("es")

            .build()

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions

            if (predictions.isEmpty()) {
                progressBar.isIndeterminate = false
                search_empty.visibility = View.VISIBLE
                viewAnimator.displayedChild = 0
                return@addOnSuccessListener
            }
            recyclerView.visibility = View.VISIBLE
            empty_rl.visibility = View.GONE
            search_empty.visibility = View.GONE
            predictions.forEach {
                placeFields(it.placeId)
            }

            progressBar.isIndeterminate = false
            viewAnimator.displayedChild = if (predictions.isEmpty()) 0 else 1
        }.addOnFailureListener { exception: Exception? ->
            progressBar.isIndeterminate = false
            if (exception is ApiException) {
                Log.e(TAG, "Place not found: " + exception.statusCode)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showKeyWord()
    }

    override fun onPause() {
        super.onPause()
        closeKeyWord()
    }


    override fun onDestroy() {
        super.onDestroy()

        //注销
        NetworkManager.getInstance().unRegisterObserver(this)


    }

    companion object {
        private val TAG = PlacesMapsLatngActivity::class.java.simpleName
    }

    @NetWork(netType = NetType.AUTO)
    fun network(netType: NetType?) {
        when (netType) {
            NetType.WIFI -> {
                Log.e("hmm----", "wifi")
                empty_network.visibility = View.GONE
            }
            NetType.CMNET, NetType.CMWAP -> {
                Log.e("hmm----", "4G")
                empty_network.visibility = View.GONE
            }
            NetType.AUTO -> {}
            NetType.NONE -> {
                Log.e("hmm----", "无网络")
                empty_network.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

}
