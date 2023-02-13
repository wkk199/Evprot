package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ViewAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.PlaceBean
import com.evport.businessapp.ui.page.adapter.PlacePredictionAdapter
import com.evport.businessapp.utils.ACache
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFetchPlace
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ProgrammaticAutocompleteToolbarActivity : AppCompatActivity() {
    var aCache: ACache? = null
    private val handler = Handler()
    private val adapter = PlacePredictionAdapter()
    private val adapter1 = PlacePredictionAdapter()

    private lateinit var placesClient: PlacesClient
    private var sessionToken: AutocompleteSessionToken? = null

    private lateinit var viewAnimator: ViewAnimator
    private lateinit var progressBar: ProgressBar
    private lateinit var empty_rl: RelativeLayout
    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var search_empty: RelativeLayout
    private var search_edit_text: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programmatic_autocomplete)

        aCache = ACache.get(this)
        placesClient = Places.createClient(this)
        sessionToken = AutocompleteSessionToken.newInstance()
        // Initialize members


        search_edit_text = findViewById(R.id.search_edit_text)
        viewAnimator = findViewById(R.id.view_animator)
        progressBar = findViewById(R.id.progress_bar)
        empty_rl = findViewById(R.id.empty_rl)
        search_empty = findViewById(R.id.search_empty)






        recyclerView1 = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager1 = LinearLayoutManager(this)
        recyclerView1.layoutManager = layoutManager1
        recyclerView1.adapter = adapter1



        initRecyclerView()
        initSearchView()
        initHistory()
    }

    private fun initHistory() {
        val historyContent = aCache!!.getAsString("history")
        if (!historyContent.isNullOrBlank()) {
            recyclerView1.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            val listType = object : TypeToken<ArrayList<PlaceBean>>() {}.type
            historyList = Gson().fromJson(historyContent, listType)
            historyList.forEach {
                adapter1.setPredictions(it)
            }
            empty_rl.visibility = View.GONE


        } else {
            empty_rl.visibility = View.VISIBLE
            recyclerView1.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        Log.e(TAG, "initRecyclerView: "+adapter1.itemCount )
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
                handler.postDelayed({ getPlacePredictions(keyWord) }, 300)
            } else {

                recyclerView1.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                empty_rl.visibility = View.GONE
                search_empty.visibility = View.GONE
                if (adapter1.itemCount <= 0) {
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
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        adapter.onPlaceClickListener = { item ->
            Log.e(TAG, "initRecyclerView: " + item.name)
            Log.e(TAG, "initRecyclerView:----- " + item.latLng)
            var intent = Intent()
            intent.putExtra("name", item.name)
            for (i in 0 until historyList.size) {
                if (historyList[i].name.isNullOrBlank()) {
                    historyList.removeAt(i)
                }
            }
            var count = 0;
            historyList.forEach {
                if (it.id == item.id) {
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

            this@ProgrammaticAutocompleteToolbarActivity.setResult(100, intent)
            this@ProgrammaticAutocompleteToolbarActivity.finish()
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
                adapter.setPredictions(
                    PlaceBean(
                        response.place.id!!,
                        response.place.name!!,
                        response.place.address!!,
                        response.place.address!!
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }


    private fun getPlacePredictions(query: String) {
        recyclerView1.visibility = View.GONE
        adapter.setPredictions()
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


    companion object {
        private val TAG = ProgrammaticAutocompleteToolbarActivity::class.java.simpleName
    }

}
